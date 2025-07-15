package com.adnant1.node.service;

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
        
    }

    // Search for similar texts based on the input query
    public List<SearchResult> querySimilar(String queryText, int topK) {

    }
}
