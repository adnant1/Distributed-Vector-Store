package com.adnant1.coordinator.client;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.adnant1.coordinator.model.IndexRequest;
import com.adnant1.coordinator.model.QueryRequest;
import com.adnant1.coordinator.model.QueryResult;

import reactor.core.publisher.Mono;

@Component
public class VectorNodeClient {
    
    private final WebClient webClient;

    public VectorNodeClient(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    // Send index request to vector node
    public Mono<Void> sendIndexRequest(String nodeUrl, IndexRequest request) {
        return webClient.post()
                .uri(nodeUrl + "/node/index")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(e -> System.err.println("Failed to index on node " + nodeUrl + ": " + e.getMessage()));
    }

    // Send query request to vector node
    public Mono<List<QueryResult>> sendQueryRequest(String nodeUrl, QueryRequest request) {
        return webClient.post()
                .uri(nodeUrl + "/node/query")
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(QueryResult.class)
                .collectList()
                .doOnError(e -> System.err.println("Failed to query node " + nodeUrl + ": " + e.getMessage()));
    }
}
