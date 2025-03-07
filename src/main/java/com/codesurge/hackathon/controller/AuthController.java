package com.codesurge.hackathon.controller;

    import com.codesurge.hackathon.dto.AuthResponse;
    import com.codesurge.hackathon.dto.LoginRequest;
    import com.codesurge.hackathon.model.User;
    import com.codesurge.hackathon.service.AuthService;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/api/auth")
    @CrossOrigin(origins = "*")
    public class AuthController {
        private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

        @Autowired
        private AuthService authService;

        @PostMapping("/register")
        public ResponseEntity<?> registerUser(@RequestBody User user) {
            logger.debug("Received registration request for user: {}", user.getUsername());
            try {
                AuthResponse response = authService.registerUser(user);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
            }
        }

        @PostMapping("/login")
        public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
            logger.debug("Received login request for user: {}", loginRequest.getUsername());
            try {
                AuthResponse response = authService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
            }
        }
    }