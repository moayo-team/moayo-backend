package com.moayo.moayobackend.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

/**
 * JWT 발급/파싱
 * - access: Authorization 헤더용
 * - refresh: HttpOnly 쿠키용
 */
@Component
public class JwtProvider {

    private final Key key;
    private final String issuer;
    private final long accessTtl;
    private final long refreshTtl;

    public JwtProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.access-token-ttl-seconds}") long accessTtl,
            @Value("${app.jwt.refresh-token-ttl-seconds}") long refreshTtl
    ) {
        // ⚠️ JWT_SECRET은 32자 이상 강한 랜덤 문자열
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.accessTtl = accessTtl;
        this.refreshTtl = refreshTtl;
    }

    public String createAccessToken(Long userId) {
        return build(userId, accessTtl, "access");
    }

    public String createRefreshToken(Long userId) {
        return build(userId, refreshTtl, "refresh");
    }

    private String build(Long userId, long ttlSeconds, String typ) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(String.valueOf(userId)) // subject=userId
                .claim("typ", typ)                  // access/refresh 구분
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(ttlSeconds)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public Long extractUserId(String token) {
        Claims claims = parse(token);
        String subject = claims.getSubject(); 
        return Long.valueOf(subject);
    }
}
