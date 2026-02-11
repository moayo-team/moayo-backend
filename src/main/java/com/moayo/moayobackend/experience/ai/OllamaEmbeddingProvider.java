package com.moayo.moayobackend.experience.ai;

import com.moayo.moayobackend.user.ai.service.EmbeddingProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OllamaEmbeddingProvider implements EmbeddingProvider {

    @Value("${ai.ollama.base-url:http://localhost:11434}")
    private String baseUrl;

    @Value("${ai.ollama.model:nomic-embed-text}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<Double> embed(String text) {
        String url = baseUrl + "/api/embeddings";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("prompt", text);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            Map<String, Object> resBody = response.getBody();
            if (resBody == null) throw new RuntimeException("응답 body가 비었습니다.");

            Object embeddingObj = resBody.get("embedding");
            if (!(embeddingObj instanceof List<?> list)) {
                throw new RuntimeException("embedding 형식이 예상과 다름: " + resBody);
            }

            List<Double> embedding = new ArrayList<>();
            for (Object v : list) embedding.add(((Number) v).doubleValue());
            return embedding;

        } catch (Exception e) {
            throw new RuntimeException("Ollama embeddings 호출 오류: " + e.getMessage(), e);
        }
    }

    @Override
    public int dimension() {
        // nomic-embed-text는 보통 768 차원(버전에 따라 다를 수 있음)
        // 중요: dimension()을 실제로 사용하는 로직이 있으면,
        // 한 번 임베딩 받아서 길이 찍어보고 맞춰야 함.
        return 768;
    }
}
