package com.codesurge.hackathon.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HackathonParticipation {
    private String hackathonId;
    private String hackathonName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isActive;
    private Solution solution;
    private Problem selectedProblem;  // Add this field
    private LocalDateTime problemSelectionTime;
}