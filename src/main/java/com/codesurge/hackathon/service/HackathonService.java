package com.codesurge.hackathon.service;

import com.codesurge.hackathon.dto.HackathonDTO;
import com.codesurge.hackathon.model.Problem;

import java.time.LocalDateTime;
import java.util.List;

public interface HackathonService {
    Problem addProblem(Problem problem);

    List<Problem> getAllProblems();

    void startHackathon(String hackathonName, List<String> teamIds, Integer durationInHours, LocalDateTime startTime);

    void submitSolution(String teamId, String githubUrl, String hostedUrl);

    Object getHackathonStatus();

    void selectProblem(String problemId, String userId, String hackathonId);

    Problem getProblem(String problemId);

    void deleteProblem(String problemId);

    List<HackathonDTO> getAllHackathons();

    void closeHackathon(String hackathonId);
}