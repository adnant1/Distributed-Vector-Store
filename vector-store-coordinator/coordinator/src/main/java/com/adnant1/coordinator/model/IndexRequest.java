package com.adnant1.coordinator.model;

public class IndexRequest {
    private String id;
    private String text;

    public IndexRequest() {}

    public IndexRequest(String id, String text) {
        this.id = id;
        this.text = text;
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
}
