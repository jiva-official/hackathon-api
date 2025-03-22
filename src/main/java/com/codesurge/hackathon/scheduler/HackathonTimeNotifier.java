// package com.codesurge.hackathon.scheduler;

// import com.codesurge.hackathon.model.User;
// import com.codesurge.hackathon.repository.UserRepository;
// import com.codesurge.hackathon.service.NotificationService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;

// import java.time.Duration;
// import java.time.LocalDateTime;
// import java.util.List;

// @Component
// @RequiredArgsConstructor
// public class HackathonTimeNotifier {

//     private final UserRepository userRepository;
//     private final NotificationService notificationService;

//     @Scheduled(fixedRate = 300000) // Runs every 5 minutes
//     public void checkHackathonTimeAndNotify() {
//         // Get all users who are currently participating in a hackathon
//         List<User> activeParticipants = userRepository.findByCurrentHackathonNotNull();
        
//         LocalDateTime now = LocalDateTime.now();
        
//         activeParticipants.forEach(user -> {
//             if (user.getCurrentHackathon() != null && user.getCurrentHackathon().getEndTime().isAfter(now)) {
//                 LocalDateTime endTime = user.getCurrentHackathon().getEndTime();
//                 Duration timeLeft = Duration.between(now, endTime);
//                 Duration totalDuration = Duration.between(
//                     user.getCurrentHackathon().getStartTime(), 
//                     endTime
//                 );
                
//                 double percentageComplete = (1 - timeLeft.toMinutes() / (double) totalDuration.toMinutes()) * 100;
                
//                 if (percentageComplete >= 80 && !user.isNotifiedForTimeRemaining()) {
//                     notificationService.notifyTimeRemaining(
//                         user.getEmail(),
//                         user.getPhone(),
//                         user.getCurrentHackathon().getName(),
//                         timeLeft.toMinutes()
//                     );
                    
//                     // Update user to mark that they've been notified
//                     user.setNotifiedForTimeRemaining(true);
//                     userRepository.save(user);
//                 }
//             }
//         });
//     }
// }
