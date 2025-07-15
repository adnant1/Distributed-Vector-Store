package com.adnant1.node.model;

public class EmbeddingRequest {
    public String model;
    public String input;

    public EmbeddingRequest(String model, String input) {
        this.model = model;
        this.input = input;
    }

    public String getModel() {
        return model;
    }

    private String getInput() {
        return input;
    }
}
