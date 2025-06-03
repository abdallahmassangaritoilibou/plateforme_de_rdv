package com.facilite_toi.dto;

public class TokenRequest {
    private String token;

    // Constructeur de la classe TokenRequet
    public TokenRequest() {}

    public TokenRequest(String token) {
        this.token = token;
    }

    // Getters et Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "TokenRequet{" +
                "token='" + token + '\'' +
                '}';
    }
}
