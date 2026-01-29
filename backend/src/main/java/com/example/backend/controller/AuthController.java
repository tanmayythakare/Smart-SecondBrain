package com.example.backend.controller;
import com.example.backend.dto.LoginRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.backend.dto.RegisterRequest;
import com.example.backend.service.AuthService;
import com.example.backend.dto.AuthResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody RegisterRequest request) {
        authService.register(request);
    }
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
    	
    	String token=authService.login(
    			request.getUsername(),
    			request.getPassword()
    			);
    	return new AuthResponse(token,"Login Successful");
    	}
}
