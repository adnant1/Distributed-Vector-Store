package com.adnant1.node.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import com.adnant1.node.client.OpenAIClient;
import com.adnant1.node.model.SearchResult;
import co.elastic.clients.elasticsearch.ElasticsearchClient;

@Service
public class VectorStoreService {
    private final OpenAIClient openAIClient;
    private final ElasticsearchClient esClient;

    public VectorStoreService(OpenAIClient openAIClient, ElasticsearchClient esClient) {
        this.openAIClient = openAIClient;
        this.esClient = esClient;
    }

    // Create the Elasticsearch index for storing vectors
    @PostConstruct
    public void setupVectorIndex() {
        String indexName = "vectors";

        try {
            boolean exists = esClient.indices().exists(b -> b.index(indexName)).value();

            if (!exists) {
                esClient.indices().create(c -> c
                    .index(indexName)
                    .mappings(m -> m
                        .properties("id", p -> p.keyword(k -> k))
                        .properties("text", p -> p.text(t -> t))
                        .properties("embedding", p -> p
                            .denseVector(dv -> dv
                                .dims(1536)
                                .index(true)
                                .similarity("cosine")
                                .indexOptions(io -> io.type("hnsw"))
                            )
                        )
                    )
                );

                System.out.println("Created index: " + indexName);
            } else {
                System.out.println("Index already exists: " + indexName);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to set up Elasticsearch index", e);
        }
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
    public List<SearchResult> querySimilar(String queryText, long topK) {
        List<Float> queryVector = openAIClient.getEmbedding(queryText);

        try {
            // Perform KNN search in Elasticsearch
            var response = esClient.search(s -> s
                    .index("vectors")
                    .knn(k -> k
                        .field("embedding")
                        .k(topK)
                        .numCandidates(100L)
                        .queryVector(queryVector)
                    ),
                    Map.class // raw doc as Map<String, Object>
            );

            // Parse search results
            List<SearchResult> results = new ArrayList<>();
            for (var hit : response.hits().hits()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> source = (Map<String, Object>) hit.source();
                Double score = hit.score();

                String id = (String) source.get("id");
                String text = (String) source.get("text");

                results.add(new SearchResult(id, text, score));
            }

            return results;

        } catch (Exception e) {
            throw new RuntimeException("Failed to execute KNN search", e);
        }
    }
}
