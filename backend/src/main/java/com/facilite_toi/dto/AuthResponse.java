package com.facilite_toi.dto;

import com.facilite_toi.model.User;

public class AuthResponse {
    private String token;
    private UserInfo user;  // j'utilise une classe UserInfo pour éviter d'exposer le mot de passe
    private String message;

    public AuthResponse() {}
    
    public AuthResponse(String token, User user, String message) {
        this.token = token;
        this.user = new UserInfo(user);  // Convertit User en UserInfo
        this.message = message;
    }

    // Getters et Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
    public UserInfo getUser() {
        return user;
    }
    
    public void setUser(UserInfo user) {
        this.user = user;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Classe interne pour les infos utilisateur (sans mot de passe)
    public static class UserInfo {
        private Long id;
        private String forename;
        private String surname;
        private String email;

        public UserInfo(User user) {
            this.id = user.getId();
            this.forename = user.getForename();
            this.surname = user.getSurname();
            this.email = user.getEmail();
            // Pas de mot de passe pour la sécurité
        }

        // Getters
        public Long getId() { return id; }
        public String getForename() { return forename; }
        public String getSurname() { return surname; }
        public String getEmail() { return email; }
        
        // Setters
        public void setId(Long id) { this.id = id; }
        public void setForename(String forename) { this.forename = forename; }
        public void setSurname(String surname) { this.surname = surname; }
        public void setEmail(String email) { this.email = email; }
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "token='" + token + '\'' +
                ", user=" + user +
                ", message='" + message + '\'' +
                '}';
    }
}