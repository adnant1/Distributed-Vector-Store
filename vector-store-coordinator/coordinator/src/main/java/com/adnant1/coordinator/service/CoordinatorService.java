package com.adnant1.coordinator.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.adnant1.coordinator.client.VectorNodeClient;
import com.adnant1.coordinator.model.IndexRequest;
import com.adnant1.coordinator.model.QueryRequest;
import com.adnant1.coordinator.model.QueryResult;
import com.adnant1.coordinator.util.ConsistentHashRing;

@Service
public class CoordinatorService {

    private final ConsistentHashRing hashRing;
    private final VectorNodeClient vectorNodeClient;

    @Value("${coordinator.node-urls}")
    private List<String> nodeUrls;

    public CoordinatorService(ConsistentHashRing hashRing, VectorNodeClient vectorNodeClient) {
        this.hashRing = hashRing;
        this.vectorNodeClient = vectorNodeClient;
    }

    // Send the index request to the appropriate vector node via VectorNodeClient
    public void index(IndexRequest request) {
        String targetNode = hashRing.getNodeForId(request.getId());
        vectorNodeClient.sendIndexRequest(targetNode, request).block();
    }

    // Concurrently query all vector nodes and aggregate results
    public List<QueryResult> query(QueryRequest request) {
        List<CompletableFuture<List<QueryResult>>> futures = new ArrayList<>();

        for (String nodeUrl : nodeUrls) {
            CompletableFuture<List<QueryResult>> future = vectorNodeClient
                    .sendQueryRequest(nodeUrl, request)
                    .toFuture()
                    .exceptionally(ex -> {
                        System.err.println("Query to node " + nodeUrl + " failed: " + ex.getMessage());
                        return List.of();
                    });
            futures.add(future);
        }

        List<QueryResult> mergedResults = futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .sorted(Comparator.comparingDouble(QueryResult::getScore).reversed())
                .limit(request.getTopK())
                .collect(Collectors.toList());
        
        return mergedResults;
    }
}
