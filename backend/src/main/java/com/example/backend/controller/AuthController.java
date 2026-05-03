package com.example.backend.controller;
import com.example.backend.dto.LoginRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.backend.dto.RegisterRequest;
import com.example.backend.service.AuthService;
import com.example.backend.dto.AuthResponse;

import jakarta.validation.Valid;

import com.example.backend.security.RateLimiter;
import com.example.backend.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final RateLimiter rateLimiter;

    public AuthController(AuthService authService, RateLimiter rateLimiter) {
        this.authService = authService;
        this.rateLimiter = rateLimiter;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request, HttpServletRequest servletRequest) {
        if (!rateLimiter.isAllowed(servletRequest.getRemoteAddr())) {
            throw new UnauthorizedException("Too many registration attempts. Please try again later.");
        }
        return authService.register(request);
    }
    
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        if (!rateLimiter.isAllowed(servletRequest.getRemoteAddr())) {
            throw new UnauthorizedException("Too many login attempts. Please try again later.");
        }
    	
        String token = authService.login(
            request.getUsername(),
            request.getPassword()
        );
        return new AuthResponse(token, "Login Successful");
    }
    
}
