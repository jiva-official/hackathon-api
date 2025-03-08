package com.codesurge.hackathon.service.impl;

import com.codesurge.hackathon.dto.*;
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
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

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
    public void submitSolution(String userId, String githubUrl, String hostedUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Find active hackathon participation
        HackathonParticipation activeParticipation = user.getHackathonParticipations().stream()
                .filter(HackathonParticipation::isActive)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No active hackathon found for user"));

        // Add validation for problem selection
        if (activeParticipation.getSelectedProblem() == null) {
            throw new RuntimeException("Please select a problem before submitting solution");
        }

        // Check if hackathon is still ongoing
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(activeParticipation.getEndTime())) {
            throw new RuntimeException("Hackathon submission period has ended");
        }

        // Create and set solution
        Solution solution = new Solution();
        solution.setGithubUrl(githubUrl);
        solution.setHostedUrl(hostedUrl);
        solution.setSubmissionTime(now);

        activeParticipation.setSolution(solution);
        
        // Save updated user document
        userRepository.save(user);
    }

    @Override
    public Object getHackathonStatus() {
        List<User> allUsers = userRepository.findAll();
        Map<String, Object> status = new HashMap<>();
        
        allUsers.forEach(user -> {
            user.getHackathonParticipations().stream()
                .filter(HackathonParticipation::isActive)
                .forEach(participation -> {
                    Map<String, Object> teamStatus = new HashMap<>();
                    teamStatus.put("teamName", user.getTeamName());
                    teamStatus.put("selectedProblem", participation.getSelectedProblem() != null ? 
                        participation.getSelectedProblem().getTitle() : "Not selected");
                    teamStatus.put("hasSolution", participation.getSolution() != null);
                    
                    status.put(participation.getHackathonId(), teamStatus);
                });
        });
        
        return status;
    }

    @Override
    public void selectProblem(String problemId, String userId) {
        Problem problem = problemRepository.findById(problemId)
            .orElseThrow(() -> new RuntimeException("Problem not found with id: " + problemId));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        HackathonParticipation activeParticipation = user.getHackathonParticipations().stream()
            .filter(HackathonParticipation::isActive)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No active hackathon found for user"));

        // Validate hackathon timing
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(activeParticipation.getEndTime())) {
            throw new RuntimeException("Hackathon has ended. Cannot select problem.");
        }

        // Add validation for already selected problem
        if (activeParticipation.getSelectedProblem() != null) {
            throw new RuntimeException("A problem has already been selected for this hackathon");
        }

        // Set the selected problem ID
        activeParticipation.setSelectedProblem(problem);
        userRepository.save(user);
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

    @Override
    public List<HackathonDTO> getAllHackathons() {
        List<User> allUsers = userRepository.findAll();
        Map<String, HackathonDTO> hackathonsMap = new HashMap<>();

        // Process each user's hackathon participations
        allUsers.forEach(user -> {
            user.getHackathonParticipations().forEach(participation -> {
                String hackathonId = participation.getHackathonId();
                HackathonDTO hackathonDTO = hackathonsMap.computeIfAbsent(hackathonId, k -> {
                    HackathonDTO dto = new HackathonDTO();
                    dto.setHackathonId(hackathonId);
                    dto.setHackathonName(participation.getHackathonName());
                    dto.setStartTime(participation.getStartTime());
                    dto.setEndTime(participation.getEndTime());
                    dto.setActive(participation.isActive());
                    dto.setTeams(new ArrayList<>());
                    return dto;
                });

                // Add team information
                TeamDTO teamDTO = hackathonDTO.getTeams().stream()
                    .filter(t -> t.getTeamName().equals(user.getTeamName()))
                    .findFirst()
                    .orElseGet(() -> {
                        TeamDTO newTeam = new TeamDTO();
                        newTeam.setTeamName(user.getTeamName());
                        newTeam.setMemberNames(new ArrayList<>());
                        newTeam.setHasSolution(false);
                        hackathonDTO.getTeams().add(newTeam);
                        return newTeam;
                    });

                teamDTO.getMemberNames().add(user.getEmail());
                if (participation.getSolution() != null) {
                    teamDTO.setHasSolution(true);
                }
                
                teamDTO.setSelectedProblemTitle(
                    participation.getSelectedProblem() != null ? 
                    participation.getSelectedProblem().getTitle() : 
                    "Not selected"
                );
            });
        });

        return new ArrayList<>(hackathonsMap.values());
    }

    @Override
    public void closeHackathon(String hackathonId) {
        List<User> allUsers = userRepository.findAll();

        allUsers.forEach(user -> {
            user.getHackathonParticipations().stream()
                .filter(participation -> participation.getHackathonId().equals(hackathonId) && participation.isActive())
                .forEach(participation -> {
                    participation.setActive(false);
                    userRepository.save(user);
                });
        });
    }
}