package com.adnant1.node.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.adnant1.node.model.SearchResult;
import com.adnant1.node.service.VectorStoreService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/node")
public class NodeController {
    
    @Autowired
    private VectorStoreService vectorStoreService;

    // Index text and its embedding
    @PostMapping("/index")
    public void index(@RequestBody Map<String, String> body) {
    
    }

    // Search for similar texts given a query
    @PostMapping("/query")
    public List<SearchResult> query(@RequestBody Map<String, Object> body) {

    }
}
