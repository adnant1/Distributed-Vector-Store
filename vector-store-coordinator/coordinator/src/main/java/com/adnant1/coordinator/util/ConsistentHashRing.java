package com.adnant1.coordinator.util;

import java.util.List;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

@Component
public class ConsistentHashRing{
    
    private final TreeMap<Integer, String> ring = new TreeMap<>();
    private final int VIRTUAL_NODES = 50; // Number of virtual nodes per real node

    @Value("${coordinator.node-urls}")
    private List<String> nodeUrls;

    // Initialize the consistent hash ring
    @PostConstruct
    public void init() {

    }

    // MD5-based hash function
    private int hash(String key) {

    }

    // Given a document ID, find the appropriate node URL
    public String getNodeForId(String id) {

    }
}
