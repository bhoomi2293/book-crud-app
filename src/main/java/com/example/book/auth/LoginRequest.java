package com.example.book.auth;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a login request containing user credentials (username and password).
 */
@Getter
@Setter
public class LoginRequest {

    /**
     * The username provided by the user.
     */
    private String username;

    /**
     * The password provided by the user.
     */
    private String password;
}