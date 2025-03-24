package com.example.book.security;

import com.example.book.ratelimit.RateLimitFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration class for Spring Security.
 * Configures the application's security filters and access rules.
 */
@Configuration
public class SecurityConfig {

    /**
     * Defines a rate-limiting filter to be used in the security chain.
     *
     * @return A {@link RateLimitFilter} bean.
     */
    @Bean
    public RateLimitFilter rateLimitFilter() {
        return new RateLimitFilter();
    }

    /**
     * Configures the security filter chain for handling authentication and authorization.
     *
     * @param http The {@link HttpSecurity} to configure.
     * @return A configured {@link SecurityFilterChain}.
     * @throws Exception If the configuration fails.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests()
                .antMatchers("/auth/**").permitAll()  // Allow unauthenticated access to /auth endpoints
                .antMatchers("/books/**").authenticated()  // Protect /books endpoints
                .and()
                .addFilterBefore(new JwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(rateLimitFilter(), JwtFilter.class);

        return http.build();
    }
}
