package com.codesurge.hackathon.controller;

import com.codesurge.hackathon.model.User;
import com.codesurge.hackathon.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

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
    @PreAuthorize("#userId == authentication.principal.id or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable String userId,
            @Valid @RequestBody User userDetails) {
        return ResponseEntity.ok(userService.updateUser(userId, userDetails));
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

    @PostMapping("/{userId}/problem/{problemId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> assignProblem(@PathVariable String userId,
            @PathVariable String problemId) {
        userService.assignProblem(userId, problemId);
        return ResponseEntity.ok().build();
    }
}