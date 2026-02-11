package com.moayo.moayobackend.experience.service;

import com.moayo.moayobackend.experience.ai.OllamaClient;
import com.moayo.moayobackend.experience.dto.response.ExperienceAiDraftResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Slf4j
@Component
public class AiServerClient {

    private final WebClient webClient;
    private final OllamaClient ollamaClient;

    @Value("${ai.openai.enabled:true}")
    private boolean openAiEnabled;

    public AiServerClient(
            @Value("${ai.openai.base-url:http://localhost:8000}") String baseUrl,
            @Value("${ai.openai.api-key:}") String apiKey,
            OllamaClient ollamaClient
    ) {
        this.ollamaClient = ollamaClient;

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(15));

        WebClient.Builder builder = WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        if (apiKey != null && !apiKey.isBlank()) {
            builder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
        }

        this.webClient = builder.build();
    }

    public String generateExperienceDraftText(String prompt) {
        if (!openAiEnabled) {
            return ollamaClient.generate(prompt);
        }

        // 기존 AI 서버 방식 유지
        ExperienceAiDraftResponse res = generateExperienceDraft(prompt, "");
        return (res == null || res.draft() == null) ? "" : res.draft();
    }

    public ExperienceAiDraftResponse generateExperienceDraft(String prompt, String context) {
        AiServerDraftRequest body = new AiServerDraftRequest(prompt, context);

        try {
            return webClient.post()
                    .uri("/v1/experience/draft")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(ExperienceAiDraftResponse.class)
                    .block(Duration.ofSeconds(20));

        } catch (WebClientResponseException e) {
            log.error("AI server error: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new IllegalStateException("AI server error: " + e.getStatusCode());

        } catch (Exception e) {
            log.error("AI server call failed", e);
            throw new IllegalStateException("AI server call failed");
        }
    }

    public record AiServerDraftRequest(String prompt, String context) {}
}
