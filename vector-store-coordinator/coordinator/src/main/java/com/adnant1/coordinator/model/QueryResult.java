package com.adnant1.coordinator.model;

public class QueryResult {
    private String id;
    private String text;
    private float score;

    public QueryResult() {}

    public QueryResult(String id, String text, float score) {
        this.id = id;
        this.text = text;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
}
