package com.codesurge.hackathon.controller;

import com.codesurge.hackathon.dto.AuthResponse;
import com.codesurge.hackathon.dto.PasswordChangeRequest;
import com.codesurge.hackathon.dto.UserUpdateDTO;
import com.codesurge.hackathon.model.User;
import com.codesurge.hackathon.service.AuthService;
import com.codesurge.hackathon.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    //ok
    @GetMapping("/team/{teamName}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<User> getUserByTeamName(@PathVariable String teamName) {
        return ResponseEntity.ok(userService.getUserByTeamName(teamName));
    }

    //not working
    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN') and (#userId == principal.username or hasAuthority('ROLE_ADMIN'))")
    public ResponseEntity<User> updateUser(@PathVariable String userId,
                                           @Valid @RequestBody UserUpdateDTO userDetails) {
        try {
            User updatedUser = userService.updateUser(userId, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    //ok
    @GetMapping("/profile")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<User> getCurrentUserProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @PostMapping("/{userId}/problem/{problemId}/{hackathonId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> assignProblem(@PathVariable String userId,
                                              @PathVariable String problemId, @PathVariable String hackathonId) {
        userService.assignProblem(userId, problemId, hackathonId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> changePassword(Principal principal,
                                          @RequestBody PasswordChangeRequest request) {
        try {
            AuthResponse response = authService.changePassword(
                principal.getName(), 
                request.getCurrentPassword(), 
                request.getNewPassword()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Password change failed: " + e.getMessage());
        }
    }
}