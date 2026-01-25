package com.moayo.moayobackend.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 구글 userinfo 응답
 * - sub: 유저 키
 */
public record GoogleUserInfoResponseDto(
        String sub,
        String name,
        String email,
        @JsonProperty("email_verified") Boolean emailVerified,
        String picture
) {}
