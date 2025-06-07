package com.facilite_toi.dto;

public class ResetPasswordRequest {
    private String token;
    private String newPassword;
    
    // Constructeur de la classe ResetPasswordRequet
    public ResetPasswordRequest() {}
    public ResetPasswordRequest(String token, String newPassword) {
        this.token = token;
        this.newPassword = newPassword;
    }
    // Getters et Setters
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
