package com.moayo.moayobackend.global.config;

import com.moayo.moayobackend.auth.filter.JwtAuthFilter;
import com.moayo.moayobackend.auth.service.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;

// Security 설정
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // CorsConfigurationSource Bean 사용
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/**",      // 소셜 로그인 시작, 콜백, 토큰 재발급 등
                                "/swagger-ui/**",       // 스웨거 UI
                                "/v3/api-docs/**",      // 스웨거 문서 데이터
                                "/ws-chat/**"           // WebSocket 핸드셰이크 허용
                        ).permitAll()
                        .anyRequest().authenticated()
                )
//                .oauth2Login(oauth2 -> oauth2
//                        .defaultSuccessUrl("/api/v1/auth/oauth/google/success", true)
//                )
                .addFilterBefore(new JwtAuthFilter(jwtProvider), SecurityContextHolderFilter.class)
                .build();
    }
}
