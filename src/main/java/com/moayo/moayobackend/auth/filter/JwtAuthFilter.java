package com.moayo.moayobackend.auth.filter;

import com.moayo.moayobackend.auth.service.JwtProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Authorization: Bearer <accessToken> 파싱해서 인증 주입
 * - principal에 userId(Long)를 넣어 컨트롤러에서 그대로 사용
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private static final String MASTER_KEY = "moayo";

    public JwtAuthFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        System.out.println(">>> Swagger에서 넘어온 Header: [" + header + "]");
        System.out.println(">>> 요청 api : [" + request.getMethod() + "] " + request.getRequestURI() + " | Header: [" + header + "]");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (MASTER_KEY.equals(token)) {
                System.out.println(">>> [MASTER KEY] 마스터 키 인증 성공! 테스트 계정(ID: 1)으로 접속합니다.");

                var auth = new UsernamePasswordAuthenticationToken(
                        1L, // 테스트용 userId (DB에 존재하는 ID여야 함)
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                try {
                    Claims c = jwtProvider.parse(token);
                    System.out.println(">>> 파싱된 Claims: " + c);

                    if ("access".equals(c.get("typ"))) {
                        Long userId = Long.valueOf(c.getSubject());
                        System.out.println(">>> 인증 성공! userId: " + userId);

                        var auth = new UsernamePasswordAuthenticationToken(
                                userId,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER"))
                        );

                        SecurityContextHolder.getContext().setAuthentication(auth);
                    } else {
                        System.out.println(">>> 인증 실패: 토큰 타입이 'access'가 아닙니다: " + c.get("typ"));
                    }
                } catch (Exception e) {
                    System.out.println(">>> JWT 인증 에러 발생: " + e.getMessage());
                }
            }
        }

        chain.doFilter(request, response);
    }
}
