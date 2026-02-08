package com.moayo.moayobackend.auth.service;

import com.moayo.moayobackend.auth.dto.GoogleTokenResponseDto;
import com.moayo.moayobackend.auth.dto.GoogleUserInfoResponseDto;
import com.moayo.moayobackend.user.entity.User;
import com.moayo.moayobackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

/**
 * 구글 OAuth 흐름 담당
 * - authorize URL 생성
 * - code -> token 교환
 * - userinfo 조회
 * - users upsert
 */
@Service
@RequiredArgsConstructor
public class GoogleOAuthService {

    private final WebClient webClient = WebClient.builder().build();
    private final UserRepository userRepository;

    @Value("${app.oauth.google.client-id}")
    private String clientId;

    @Value("${app.oauth.google.client-secret}")
    private String clientSecret;

    @Value("${app.oauth.google.redirect-uri}")
    private String redirectUri;

    @Value("${app.oauth.google.scope}")
    private String scope;

    public String createState() {
        return UUID.randomUUID().toString();
    }

    public String buildAuthorizeUrl(String state) {
        return "https://accounts.google.com/o/oauth2/v2/auth"
                + "?response_type=code"
                + "&client_id=" + enc(clientId)
                + "&redirect_uri=" + enc(redirectUri)
                + "&scope=" + enc(scope)
                + "&state=" + enc(state)
                + "&access_type=offline"
                + "&prompt=consent";
    }

    public GoogleUserInfoResponseDto fetchUserInfoByCode(String code) {
        GoogleTokenResponseDto token = webClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("code", code)
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("redirect_uri", redirectUri)
                        .with("grant_type", "authorization_code"))
                .retrieve()
                .bodyToMono(GoogleTokenResponseDto.class)
                .block();

        if (token == null || token.accessToken() == null) {
            throw new IllegalStateException("구글 토큰 교환 실패");
        }
        System.out.println("### Google AccessToken: " + token.accessToken());

        GoogleUserInfoResponseDto userInfo = webClient.get()
                .uri("https://openidconnect.googleapis.com/v1/userinfo")
                .headers(h -> h.setBearerAuth(token.accessToken()))
                .retrieve()
                .bodyToMono(GoogleUserInfoResponseDto.class)
                .block();

        if (userInfo == null || userInfo.sub() == null) {
            throw new IllegalStateException("구글 유저 정보 조회 실패");
        }

        return userInfo;
    }

    public record UserLoginDto(User user, boolean isFirstLogin) {}

    @Transactional
    public UserLoginDto upsertGoogleUser(GoogleUserInfoResponseDto info) {
        Optional<User> userOpt = userRepository.findByOauthProviderAndOauthSub("google", info.sub());

        if (userOpt.isPresent()) {
            // 재방문자
            return new UserLoginDto(userOpt.get(), false);
        } else {
            // 첫 로그인 방문자
            User newUser = userRepository.save(User.createGoogleUser(info.sub(), info.email(), info.name()));
            return new UserLoginDto(newUser, true);
        }
    }

    private String enc(String v) {
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
    }
}
