package com.facilite_toi.dto;

public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest() {}
    // Constructeur de la classe LoginRequet
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
    // Getters et Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginRequet{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
} 
