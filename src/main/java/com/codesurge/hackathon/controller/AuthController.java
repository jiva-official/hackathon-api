package com.codesurge.hackathon.controller;

import com.codesurge.hackathon.constant.ErrorCodes;
import com.codesurge.hackathon.dto.AuthResponse;
import com.codesurge.hackathon.dto.LoginRequest;
import com.codesurge.hackathon.dto.PasswordResetRequest;
import com.codesurge.hackathon.dto.SuccessResponse;
import com.codesurge.hackathon.exception.ErrorResponse;
import com.codesurge.hackathon.model.User;
import com.codesurge.hackathon.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        log.info("Received registration request for username: {}", user.getUsername());
        try {
            AuthResponse response = authService.registerUser(user);
            log.info("User registered successfully: {}", user.getUsername());
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            log.error("Registration failed for {}: {}", user.getUsername(), e.getMessage());
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(ErrorCodes.REGISTRATION_FAILED, e.getMessage(), LocalDateTime.now()));
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        log.info("Processing email verification for token");
        try {
            AuthResponse response = authService.verifyEmail(token);
            log.info("Email verification successful");
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            log.error("Email verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(ErrorCodes.EMAIL_VERIFY_FAILED, e.getMessage(), LocalDateTime.now()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        log.info("Received login request for user: {}", loginRequest.getUsername());
        try {
            AuthResponse response = authService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
            log.info("Login successful for user: {}", loginRequest.getUsername());
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            log.error("Login failed for user {}: {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(ErrorCodes.LOGIN_ERROR, e.getMessage(), LocalDateTime.now()));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        log.info("Processing forgot password request for email: {}", email);
        try {
            authService.initiatePasswordReset(email);
            log.info("Password reset instructions sent to: {}", email);
            return ResponseEntity.ok(new SuccessResponse("Password reset instructions sent to your email"));
        } catch (AuthenticationException e) {
            log.error("Password reset initiation failed for {}: {}", email, e.getMessage());
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getMessage(), LocalDateTime.now()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        log.info("Processing password reset request");
        try {
            AuthResponse response = authService.resetPassword(request.getToken(), request.getNewPassword());
            log.info("Password reset successful");
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            log.error("Password reset failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(ErrorCodes.RESET_PASSWORD_FAILED, e.getMessage(), LocalDateTime.now()));
        }
    }

}