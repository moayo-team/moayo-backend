package com.moayo.moayobackend.user.ai.service;

import java.util.List;

public interface EmbeddingProvider {
    List<Double> embed(String text);
    int dimension();
}
