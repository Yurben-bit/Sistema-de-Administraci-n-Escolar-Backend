package com.tecmilenio.edutec.dto;

public class LoginRequest {
    private String username;
    private String password;

    // Constructor vacío (necesario para que Spring pueda crear el objeto)
    public LoginRequest() {
    }

    // Getter para username
    public String getUsername() {
        return username;
    }

    // Setter para username
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter para password
    public String getPassword() {
        return password;
    }

    // Setter para password
    public void setPassword(String password) {
        this.password = password;
    }

}
