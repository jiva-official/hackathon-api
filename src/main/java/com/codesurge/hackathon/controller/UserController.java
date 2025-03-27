package com.codesurge.hackathon.controller;

import com.codesurge.hackathon.constant.ErrorCodes;
import com.codesurge.hackathon.dto.AuthResponse;
import com.codesurge.hackathon.dto.PasswordChangeRequest;
import com.codesurge.hackathon.dto.SuccessResponse;
import com.codesurge.hackathon.dto.UserUpdateDTO;
import com.codesurge.hackathon.exception.ErrorResponse;
import com.codesurge.hackathon.exception.InvalidPasswordException;
import com.codesurge.hackathon.exception.ProblemNotFoundException;
import com.codesurge.hackathon.exception.UserException;
import com.codesurge.hackathon.model.User;
import com.codesurge.hackathon.service.AuthService;
import com.codesurge.hackathon.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        log.info("Retrieving all users");
        try {
            List<User> users = userService.getAllUsers();
            log.info("Retrieved {} users successfully", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Failed to retrieve users: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(ErrorCodes.USER_FETCH_FAILED, e.getMessage(), LocalDateTime.now()));
        }
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        log.info("Retrieving user with ID: {}", userId);
        try {
            User user = userService.getUserById(userId);
            log.info("Successfully retrieved user: {}", userId);
            return ResponseEntity.ok(user);
        } catch (UsernameNotFoundException e) {
            log.error("User not found: {}", userId);
            return ResponseEntity.notFound()
                .build();
        } catch (Exception e) {
            log.error("Failed to retrieve user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(ErrorCodes.USER_FETCH_FAILED, e.getMessage(), LocalDateTime.now()));
        }
    }

    @GetMapping("/team/{teamName}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getUserByTeamName(@PathVariable String teamName) {
        log.info("Retrieving user by team name: {}", teamName);
        try {
            User user = userService.getUserByTeamName(teamName);
            log.info("Successfully retrieved user for team: {}", teamName);
            return ResponseEntity.ok(user);
        } catch (UserException e) {
            log.error("Team not found: {}", teamName);
            return ResponseEntity.notFound()
                .build();
        } catch (Exception e) {
            log.error("Failed to retrieve team {}: {}", teamName, e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(ErrorCodes.TEAM_FETCH_FAILED, e.getMessage(), LocalDateTime.now()));
        }
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN') and (#userId == principal.username or hasAuthority('ROLE_ADMIN'))")
    public ResponseEntity<?> updateUser(@PathVariable String userId,
                                      @Valid @RequestBody UserUpdateDTO userDetails) {
        log.info("Updating user with ID: {}", userId);
        try {
            User updatedUser = userService.updateUser(userId, userDetails);
            log.info("Successfully updated user: {}", userId);
            return ResponseEntity.ok(updatedUser);
        } catch (UserException e) {
            log.error("User not found for update: {}", userId);
            return ResponseEntity.notFound()
                .build();
        } catch (ValidationException e) {
            log.error("Validation failed for user update {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(ErrorCodes.VALIDATION_ERROR, e.getMessage(), LocalDateTime.now()));
        } catch (Exception e) {
            log.error("Failed to update user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(ErrorCodes.USER_UPDATE_FAILED, e.getMessage(), LocalDateTime.now()));
        }
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        log.info("Deleting user with ID: {}", userId);
        try {
            userService.deleteUser(userId);
            log.info("Successfully deleted user: {}", userId);
            return ResponseEntity.ok()
                .body(new SuccessResponse("User deleted successfully"));
        } catch (UsernameNotFoundException e) {
            log.error("User not found for deletion: {}", userId);
            return ResponseEntity.notFound()
                .build();
        } catch (Exception e) {
            log.error("Failed to delete user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(ErrorCodes.USER_DELETE_FAILED, e.getMessage(), LocalDateTime.now()));
        }
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getCurrentUserProfile() {
        log.info("Retrieving current user profile");
        try {
            User profile = userService.getCurrentUserProfile();
            log.info("Successfully retrieved user profile");
            return ResponseEntity.ok(profile);
        } catch (UsernameNotFoundException e) {
            log.error("Current user profile not found");
            return ResponseEntity.notFound()
                .build();
        } catch (Exception e) {
            log.error("Failed to retrieve current user profile: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(ErrorCodes.PROFILE_FETCH_FAILED, e.getMessage(), LocalDateTime.now()));
        }
    }

    @PostMapping("/{userId}/problem/{problemId}/{hackathonId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> assignProblem(@PathVariable String userId,
                                         @PathVariable String problemId,
                                         @PathVariable String hackathonId) {
        log.info("Assigning problem {} to user {} for hackathon {}", problemId, userId, hackathonId);
        try {
            userService.assignProblem(userId, problemId, hackathonId);
            log.info("Successfully assigned problem {} to user {}", problemId, userId);
            return ResponseEntity.ok()
                .body(new SuccessResponse("Problem assigned successfully"));
        } catch (UsernameNotFoundException | ProblemNotFoundException e) {
            log.error("Resource not found: {}", e.getMessage());
            return ResponseEntity.notFound()
                .build();
        } catch (Exception e) {
            log.error("Failed to assign problem: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(ErrorCodes.PROBLEM_ASSIGNMENT_FAILED, e.getMessage(), LocalDateTime.now()));
        }
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> changePassword(Principal principal,
                                          @RequestBody PasswordChangeRequest request) {
        log.info("Changing password for user: {}", principal.getName());
        try {
            AuthResponse response = authService.changePassword(
                principal.getName(),
                request.getCurrentPassword(),
                request.getNewPassword()
            );
            log.info("Password changed successfully for user: {}", principal.getName());
            return ResponseEntity.ok(response);
        } catch (InvalidPasswordException e) {
            log.error("Invalid password for user {}: {}", principal.getName(), e.getMessage());
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(ErrorCodes.INVALID_PASSWORD, e.getMessage(), LocalDateTime.now()));
        } catch (Exception e) {
            log.error("Failed to change password for user {}: {}", principal.getName(), e.getMessage(), e);
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(ErrorCodes.PASSWORD_CHANGE_FAILED, e.getMessage(), LocalDateTime.now()));
        }
    }
}