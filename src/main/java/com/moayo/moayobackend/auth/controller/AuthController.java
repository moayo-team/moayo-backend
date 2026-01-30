package com.moayo.moayobackend.auth.controller;

import com.moayo.moayobackend.auth.dto.GoogleUserInfoResponseDto;
import com.moayo.moayobackend.auth.dto.TokenResponseDto;
import com.moayo.moayobackend.auth.service.GoogleOAuthService;
import com.moayo.moayobackend.auth.service.JwtProvider;
import com.moayo.moayobackend.global.response.ApiResponse;
import com.moayo.moayobackend.user.entity.User;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

/**
 * 유저 카테고리 - Auth API
 * 1) GET  /api/v1/auth/oauth/google
 * 2) GET  /api/v1/auth/oauth/google/callback
 * 3) POST /api/v1/auth/token/refresh
 * 4) POST /api/v1/auth/logout
 */

@Tag(name="구글 소셜 로그인 및 토큰 관리", description = "구글 소셜 로그인, 토큰 관리 API, 로그아웃")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final GoogleOAuthService googleOAuthService;
    private final JwtProvider jwtProvider;

    @Value("${app.front.redirect-url}")
    private String frontRedirectUrl;

    @Value("${app.cookie.secure:false}")
    private boolean cookieSecure;

    private static final String STATE_COOKIE = "oauth_state";
    private static final String REFRESH_COOKIE = "refresh_token";

    @Operation(summary = "소셜 로그인 시작", description = "구글 로그인 동의 화면으로 리다이렉트합니다. CSRF 방지용 state 쿠키가 생성됩니다.")
    @GetMapping("/oauth/google")
    public RedirectView googleStart(HttpServletResponse res) {
        String state = googleOAuthService.createState();

        ResponseCookie stateCookie = ResponseCookie.from(STATE_COOKIE, state)
                .path("/")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .maxAge(300)
                .build();
        res.addHeader("Set-Cookie", stateCookie.toString());

        return new RedirectView(googleOAuthService.buildAuthorizeUrl(state));
    }

    @Operation(summary = "소셜 로그인 콜백", description = "구글 로그인 성공 후 리다이렉트되는 경로입니다. 유저 정보를 저장하고 Refresh Token(쿠키)을 발급합니다.")
    @GetMapping("/oauth/google/callback")
    public RedirectView googleCallback(
            @RequestParam String code,
            @RequestParam String state,
            HttpServletRequest req,
            HttpServletResponse res
    ) {
        System.out.println("1. 콜백 진입 성공! code: " + code);

        try {
            String savedState = readCookie(req, STATE_COOKIE);
            System.out.println(">>> 쿠키에서 가져온 state: " + savedState);
            System.out.println(">>> 구글이 보내준 state: " + state);

            if (savedState == null || !savedState.equals(state)) {
                System.out.println(">>> [경고] state 불일치 발생!");
                System.out.println(">>> savedState: " + savedState + ", 받은 state: " + state);
//                return new RedirectView(frontRedirectUrl + "?error=state_mismatch");
            }
            GoogleUserInfoResponseDto info = googleOAuthService.fetchUserInfoByCode(code);
            System.out.println("2. 구글 유저 정보 획득 완료: " + info.email());

            User user = googleOAuthService.upsertGoogleUser(info);
            System.out.println("3. DB 저장/업데이트 완료: " + user.getId());

            // Access Token, refresh 토큰 생성 및 리다이렉트
            String access = jwtProvider.createAccessToken(user.getId());
            String refresh = jwtProvider.createRefreshToken(user.getId());

            ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_COOKIE, refresh)
                    .path("/")
                    .httpOnly(true)
                    .secure(cookieSecure)
                    .sameSite("Lax")
                    .maxAge(60L * 60 * 24 * 14)
                    .build();
            res.addHeader("Set-Cookie", refreshCookie.toString());

            // state 쿠키 제거
            ResponseCookie deleteState = ResponseCookie.from(STATE_COOKIE, "")
                    .path("/")
                    .httpOnly(true)
                    .secure(cookieSecure)
                    .sameSite("Lax")
                    .maxAge(0)
                    .build();
            res.addHeader("Set-Cookie", deleteState.toString());

            // URL에 Access Token을 쿼리 스트링으로 붙여서 리다이렉트
            String redirectUrlWithToken = frontRedirectUrl + "?accessToken=" + access;

            return new RedirectView(redirectUrlWithToken);
        } catch (Exception e) {
            e.printStackTrace();
            return new RedirectView(frontRedirectUrl + "?error=server_error");
        }
    }

    @Operation(summary = "토큰 재발급", description = "HttpOnly 쿠키에 저장된 Refresh Token을 사용하여 새로운 Access Token을 발급받습니다.")
    @PostMapping("/token/refresh")
    public ApiResponse<TokenResponseDto> refresh(HttpServletRequest req) {
        String refreshToken = readCookie(req, REFRESH_COOKIE);
        if (refreshToken == null) {
            return ApiResponse.fail("AUTH-401", "refresh 토큰이 없습니다.");
        }

        Claims claims = jwtProvider.parse(refreshToken);
        if (!"refresh".equals(claims.get("typ"))) {
            return ApiResponse.fail("AUTH-401", "refresh 토큰이 아닙니다.");
        }

        Long userId = Long.valueOf(claims.getSubject());
        String access = jwtProvider.createAccessToken(userId);

        return ApiResponse.ok("SUCCESS-200", "요청에 성공했습니다.", new TokenResponseDto(access));
    }

    @Operation(summary = "로그아웃", description = "서버에 저장된 Refresh Token 쿠키를 삭제하여 로그아웃 처리합니다.")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletResponse res) {
        ResponseCookie deleteRefresh = ResponseCookie.from(REFRESH_COOKIE, "")
                .path("/")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .maxAge(0)
                .build();
        res.addHeader("Set-Cookie", deleteRefresh.toString());
        return ApiResponse.ok("SUCCESS-200", "요청에 성공했습니다.", null);
    }

    private String readCookie(HttpServletRequest req, String name) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }
}
