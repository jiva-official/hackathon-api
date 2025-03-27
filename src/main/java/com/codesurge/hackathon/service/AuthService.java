package com.codesurge.hackathon.service;

import com.codesurge.hackathon.config.JwtTokenProvider;
import com.codesurge.hackathon.constant.ErrorCodes;
import com.codesurge.hackathon.dto.AuthResponse;
import com.codesurge.hackathon.exception.AuthenticationException;
import com.codesurge.hackathon.exception.UserException;
import com.codesurge.hackathon.model.EmailVerificationToken;
import com.codesurge.hackathon.model.User;
import com.codesurge.hackathon.repository.EmailVerificationTokenRepository;
import com.codesurge.hackathon.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Service
public class AuthService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$"
    );
    private static final Set<String> DISPOSABLE_DOMAINS = Set.of(
            "tempmail.com", "throwawaymail.com", "tmpmail.org", "temp-mail.org",
            "guerrillamail.com", "sharklasers.com", "mailinator.com", "yopmail.com"
    );
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private EmailVerificationTokenRepository tokenRepository;
    @Value("${app.url}")
    private String appUrl;

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
        log.info("Starting user registration process for username: {}", user.getUsername());
        try {
            validateEmail(user.getEmail());
            if (userRepository.existsByUsername(user.getUsername())) {
                throw new AuthenticationException("Username already exists", ErrorCodes.AUTH_EMAIL_EXISTS);
            }

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEmailVerified(false); // Set initial verification status
            User savedUser = userRepository.save(user);

            // Generate verification token
            String token = generateVerificationToken();
            EmailVerificationToken verificationToken = new EmailVerificationToken(savedUser, token);
            tokenRepository.save(verificationToken);

            // Send verification email
            sendVerificationEmail(user.getEmail(), token);
            log.info("User registration completed successfully for username: {}", user.getUsername());

            return new AuthResponse(user.getUsername(), "Registration successful. Please check your email to verify your account.");
        } catch (AuthenticationException e) {
            log.error("Registration failed for username {}: {}", user.getUsername(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during registration for username {}: {}", user.getUsername(), e.getMessage(), e);
            throw new AuthenticationException("Registration failed: " + e.getMessage(), ErrorCodes.INTERNAL_ERROR);
        }
    }

    private String generateVerificationToken() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private void sendVerificationEmail(String email, String token) {
        String verificationUrl = appUrl + "/api/auth/verify?token=" + token;
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("verificationUrl", verificationUrl);
        templateVariables.put("otp", token);

        String htmlContent = notificationService.processTemplate("email/verification", templateVariables);
        notificationService.sendEmail(email, "Verify Your Email", htmlContent);
    }

    public AuthResponse verifyEmail(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new UserException("Invalid verification token"));

        if (verificationToken.isUsed()) {
            throw new UserException("Token already used");
        }

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new UserException("Token has expired");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);

        return new AuthResponse(user.getUsername(), "Email verified successfully");
    }

    public AuthResponse loginUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException("User not found"));

        if (!user.isEmailVerified()) {
            throw new UserException("Please verify your email before logging in");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserException("Invalid password");
        }

        String token = jwtTokenProvider.generateToken(username);
        return new AuthResponse(token, username, "Login successful");
    }

    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("No user found with this email address"));

        String token = generateVerificationToken();
        EmailVerificationToken resetToken = new EmailVerificationToken(user, token);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1)); // Reset link valid for 1 hour
        tokenRepository.save(resetToken);

        // Send reset password email
        String resetUrl = appUrl + "/api/auth/reset-password?token=" + token;
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("resetUrl", resetUrl);
        templateVariables.put("username", user.getUsername());
        templateVariables.put("otp", token);

        String htmlContent = notificationService.processTemplate("email/reset-password", templateVariables);
        notificationService.sendEmail(email, "Reset Your Password", htmlContent);
    }

    public AuthResponse resetPassword(String token, String newPassword) {
        EmailVerificationToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new UserException("Invalid reset token"));

        if (resetToken.isUsed()) {
            throw new UserException("This reset link has already been used");
        }

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new UserException("This reset link has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        return new AuthResponse(user.getUsername(), "Password reset successfully");
    }

    public AuthResponse changePassword(String username, String currentPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new UserException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return new AuthResponse(username, "Password changed successfully");
    }
}