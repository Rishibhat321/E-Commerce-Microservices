package com.ecommerce.auth_service.security;


import com.ecommerce.auth_service.util.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        // Extracts Header
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT , removes Bearer only keeps the token
        String jwt = authHeader.substring(7);


        try {

            // throws an exception before Spring reaches the AuthenticationEntryPoint, as a result JSON response gets corrupted
            // Extracts email
            String email = jwtService.extractEmail(jwt);

            if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                // loads user by email from db
                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);

                if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {

                    // Creating an authentication object that represents user has already been authenticated
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,                     // here password is null as JWT is verified, so spring no longer needs ths password anymore.
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    // sets authentication
                    // also Controller/service can access current user
                    SecurityContextHolder.getContext()
                            .setAuthentication(authToken);
                }
            }
        }
        // catch JWT-specific exceptions
        catch(JwtException | IllegalArgumentException ex) {

            // Invalid JWT
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

}






