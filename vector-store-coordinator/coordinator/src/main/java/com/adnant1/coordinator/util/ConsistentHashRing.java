package com.adnant1.coordinator.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

@Component
public class ConsistentHashRing{
    
    private final TreeMap<Integer, String> ring = new TreeMap<>();
    private final int VIRTUAL_NODES = 50; // Number of virtual nodes per real node
    public static final int REPLICATION_FACTOR = 2; // Number of nodes to replicate data to
    
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

    // Given a document ID, return REPLICATION_FACTOR distinct node URLs responsible for it
    public List<String> getNodesForId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID cannot be null or blank");
        }

        if (ring.isEmpty()) {
            throw new IllegalStateException("Hash ring is not initialized.");
        }

        int keyHash = hash(id);
        List<String> nodes = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        NavigableMap<Integer, String> tailMap = ring.tailMap(keyHash, true);
        Iterator<Map.Entry<Integer, String>> it = tailMap.entrySet().iterator();

        // Loop until we find REPLICATION_FACTOR distinct nodes
        while (nodes.size() < REPLICATION_FACTOR) {
            if (!it.hasNext()) {
                it = ring.entrySet().iterator(); // Wrap around to the start of the ring
            }

            String physicalNode = it.next().getValue();
            if (seen.add(physicalNode)) { // Only add if not already seen
                nodes.add(physicalNode);
            }
        }

        return nodes;
    }
}
