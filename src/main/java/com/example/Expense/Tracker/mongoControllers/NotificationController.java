package com.example.Expense.Tracker.mongoControllers;

import com.example.Expense.Tracker.mongoModels.Notification;
import com.example.Expense.Tracker.service.EmailService;
import com.example.Expense.Tracker.service.NotificationService;
import com.example.Expense.Tracker.service.SmsService;
import com.example.Expense.Tracker.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService service;
    private final UserService userService;
    private final EmailService emailService;
    private final SmsService smsService;

    @PostMapping("/create")
    public Notification create(Authentication auth,
                               @RequestParam String message,
                               @RequestParam String type
    ) {
        Long userId = userService.getCurrentUser(auth).getId();
        return service.create(String.valueOf(userId), message, type);
    }

    @GetMapping
    public List<Notification> get(Authentication auth) {
        Long userId = userService.getCurrentUser(auth).getId();
        return service.getUserNotification(userId);
    }

    @PostMapping("/send-email")
    public ResponseEntity<?> sendEmail(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String subject = request.get("subject");
            String body = request.get("body");

            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
            }
            if (subject == null || subject.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Subject is required"));
            }
            if (body == null || body.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Body is required"));
            }

            emailService.sendHtmlEmail(email, subject, body);
            log.info("Email sent successfully to: {}", email);
            
            return ResponseEntity.ok(Map.of("message", "Email sent successfully"));
        } catch (Exception e) {
            log.error("Error sending email: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to send email: " + e.getMessage()));
        }
    }

    @PostMapping("/send-sms")
    public ResponseEntity<?> sendSms(@RequestBody Map<String, String> request) {
        try {
            String phoneNumber = request.get("phoneNumber");
            String message = request.get("message");

            if (phoneNumber == null || phoneNumber.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Phone number is required"));
            }
            if (message == null || message.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Message is required"));
            }

            smsService.sendSMS(phoneNumber, message);
            log.info("SMS sent successfully to: {}", phoneNumber);
            
            return ResponseEntity.ok(Map.of("message", "SMS sent successfully"));
        } catch (Exception e) {
            log.error("Error sending SMS: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to send SMS: " + e.getMessage()));
        }
    }
}
