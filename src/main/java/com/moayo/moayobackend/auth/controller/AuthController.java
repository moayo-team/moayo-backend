package com.moayo.moayobackend.auth.controller;

import com.moayo.moayobackend.auth.dto.GoogleUserInfoResponseDto;
import com.moayo.moayobackend.auth.dto.TokenResponseDto;
import com.moayo.moayobackend.auth.service.GoogleOAuthService;
import com.moayo.moayobackend.auth.service.JwtProvider;
import com.moayo.moayobackend.global.response.ApiResponse;
import com.moayo.moayobackend.user.entity.User;
import io.jsonwebtoken.Claims;
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

    /**
     * 구글 로그인 시작(동의 화면으로 리다이렉트)
     * - CSRF 방지용 state를 HttpOnly 쿠키에 저장
     */
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

    /**
     * 구글 콜백
     * - state 검증 -> user upsert -> refresh 쿠키 세팅 -> 프론트로 리다이렉트
     */
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
            if (savedState == null || !savedState.equals(state)) {
                return new RedirectView(frontRedirectUrl + "?error=state_mismatch");
            }
            GoogleUserInfoResponseDto info = googleOAuthService.fetchUserInfoByCode(code);
            System.out.println("2. 구글 유저 정보 획득 완료: " + info.email());

            User user = googleOAuthService.upsertGoogleUser(info);
            System.out.println("3. DB 저장/업데이트 완료: " + user.getId());

            // refresh 토큰은 HttpOnly 쿠키에 저장
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
            return new RedirectView(frontRedirectUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return new RedirectView(frontRedirectUrl + "?error=server_error");
        }
    }

    /**
     * 토큰 재발급
     * - refresh 쿠키로 access token 재발급
     */
    @PostMapping("/token/refresh")
    public ApiResponse<TokenResponseDto> refresh(HttpServletRequest req) {
        String refreshToken = readCookie(req, REFRESH_COOKIE);
        if (refreshToken == null) {
            return ApiResponse.fail("AUTH-401", "refresh 토큰이 없어.");
        }

        Claims claims = jwtProvider.parse(refreshToken);
        if (!"refresh".equals(claims.get("typ"))) {
            return ApiResponse.fail("AUTH-401", "refresh 토큰이 아니야.");
        }

        Long userId = Long.valueOf(claims.getSubject());
        String access = jwtProvider.createAccessToken(userId);

        return ApiResponse.ok("SUCCESS-200", "요청에 성공했습니다.", new TokenResponseDto(access));
    }

    /**
     * 로그아웃
     * - refresh 쿠키 삭제
     */
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
