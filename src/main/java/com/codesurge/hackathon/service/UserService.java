package com.codesurge.hackathon.service;

import com.codesurge.hackathon.model.User;
import java.util.List;

public interface UserService {

    /**
     * Get all registered users
     */
    List<User> getAllUsers();

    /**
     * Get user by ID
     */
    User getUserById(String userId);

    /**
     * Get user by team name
     */
    User getUserByTeamName(String teamName);

    /**
     * Update user details
     */
    User updateUser(String userId, User userDetails);

    /**
     * Delete user
     */
    void deleteUser(String userId);

    /**
     * Get current logged-in user profile
     */
    User getCurrentUserProfile();

    /**
     * Assign problem to user/team
     */
    void assignProblem(String userId, String problemId);

    /**
     * Check if team name is available
     */
    boolean isTeamNameAvailable(String teamName);

    /**
     * Count total registered teams
     */
    long getRegisteredTeamsCount();

    /**
     * Get teams that have selected a specific problem
     */
    List<User> getTeamsByProblem(String problemId);

    /**
     * Submit solution for team
     */
    void submitSolution(String teamId, String githubUrl, String hostedUrl);
}