package com.codesurge.hackathon.service;

import java.time.LocalDateTime;
import java.util.Map;

public interface NotificationService {
    void sendEmail(String to, String subject, String content);

    void sendWhatsApp(String phoneNumber, String message);

    void notifyUserRegistration(String email, String username);

    void notifyHackathonAssignment(String email, String phone, String hackathonName, String teamName);

    void notifyTimeRemaining(String email, String phone, String hackathonName, long remainingMinutes);

    void notifyHackathonStart(String email, String username, String hackathonName,
                              String teamName, LocalDateTime startTime, LocalDateTime endTime, int duration);

    void notifyHackathonEnded(String email, String username, String hackathonName,
                              LocalDateTime endTime, String reason);

    void notifySolutionSubmitted(String email, String username, String hackathonName,
                                 String githubUrl, String hostedUrl, LocalDateTime submissionTime);

    String processTemplate(String template, Map<String, Object> variables);
    void notifyProblemSelection(String email, String username, String hackathonName, 
                          String problemTitle, LocalDateTime endTime);

    void notifyHackathonScheduled(String email, String username, String hackathonName, String teamName,
            LocalDateTime startTime, LocalDateTime endTime, Integer durationInHours);
}