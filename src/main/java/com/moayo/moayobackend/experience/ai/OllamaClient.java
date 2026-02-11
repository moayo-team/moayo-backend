package com.moayo.moayobackend.experience.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OllamaClient {

    private final OllamaProperties props;

    private WebClient webClient() {
        return WebClient.builder()
                .baseUrl(props.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public String generate(String prompt) {
        // Ollama Generate API: POST /api/generate
        Map<String, Object> body = Map.of(
                "model", props.getModel(),
                "prompt", prompt,
                "stream", false
        );

        return webClient().post()
                .uri("/api/generate")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofMillis(props.getTimeoutMs()))
                .onErrorResume(e -> Mono.error(new IllegalStateException("Ollama 호출 실패: " + e.getMessage())))
                .map(res -> String.valueOf(res.getOrDefault("response", "")))
                .block();
    }
}
