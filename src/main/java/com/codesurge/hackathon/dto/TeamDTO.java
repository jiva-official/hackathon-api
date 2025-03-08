package com.codesurge.hackathon.dto;

import lombok.Data;

import java.util.List;

@Data
public class TeamDTO {
    private String teamName;
    private List<String> memberNames;
    private boolean hasSolution;
    private String selectedProblemTitle;
}
