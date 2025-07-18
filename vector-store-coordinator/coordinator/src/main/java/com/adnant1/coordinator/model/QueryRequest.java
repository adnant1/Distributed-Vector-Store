package com.adnant1.coordinator.model;

public class QueryRequest {
    private String query;
    private int topK;

    public QueryRequest() {}

    public QueryRequest(String query, int topK) {
        this.query = query;
        this.topK = topK;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getTopK() {
        return topK;
    }

    public void setTopK(int topK) {
        this.topK = topK;
    }   
}
