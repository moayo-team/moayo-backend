package com.moayo.moayobackend.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwtSchemeName = "jwtAuth";

        // API 요청 시 보안 요구사항 설정
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        // 보안 스키마 정의 (Bearer 방식 JWT)
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .info(new Info()
                        .title("Moayo API 명세서")
                        .description("모아요 프로젝트 백엔드 API 문서입니다.")
                        .version("v1.0.0"))
                .servers(List.of(
                        new Server().url("https://moayo-backend.p-e.kr").description("운영 서버 (HTTPS)"),
                        new Server().url("http://localhost:8080").description("로컬 서버 (개발용)")
                ))
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}