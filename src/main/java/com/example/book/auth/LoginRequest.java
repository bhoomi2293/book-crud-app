package com.example.book.auth;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a login request containing user credentials (username and password).
 */
public class LoginRequest {

    /**
     * The username provided by the user.
     */
    private String username;

    /**
     * The password provided by the user.
     */
    private String password;

    public LoginRequest() { }

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}