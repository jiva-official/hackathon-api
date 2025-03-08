package com.codesurge.hackathon.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Solution {
    private String githubUrl;
    private String hostedUrl;
    private LocalDateTime submissionTime;
}