package com.facilite_toi.dto;

public class RegisterRequest {
    private String forename;
    private String surname;
    private String email;
    private String password;


    public RegisterRequest() {}

    // getters et setters
    public String getForename() {
        return forename;
    }
    public void setForename(String forename) {
        this.forename = forename;
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
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
        return "RegisterRequet{" +
                "forename='" + forename + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
    // Constructeur de la classe RegisterRequet
    public RegisterRequest(String forename, String surname, String email, String password) {
        this.forename = forename;
        this.surname = surname;
        this.email = email;
        this.password = password;
    }
}