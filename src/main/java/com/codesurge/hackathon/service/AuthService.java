package com.codesurge.hackathon.service;

import com.codesurge.hackathon.config.JwtTokenProvider;
import com.codesurge.hackathon.dto.AuthResponse;
import com.codesurge.hackathon.model.User;
import com.codesurge.hackathon.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private NotificationService notificationService;

    public AuthResponse registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        notificationService.notifyUserRegistration(user.getEmail(), user.getTeamName());
//        String token = jwtTokenProvider.generateToken(user.getUsername());
        return new AuthResponse(user.getUsername(), "Registration successful");
    }

    public AuthResponse loginUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtTokenProvider.generateToken(username);
        return new AuthResponse(token, username, "Login successful");
    }
}