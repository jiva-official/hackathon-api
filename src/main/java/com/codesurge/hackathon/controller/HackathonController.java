package com.codesurge.hackathon.controller;

import com.codesurge.hackathon.dto.HackathonDTO;
import com.codesurge.hackathon.dto.SuccessResponse;
import com.codesurge.hackathon.exception.ErrorResponse;
import com.codesurge.hackathon.exception.HackathonException;
import com.codesurge.hackathon.exception.HackathonServiceException;
import com.codesurge.hackathon.model.Problem;
import com.codesurge.hackathon.service.HackathonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/hackathon")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class HackathonController {

    private final HackathonService hackathonService;

    @PostMapping("/problems")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addProblem(@Valid @RequestBody Problem problem) {
        log.info("Adding new problem: {}", problem.getTitle());
        try {
            Problem savedProblem = hackathonService.addProblem(problem);
            log.info("Problem added successfully with ID: {}", savedProblem.getId());
            return ResponseEntity.ok(savedProblem);
        } catch (HackathonException e) {
            log.error("Failed to add problem: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getErrorCode(), e.getMessage(), LocalDateTime.now()));
        }
    }

    @GetMapping("/problems")
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Problem>> getAllProblems() {
        return ResponseEntity.ok(hackathonService.getAllProblems());
    }

    @PostMapping("/start")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> startHackathon(
        @RequestParam String hackathonName,
        @RequestParam List<String> teamNames,
        @RequestParam Integer durationInHours,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        
        hackathonService.startHackathon(hackathonName, teamNames, durationInHours, startTime);
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

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<HackathonDTO>> getAllHackathons() {
        return ResponseEntity.ok(hackathonService.getAllHackathons());
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Object> getHackathonStatus() {
        return ResponseEntity.ok(hackathonService.getHackathonStatus());
    }

    @PostMapping("/problems/{problemId}/{userId}/{hackathonId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> selectProblem(@PathVariable String problemId,
                                           @PathVariable String userId,
                                           @PathVariable String hackathonId) {
        log.info("Received request to select problem {} for user {} in hackathon {}", 
            problemId, userId, hackathonId);
        try {
            hackathonService.selectProblem(problemId, userId, hackathonId);
            return ResponseEntity.ok()
                .body(new SuccessResponse("Problem selected successfully"));
        } catch (HackathonServiceException e) {
            log.error("Failed to select problem: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getErrorCode(), e.getMessage(), LocalDateTime.now()));
        }
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

    @PostMapping("/close/{hackathonId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> closeHackathon(@PathVariable String hackathonId) {
        hackathonService.closeHackathon(hackathonId);
        return ResponseEntity.ok().build();
    }

}