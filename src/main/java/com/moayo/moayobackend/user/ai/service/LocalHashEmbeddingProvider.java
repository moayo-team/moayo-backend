package com.moayo.moayobackend.user.ai.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
//@Primary
public class LocalHashEmbeddingProvider implements EmbeddingProvider {

    private static final int DIM = 128;

    @Override
    public List<Double> embed(String text) {
        String normalized = (text == null ? "" : text.toLowerCase(Locale.ROOT));

        List<Double> vec = new ArrayList<>(DIM);
        for (int i = 0; i < DIM; i++) vec.add(0.0);

        for (int i = 0; i < Math.max(0, normalized.length() - 2); i++) {
            String tri = normalized.substring(i, i + 3);
            int idx = hashToIndex(tri, DIM);
            vec.set(idx, vec.get(idx) + 1.0);
        }

        // L2 정규화
        double norm = 0;
        for (double v : vec) norm += v * v;
        norm = Math.sqrt(norm);

        if (norm == 0) return vec;
        for (int i = 0; i < vec.size(); i++) vec.set(i, vec.get(i) / norm);

        return vec;
    }

    private int hashToIndex(String s, int mod) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
            int x = ((d[0] & 0xff) << 24) | ((d[1] & 0xff) << 16) | ((d[2] & 0xff) << 8) | (d[3] & 0xff);
            if (x == Integer.MIN_VALUE) x = 0;
            x = Math.abs(x);
            return x % mod;
        } catch (Exception e) {
            return Math.abs(s.hashCode()) % mod;
        }
    }

    @Override
    public int dimension() {
        return DIM;
    }
}
