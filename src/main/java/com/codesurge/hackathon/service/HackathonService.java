package com.codesurge.hackathon.service;

    import com.codesurge.hackathon.model.Problem;
    import java.util.List;

    public interface HackathonService {
        Problem addProblem(Problem problem);
        List<Problem> getAllProblems();
        void startHackathon(String hackathonName, List<String> teamIds, Integer durationInHours);
        void submitSolution(String teamId, String githubUrl, String hostedUrl);
        Object getHackathonStatus();
        void selectProblem(String problemId, String teamId);
        Problem getProblem(String problemId);
        void deleteProblem(String problemId);
    }