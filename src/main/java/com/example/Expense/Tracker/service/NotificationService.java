package com.example.Expense.Tracker.service;

import com.example.Expense.Tracker.entity.User;
import com.example.Expense.Tracker.mongoModels.Notification;
import com.example.Expense.Tracker.repo.NotificationRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepo repo;
    private final EmailService emailService;
    private final SmsService smsService;

    @Value("${app.notifications.transaction.enabled:true}")
    private boolean transactionNotificationsEnabled;

    @Value("${app.notifications.category.enabled:true}")
    private boolean categoryNotificationsEnabled;

    @Value("${app.notifications.report.enabled:true}")
    private boolean reportNotificationsEnabled;

    /**
     * Send transaction notification via email and SMS
     */
    public void notifyTransaction(User user, String type, Double amount, String category,
                                 String description, String date, Double balance) {
        if (!transactionNotificationsEnabled) {
            log.debug("Transaction notifications are disabled");
            return;
        }

        // Send email
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            emailService.sendTransactionNotification(
                    user.getEmail(),
                    user.getUsername(),
                    type,
                    amount,
                    category,
                    description,
                    date,
                    balance
            );
        }

        // Send SMS
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
            smsService.sendTransactionSMS(
                    user.getPhoneNumber(),
                    type,
                    amount,
                    balance
            );
        }

        // Store in database
        String message = String.format("%s of ₹%.2f on %s", type, amount, date);
        create(String.valueOf(user.getId()), message, "TRANSACTION");
    }

    /**
     * Send category notification via email
     */
    public void notifyCategory(User user, String categoryName, String categoryType) {
        if (!categoryNotificationsEnabled) {
            log.debug("Category notifications are disabled");
            return;
        }

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            emailService.sendCategoryNotification(
                    user.getEmail(),
                    user.getUsername(),
                    categoryName,
                    categoryType
            );
        }

        create(String.valueOf(user.getId()), "Category " + categoryName + " created", "CATEGORY");
    }

    /**
     * Send weekly report via email
     */
    public void sendWeeklyReport(User user, String reportHtml) {
        if (!reportNotificationsEnabled) {
            log.debug("Report notifications are disabled");
            return;
        }

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            emailService.sendWeeklyReport(user.getEmail(), user.getUsername(), reportHtml);
        }

        create(String.valueOf(user.getId()), "Weekly report generated", "REPORT");
    }

    /**
     * Create notification record in MongoDB
     */
    public Notification create(String userId, String message, String type) {
        Notification n = Notification.builder()
                .userId(userId)
                .message(message)
                .type(type)
                .createdAt(LocalDateTime.now())
                .sent(true)
                .build();

        return repo.save(n);
    }

    /**
     * Get user notifications
     */
    public List<Notification> getUserNotification(Long userId) {
        return repo.findByUserId(String.valueOf(userId));
    }

    /**
     * Mark notification as sent
     */
    public void markSent(String id) {
        repo.findById(id).ifPresent(n -> {
            n.setSent(true);
            repo.save(n);
        });
    }
}
