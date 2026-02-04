package com.moayo.moayobackend.user.ai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
//@Primary
public class OpenAIEmbeddingProvider implements EmbeddingProvider {

    @Value("${ai.server.api-key}")
    private String apiKey;

    @Value("${ai.server.base-url}")
    private String baseUrl;

    @Value("${ai.server.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<Double> embed(String text) {
        String url = baseUrl + "/embeddings";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("input", text);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            List<Map<String, Object>> data = (List<Map<String, Object>>) response.getBody().get("data");
            return (List<Double>) data.get(0).get("embedding");
        } catch (Exception e) {
            throw new RuntimeException("OpenAI API 호출 중 오류 발생: " + e.getMessage());
        }
    }

    @Override
    public int dimension() {
        // text-embedding-3-small 모델은 1536 차원입니다.
        return 1536;
    }
}