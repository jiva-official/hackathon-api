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
import java.util.Set;
import java.util.regex.Pattern;

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

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@(.+)$"
    );

    private static final Set<String> DISPOSABLE_DOMAINS = Set.of(
        "tempmail.com", "throwawaymail.com", "tmpmail.org", "temp-mail.org",
        "guerrillamail.com", "sharklasers.com", "mailinator.com", "yopmail.com"
    );

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        // Check email format
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Check for disposable email domains
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();
        if (DISPOSABLE_DOMAINS.contains(domain)) {
            throw new IllegalArgumentException("Disposable email addresses are not allowed");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
    }

    public AuthResponse registerUser(User user) {
        validateEmail(user.getEmail());
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