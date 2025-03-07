package com.codesurge.hackathon.service.impl;

import com.codesurge.hackathon.model.User;
import com.codesurge.hackathon.repository.UserRepository;
import com.codesurge.hackathon.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(String userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    @Override
    public User getUserByTeamName(String teamName) {
        return userRepository.findByTeamName(teamName)
            .orElseThrow(() -> new RuntimeException("Team not found: " + teamName));
    }

    @Override
    public User updateUser(String userId, User userDetails) {
        User user = getUserById(userId);
        user.setTeamName(userDetails.getTeamName());
        user.setEmail(userDetails.getEmail());
        user.setUsername(userDetails.getUsername());
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public User getCurrentUserProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
            .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    @Override
    public void assignProblem(String userId, String problemId) {
        User user = getUserById(userId);
        user.setAssignedProblemId(problemId);
        userRepository.save(user);
    }

    @Override
    public boolean isTeamNameAvailable(String teamName) {
        return !userRepository.existsByTeamName(teamName);
    }

    @Override
    public long getRegisteredTeamsCount() {
        return userRepository.countByTeamNameIsNotNull();
    }

    @Override
    public List<User> getTeamsByProblem(String problemId) {
        return userRepository.findByAssignedProblemId(problemId);
    }

    @Override
    public void submitSolution(String teamId, String githubUrl, String hostedUrl) {
        User user = getUserById(teamId);
        user.setSubmissionUrl(githubUrl);
        user.setHostedUrl(hostedUrl);
        user.setSolutionSubmitted(true);
        userRepository.save(user);
    }
}