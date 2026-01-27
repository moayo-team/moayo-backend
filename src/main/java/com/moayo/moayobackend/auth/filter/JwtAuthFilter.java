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

    public JwtAuthFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        System.out.println(">>> Swagger에서 넘어온 Header: [" + header + "]");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

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
                    System.out.println(">>> typ 클레임이 'access'가 아닙니다: " + c.get("typ"));
                }
            } catch (Exception e) {
                System.out.println(">>> JWT 인증 에러 발생: " + e.getMessage());
            }
        } else {
            System.out.println(">>> Authorization 헤더가 없거나 형식이 올바르지 않습니다.");
        }

        chain.doFilter(request, response);
    }
}
