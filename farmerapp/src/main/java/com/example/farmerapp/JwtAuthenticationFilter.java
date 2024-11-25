package com.example.farmerapp;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.ServletException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.Filter;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@WebFilter
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String SECRET_KEY = "my_secret_key"; // You should store this securely

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String token = extractToken(request);
        System.out.println("filter "+token);

        if (token != null && isValidToken(token)) {
            String user = getUserFromToken(token);

            // Authenticate the user based on the JWT token
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());

            // Set the authentication in the SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // Continue the request-response chain
        filterChain.doFilter(request, response);
    }

    // Extract JWT token from the Authorization header
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix
        }

        return null;
    }

    // Validate the JWT token
    private boolean isValidToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true; // Token is valid
        } catch (Exception e) {
            return false; // Token is invalid or expired
        }
    }

    // Extract the user (subject) from the JWT token
    private String getUserFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // Get the username (subject) from the token
    }
}
