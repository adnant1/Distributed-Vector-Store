package com.adnant1.coordinator.service;

import java.util.List;

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

    public List<QueryResult> query(QueryRequest request) {

    }   
}
