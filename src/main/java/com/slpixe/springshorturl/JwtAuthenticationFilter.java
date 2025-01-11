package com.slpixe.springshorturl;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepo userRepo;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepo userRepo) {
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        // Extract the Authorization header
        String header = request.getHeader("Authorization");

        // Check if the header is present and starts with "Bearer "
        if (header != null && header.startsWith("Bearer ")) {
            // Extract the token from the header
            String token = header.substring(7);
            // Extract the username from the token
            String username = jwtUtil.extractUsername(token);

            // Ensure the username is not null and the user is not already authenticated
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Retrieve the user from the database
                UserModel user = userRepo.findByUsername(username).orElse(null);

                // Validate the token and ensure the user exists
                if (user != null && jwtUtil.validateToken(token, username)) {
                    // Create an authentication token
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(username, null, null);
                    // Set the authentication details
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Set the authentication in the security context
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
