package com.example.book.auth;

/**
 * Represents the response returned after successful login containing the JWT token.
 */
public class LoginResponse {

    /**
     * The JWT token generated upon successful authentication.
     */
    private String token;

    /**
     * Constructor to create a new {@link LoginResponse}.
     *
     * @param token The JWT token.
     */
    public LoginResponse(String token) {
        this.token = token;
    }

    /**
     * Returns the JWT token.
     *
     * @return The token.
     */
    public String getToken() {
        return token;
    }
}