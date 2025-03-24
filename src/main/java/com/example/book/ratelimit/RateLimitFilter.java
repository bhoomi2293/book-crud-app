package com.example.book.ratelimit;

import com.example.book.auth.JwtUtil;
import io.github.bucket4j.*;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * A custom filter for rate limiting requests based on IP or username.
 * Ensures that a client can only make a specific number of requests per time window.
 */
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);

    // Constants for rate-limiting configuration
    private static final int MAX_REQUESTS = 5;
    private static final int REFILL_INTERVAL_SECONDS = 30;
    private static final int RATE_LIMIT_STATUS_CODE = 429;
    private static final int TOKEN_CONSUMPTION = 1;
    private static final int SECONDS_IN_NANOS = 1_000_000_000;

    /**
     * Stores a rate-limiting bucket for each client (IP or username).
     */
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    /**
     * Filters requests to enforce rate limits.
     *
     * @param request     The {@link HttpServletRequest}.
     * @param response    The {@link HttpServletResponse}.
     * @param filterChain The {@link FilterChain}.
     * @throws IOException if an input or output exception occurs.
     * @throws javax.servlet.ServletException if the request could not be handled.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws IOException, javax.servlet.ServletException {

        String identifier;
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String username = JwtUtil.validateToken(token);
            identifier = (username != null) ? username : request.getRemoteAddr();
        } else {
            identifier = request.getRemoteAddr();
        }

        logger.debug("RateLimitFilter: Processing request for identifier '{}'", identifier);

        Bucket bucket = buckets.computeIfAbsent(identifier, k -> {
            logger.debug("Creating new rate limit bucket for identifier '{}'", identifier);
            return createBucket();
        });

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(TOKEN_CONSUMPTION);
        if (probe.isConsumed()) {
            logger.debug("Request allowed. {} tokens remaining for identifier '{}'", probe.getRemainingTokens(), identifier);
            response.addHeader("X-RateLimit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            long waitForRefill = probe.getNanosToWaitForRefill() / SECONDS_IN_NANOS;
            logger.warn("Rate limit exceeded for identifier '{}'. Try again in {} seconds.", identifier, waitForRefill);
            response.setStatus(RATE_LIMIT_STATUS_CODE);
            response.getWriter().write("Rate limit exceeded. Try again in " + waitForRefill + " seconds.");
        }
    }

    /**
     * Creates a new rate-limiting bucket with a configured limit and refill interval.
     *
     * @return A configured {@link Bucket}.
     */
    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.classic(MAX_REQUESTS, Refill.greedy(MAX_REQUESTS, Duration.ofSeconds(REFILL_INTERVAL_SECONDS)));
        return Bucket.builder().addLimit(limit).build();
    }
}
