package com.facilite_toi.dto;

public class ForgotPasswordRequest {
    private String email;

    // Constructeur de la classe ForgotPasswordRequet
    public ForgotPasswordRequest() {}

    public ForgotPasswordRequest(String email) {
        this.email = email;
    }

    // Getters et Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "ForgotPasswordRequet{" +
                "email='" + email + '\'' +
                '}';
    }
}
