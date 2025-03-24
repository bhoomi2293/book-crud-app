package com.example.book.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a user entity with username and password.
 */
@AllArgsConstructor
@Getter
public class User {

    /**
     * The username of the user.
     */
    private String username;

    /**
     * The password of the user.
     */
    private String password;
}
