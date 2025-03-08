package com.codesurge.hackathon.service.impl;

import com.codesurge.hackathon.service.HackathonService;
import com.codesurge.hackathon.model.Problem;
import com.codesurge.hackathon.model.HackathonParticipation;
import com.codesurge.hackathon.model.User;
import com.codesurge.hackathon.model.Solution;
import com.codesurge.hackathon.repository.ProblemRepository;
import com.codesurge.hackathon.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class HackathonServiceImpl implements HackathonService {

    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;

    public HackathonServiceImpl(ProblemRepository problemRepository, UserRepository userRepository) {
        this.problemRepository = problemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Problem addProblem(Problem problem) {
        return problemRepository.save(problem);
    }

    @Override
    public List<Problem> getAllProblems() {
        return problemRepository.findAll();
    }

    @Override
    public void startHackathon(String hackathonName, List<String> teamNames, Integer durationInHours) {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(durationInHours);
        
        String hackathonId = UUID.randomUUID().toString();
        
        HackathonParticipation participation = new HackathonParticipation();
        participation.setHackathonId(hackathonId);
        participation.setHackathonName(hackathonName);
        participation.setStartTime(startTime);
        participation.setEndTime(endTime);
        participation.setActive(true);

        // Find all users in the selected teams and update their documents
        List<User> teamUsers = userRepository.findByTeamNameIn(teamNames);
        if (teamUsers.isEmpty()) {
            throw new RuntimeException("No users found for the selected teams");
        }

        teamUsers.forEach(user -> {
            // Deactivate any previous active hackathons
            user.getHackathonParticipations().forEach(h -> h.setActive(false));
            
            // Add new hackathon participation
            user.getHackathonParticipations().add(participation);
            userRepository.save(user);
        });
    }

    @Override
    public void submitSolution(String teamName, String githubUrl, String hostedUrl) {

    }

    @Override
    public Object getHackathonStatus() {
        // Implement status retrieval logic
        return null;
    }

    @Override
    public void selectProblem(String problemId, String userId) {
        // Implement problem selection logic
    }

    @Override
    public Problem getProblem(String problemId) {
        return problemRepository.findById(problemId)
            .orElseThrow(() -> new RuntimeException("Problem not found with id " + problemId));
    }

    @Override
    public void deleteProblem(String problemId) {
        Problem problem = getProblem(problemId);
        problemRepository.delete(problem);
    }
}