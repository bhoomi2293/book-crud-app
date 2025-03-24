package com.example.book.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the response returned after successful login containing the JWT token.
 */
@Getter
@AllArgsConstructor
public class LoginResponse {

    /**
     * The JWT token generated upon successful authentication.
     */
    private String token;
}