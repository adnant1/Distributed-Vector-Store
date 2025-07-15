package com.adnant1.node.model;

public class SearchResult {
    private String id;
    private String text;
    private Double score;

    public SearchResult(String id, String text, Double score) {
        this.id = id;
        this.text = text;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Double getScore() {
        return score;
    }
}
