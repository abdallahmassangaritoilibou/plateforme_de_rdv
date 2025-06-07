package com.facilite_toi.dto;

public class ApiResponse {
    private boolean success;
    private String message;
   
    // constructeur de la classe ApiResponse
    public ApiResponse() {}
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    // getters et setters
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}

