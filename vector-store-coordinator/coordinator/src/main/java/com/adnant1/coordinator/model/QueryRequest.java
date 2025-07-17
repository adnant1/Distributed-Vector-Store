package com.adnant1.coordinator.model;

public class QueryRequest {
    private String queryText;
    private int topK;

    public QueryRequest() {}

    public QueryRequest(String queryText, int topK) {
        this.queryText = queryText;
        this.topK = topK;
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
    }

    public int getTopK() {
        return topK;
    }

    public void setTopK(int topK) {
        this.topK = topK;
    }   
}
