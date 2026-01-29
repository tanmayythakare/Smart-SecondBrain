package com.example.backend.service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.example.backend.dto.RegisterRequest;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;

import com.example.backend.security.JwtUtil;

@Service
public class AuthService {

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




    public void register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
    }
    public String login(String username, String password) {

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );

        return jwtUtil.generateToken(username);
    }


}
