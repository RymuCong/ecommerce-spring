package com.arius.ecommerce.security;

import com.arius.ecommerce.dto.response.ErrorResponse;
import com.arius.ecommerce.exception.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    private final CustomUserDetailService userDetailService;

    private final ObjectMapper objectMapper;

    @Autowired
    public JWTFilter(JwtUtils jwtUtils, CustomUserDetailService userDetailService, ObjectMapper objectMapper) {
        this.jwtUtils = jwtUtils;
        this.userDetailService = userDetailService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String token = null;
        String email = null;

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
            try {
                email = jwtUtils.extractUserName(token);
            } catch (TokenExpiredException e) {
                ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), LocalDateTime.now());
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                return;
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailService.loadUserByUsername(email);
            if (jwtUtils.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
