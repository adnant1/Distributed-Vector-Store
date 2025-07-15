package com.adnant1.node.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

public class OpenAIClient {
    
    private final WebClient webClient;

    public OpenAIClient(@Value("${openai.api-key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public List<Float> getEmbedding(String input) {

    }

}
