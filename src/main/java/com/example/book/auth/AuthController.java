package com.example.book.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling authentication-related requests.
 * Provides endpoints for user login and token generation.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    // For demo, let's store one user in-memory
    private final User demoUser = new User("john", "password123");

    /**
     * Handles login requests.
     *
     * @param request The {@link LoginRequest} containing the username and password.
     * @return A {@link ResponseEntity} containing either:
     *         - A {@link LoginResponse} with a generated JWT token if credentials are valid.
     *         - An error message (HTTP status 401) if credentials are invalid.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        logger.info("Received login request: username={}, password={}", request.getUsername(), request.getPassword());
        if (request.getUsername().equals(demoUser.getUsername()) &&
                request.getPassword().equals(demoUser.getPassword())) {
            logger.info("Authentication successful for username={}", request.getUsername());
            String token = JwtUtil.generateToken(request.getUsername());
            return ResponseEntity.ok(new LoginResponse(token));
        }
        logger.warn("Authentication failed for username={}", request.getUsername());
        return ResponseEntity.status(401).body("Invalid credentials");
    }
}
