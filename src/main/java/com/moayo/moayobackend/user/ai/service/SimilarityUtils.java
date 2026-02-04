package com.moayo.moayobackend.user.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class SimilarityUtils {

    private static final ObjectMapper om = new ObjectMapper();

    public static String toJson(List<Double> v) {
        try {
            return om.writeValueAsString(v);
        } catch (Exception e) {
            throw new IllegalArgumentException("vector json serialize failed", e);
        }
    }

    public static List<Double> fromJson(String json) {
        try {
            return om.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("vector json parse failed", e);
        }
    }

    public static double cosine(List<Double> a, List<Double> b) {
        if (a.size() != b.size()) throw new IllegalArgumentException("vector dim mismatch");

        double dot = 0, na = 0, nb = 0;
        for (int i = 0; i < a.size(); i++) {
            double x = a.get(i), y = b.get(i);
            dot += x * y;
            na += x * x;
            nb += y * y;
        }
        if (na == 0 || nb == 0) return 0;
        return dot / (Math.sqrt(na) * Math.sqrt(nb));
    }
}
