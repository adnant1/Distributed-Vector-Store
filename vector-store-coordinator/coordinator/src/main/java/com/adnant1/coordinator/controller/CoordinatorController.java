package com.adnant1.coordinator.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adnant1.coordinator.model.IndexRequest;
import com.adnant1.coordinator.model.QueryRequest;
import com.adnant1.coordinator.model.QueryResult;
import com.adnant1.coordinator.service.CoordinatorService;

@RestController
@RequestMapping("/coordinator")
public class CoordinatorController {
    
    private final CoordinatorService coordinatorService;

    public CoordinatorController(CoordinatorService coordinatorService) {
        this.coordinatorService = coordinatorService;
    }

    // Send index request to coordinator service, which routes it to the appropriate vector node
    @PostMapping("/index")
    public ResponseEntity<?> index(@RequestBody Map<String, String> body) {
        String text = body.get("text");
        if (text == null || text.isBlank()) {
            return ResponseEntity.badRequest().body("Text cannot be null or blank");
        }

        String id = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        IndexRequest request = new IndexRequest(id, text);

        coordinatorService.index(request);
        return ResponseEntity.ok().build();
    }

    // Send query request to coordinator service, which queries all vector nodes and aggregates results
    @PostMapping("/query")
    public ResponseEntity<?> query(@RequestBody QueryRequest request) {
        if (request.getQuery() == null || request.getQuery().isBlank()) {
            return ResponseEntity.badRequest().body("Query cannot be null or blank");
        }
        if (request.getTopK() <= 0) {
            return ResponseEntity.badRequest().body("topK must be greater than 0");
        }

        List<QueryResult> results = coordinatorService.query(request);
        return ResponseEntity.ok(results);
    }
}
