package com.codesurge.hackathon.service.impl;

import com.codesurge.hackathon.service.NotificationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.url}")
    private String appUrl;

    @Async
    @Override
    public void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // true indicates HTML content
            emailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public String processTemplate(String template, Map<String, Object> variables) {
        Context context = new Context();
        variables.forEach(context::setVariable);
        return templateEngine.process(template, context);
    }

    @Async
    @Override
    public void sendWhatsApp(String phoneNumber, String message) {
        // Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        // Message.creator(
        //         new PhoneNumber("whatsapp:" + phoneNumber),
        //         new PhoneNumber("whatsapp:" + FROM_NUMBER),
        //         message)
        //     .create();
    }

    @Override
    public void notifyUserRegistration(String email, String username) {
        String htmlContent = processTemplate("email/registration", Map.of(
                "username", username,
                "message", "Welcome to CodeSurge! Your registration was successful. You can now participate in upcoming hackathons.",
                "actionUrl", appUrl + "/dashboard"
        ));
        sendEmail(email, "Welcome to CodeSurge Hackathon Platform", htmlContent);
    }

    @Override
    public void notifyHackathonAssignment(String email, String phone, String hackathonName, String teamName) {
        String htmlContent = processTemplate("email/registration", Map.of(
                "username", "Participant",
                "message", String.format("You have been assigned to team '%s' for the hackathon '%s'. Get ready to showcase your skills!",
                        teamName, hackathonName),
                "actionUrl", appUrl + "/hackathon/current"
        ));
        sendEmail(email, "Hackathon Assignment - " + hackathonName, htmlContent);

        if (phone != null) {
            sendWhatsApp(phone, String.format("You've been assigned to team '%s' for '%s'!", teamName, hackathonName));
        }
    }

    @Override
    public void notifyTimeRemaining(String email, String phone, String hackathonName, long remainingMinutes) {
        String htmlContent = processTemplate("email/registration", Map.of(
                "username", "Participant",
                "message", String.format("⚠️ Alert: Only %d minutes remaining in %s! Please submit your solution before time runs out.",
                        remainingMinutes, hackathonName),
                "actionUrl", appUrl + "/hackathon/submit"
        ));
        sendEmail(email, "Time Alert - " + hackathonName, htmlContent);

        if (phone != null) {
            sendWhatsApp(phone, String.format("⚠️ %d minutes remaining in %s!", remainingMinutes, hackathonName));
        }
    }

    @Override
    public void notifyHackathonStart(String email, String username, String hackathonName,
                                     String teamName, LocalDateTime startTime, LocalDateTime endTime, int duration) {
        String htmlContent = processTemplate("email/startHackathon", Map.of(
                "username", username,
                "hackathonName", hackathonName,
                "teamName", teamName,
                "startTime", startTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy, hh:mm a")),
                "endTime", endTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy, hh:mm a")),
                "duration", duration,
                "actionUrl", appUrl + "/hackathon/current"
        ));

        sendEmail(email, "Hackathon Started - " + hackathonName, htmlContent);

        // if (phone != null) {
        //     String message = String.format("🚀 Hackathon '%s' has started! You're in team '%s'. Duration: %d hours. Start: %s, End: %s", 
        //         hackathonName, teamName, duration,
        //         startTime.format(DateTimeFormatter.ofPattern("MMM d, hh:mm a")),
        //         endTime.format(DateTimeFormatter.ofPattern("MMM d, hh:mm a")));
        //     sendWhatsApp(phone, message);
        // }
    }

    @Override
    public void notifyHackathonEnded(String email, String username, String hackathonName,
                                     LocalDateTime endTime, String reason) {
        String htmlContent = processTemplate("email/hackathon-ended", Map.of(
                "username", username,
                "hackathonName", hackathonName,
                "endTime", endTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy, hh:mm a")),
                "reason", reason,
                "actionUrl", appUrl + "/hackathon/results"
        ));

        sendEmail(email, "Hackathon Ended - " + hackathonName, htmlContent);
    }

    @Override
    public void notifySolutionSubmitted(String email, String username, String hackathonName,
                                        String githubUrl, String hostedUrl, LocalDateTime submissionTime) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("username", username);
        variables.put("hackathonName", hackathonName);
        variables.put("submissionTime", submissionTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy, hh:mm a")));
        variables.put("githubUrl", githubUrl);
        if (hostedUrl != null) {
            variables.put("hostedUrl", hostedUrl);
        }

        String htmlContent = processTemplate("email/solution-submitted", variables);
        sendEmail(email, "Solution Submitted - " + hackathonName, htmlContent);
    }
}
