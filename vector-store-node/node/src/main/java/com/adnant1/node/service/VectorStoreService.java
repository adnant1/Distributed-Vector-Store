package com.adnant1.node.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import com.adnant1.node.client.OpenAIClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;

@Service
public class VectorStoreService {
    private final OpenAIClient openAIClient;
    private final ElasticsearchClient esClient;

    public VectorStoreService(OpenAIClient openAIClient, ElasticsearchClient esClient) {
        this.openAIClient = openAIClient;
        this.esClient = esClient;
    }

    // Store the text and its embedding in Elasticsearch
    public void indexVector(String id, String text) {
        try {
            // Get embedding from OpenAI
            List<Float> embedding = openAIClient.getEmbedding(text);

            // Create a document to index
            Map<String, Object> document = new HashMap<>();
            document.put("id", id);
            document.put("text", text);
            document.put("embedding", embedding);

            // Index the document in Elasticsearch
            esClient.index(i -> i
                .index("vectors")
                .id(id)
                .document(document)
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to index vector in Elasticsearch", e);
        }
    }

    // Search for similar texts based on the input query
    public List<SearchResult> querySimilar(String queryText, int topK) {

    }
}
