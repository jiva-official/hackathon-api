package com.codesurge.hackathon.controller;

import com.codesurge.hackathon.model.Problem;
import com.codesurge.hackathon.service.HackathonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hackathon")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HackathonController {

    private final HackathonService hackathonService;

    @PostMapping("/problems")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Problem> addProblem(@Valid @RequestBody Problem problem) {
        return ResponseEntity.ok(hackathonService.addProblem(problem));
    }

    @GetMapping("/problems")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Problem>> getAllProblems() {
        return ResponseEntity.ok(hackathonService.getAllProblems());
    }

    @PostMapping("/start")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> startHackathon(@RequestParam String hackathonName,
            @RequestParam List<String> teamNames,
            @RequestParam Integer durationInHours) {
        hackathonService.startHackathon(hackathonName, teamNames, durationInHours);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/submit/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> submitSolution(@PathVariable String userId,
            @RequestParam String githubUrl,
            @RequestParam(required = false) String hostedUrl) {
        hackathonService.submitSolution(userId, githubUrl, hostedUrl);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Object> getHackathonStatus() {
        return ResponseEntity.ok(hackathonService.getHackathonStatus());
    }

    @PostMapping("/problems/{problemId}/select")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> selectProblem(@PathVariable String problemId,
            @RequestParam String userId) {
        hackathonService.selectProblem(problemId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/problems/{problemId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Problem> getProblem(@PathVariable String problemId) {
        return ResponseEntity.ok(hackathonService.getProblem(problemId));
    }

    @DeleteMapping("/problems/{problemId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProblem(@PathVariable String problemId) {
        hackathonService.deleteProblem(problemId);
        return ResponseEntity.ok().build();
    }
}