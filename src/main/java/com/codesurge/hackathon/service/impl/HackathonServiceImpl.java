package com.codesurge.hackathon.service.impl;

import com.codesurge.hackathon.service.HackathonService;
import com.codesurge.hackathon.model.Problem;
import com.codesurge.hackathon.repository.ProblemRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HackathonServiceImpl implements HackathonService {

    private final ProblemRepository problemRepository;

    public HackathonServiceImpl(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
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
    public void startHackathon(String hackathonName, List<String> teamIds, Integer durationInHours) {
        // Implement hackathon start logic
    }

    @Override
    public void submitSolution(String teamId, String githubUrl, String hostedUrl) {
        // Implement solution submission logic
    }

    @Override
    public Object getHackathonStatus() {
        // Implement status retrieval logic
        return null;
    }

    @Override
    public void selectProblem(String problemId, String teamId) {
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