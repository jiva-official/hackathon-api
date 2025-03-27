package com.codesurge.hackathon.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class HackathonDTO {
    private String hackathonId;
    private String hackathonName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean active;
    private List<TeamDTO> teams;
}

