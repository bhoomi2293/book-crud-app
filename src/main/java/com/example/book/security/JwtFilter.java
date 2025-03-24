package com.example.book.security;

import com.example.book.auth.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A custom filter for handling JWT-based authentication.
 * This filter extracts the JWT token from the Authorization header,
 * validates it, and sets the authentication context for the current request.
 */
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    /**
     * Intercepts HTTP requests to authenticate users based on their JWT token.
     *
     * @param request     The {@link HttpServletRequest}.
     * @param response    The {@link HttpServletResponse}.
     * @param filterChain The {@link FilterChain}.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) {
        try {
            String header = request.getHeader("Authorization");
            logger.debug("JwtFilter: Authorization header received: {}", header);
            if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                logger.debug("JwtFilter: Extracted token: {}", token);
                String username = JwtUtil.validateToken(token);
                if (username != null) {
                    logger.info("JwtFilter: Token validated successfully for username: {}", username);
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(username, null, null);
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    logger.warn("JwtFilter: Token validation failed.");
                }
            } else {
                logger.debug("JwtFilter: No Bearer token found in the Authorization header.");
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            logger.error("JwtFilter: Exception during token processing", e);
            try {
                filterChain.doFilter(request, response);
            } catch (Exception ex) {
                logger.error("JwtFilter: Exception in filter chain after error", ex);
            }
        }
    }
}
