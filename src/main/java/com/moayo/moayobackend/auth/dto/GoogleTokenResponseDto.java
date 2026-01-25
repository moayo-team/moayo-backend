package com.moayo.moayobackend.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// 구글 token endpoint 응답 매핑
public record GoogleTokenResponseDto(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("expires_in") Long expiresIn,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("scope") String scope,
        @JsonProperty("id_token") String idToken
) {}
