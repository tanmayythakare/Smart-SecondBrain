package com.example.backend.service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.example.backend.dto.RegisterRequest;
import com.example.backend.exception.ConflictException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.dto.AuthResponse;

import com.example.backend.security.JwtUtil;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public AuthService(
    	    UserRepository userRepository,
    	    AuthenticationManager authenticationManager,
    	    JwtUtil jwtUtil,
    	    BCryptPasswordEncoder passwordEncoder
    	) {
    	    this.userRepository = userRepository;
    	    this.authenticationManager = authenticationManager;
    	    this.jwtUtil = jwtUtil;
    	    this.passwordEncoder = passwordEncoder;
    	}




    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
        
        String token = jwtUtil.generateToken(user.getUsername());
        return new AuthResponse(token, "Registration Successful");
    }
    public String login(String username, String password) {
        logger.info("Login attempt for user: {}", username);
        
        try {
            // Check if user exists first to provide better error messages
            if (!userRepository.existsByUsername(username) && !userRepository.existsByEmail(username)) {
                logger.warn("Login failed: User not found: {}", username);
                throw new UsernameNotFoundException("User not found");
            }

            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            
            // Always get the canonical username from the authenticated principal
            User authenticatedUser = (User) authentication.getPrincipal();
            String canonicalUsername = authenticatedUser.getUsername();
            
            logger.info("Authentication successful for user: {}", canonicalUsername);
            return jwtUtil.generateToken(canonicalUsername);
        } catch (BadCredentialsException e) {
            logger.warn("Login failed: Invalid credentials for user: {}", username);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during login for user: {}", username, e);
            throw e;
        }
    }


}
