package com.adnant1.coordinator.controller;

import java.util.List;

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
    public ResponseEntity<?> index(@RequestBody IndexRequest request) {
        if (request.getId() == null || request.getId().isBlank()) {
            return ResponseEntity.badRequest().body("ID cannot be null or blank");
        }

        if (request.getText() == null || request.getText().isBlank()) {
            return ResponseEntity.badRequest().body("Text cannot be null or blank");
        }

        coordinatorService.index(request);
        return ResponseEntity.ok().build();
    }

    // Send query request to coordinator service, which queries all vector nodes and aggregates results
    @PostMapping("/query")
    public ResponseEntity<?> query(@RequestBody QueryRequest request) {
        if (request.getQueryText() == null || request.getQueryText().isBlank()) {
            return ResponseEntity.badRequest().body("Query text cannot be null or blank");
        }
        if (request.getTopK() <= 0) {
            return ResponseEntity.badRequest().body("topK must be greater than 0");
        }

        List<QueryResult> results = coordinatorService.query(request);
        return ResponseEntity.ok(results);
    }
}
