package com.adnant1.node.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.adnant1.node.model.EmbeddingRequest;
import com.adnant1.node.model.EmbeddingResponse;

@Component
public class OpenAIClient {
    
    private final WebClient webClient;

    public OpenAIClient(@Value("${openai.api-key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    // Method to get embedding from OpenAI API
    public List<Float> getEmbedding(String input) {
        EmbeddingRequest request = new EmbeddingRequest("text-embedding-ada-002", input);
        EmbeddingResponse response = webClient.post()
                .uri("/embeddings")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(EmbeddingResponse.class)
                .block();

        if (response != null && !response.getData().isEmpty()) {
            return response.getData().get(0).getEmbedding();
        } else {
            throw new RuntimeException("Failed to get embedding from OpenAI");
        }
    }
}
