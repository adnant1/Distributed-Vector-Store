package com.adnant1.coordinator.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
        if (nodeUrls == null || nodeUrls.isEmpty()) {
            throw new IllegalStateException("No node URLs configured for the hash ring.");
        }

        // Add each node as multiple virtual nodes onto the ring
        for (String node: nodeUrls) {
            for (int i = 0; i < VIRTUAL_NODES; i++) {
                String virtualNodeKey = node + "-VN#" + i;
                int hash = hash(virtualNodeKey);
                ring.put(hash, node);
            }
        }
    }

    // MD5-based hash function
    private int hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(key.getBytes(StandardCharsets.UTF_8));
            int h = ((digest[0] & 0xFF) << 24)
                    | ((digest[1] & 0xFF) << 16)
                    | ((digest[2] & 0xFF) << 8)
                    | (digest[3] & 0xFF);
            return h & 0x7FFFFFFF;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    // Given a document ID, find the appropriate node URL
    public String getNodeForId(String id) {

    }
}
