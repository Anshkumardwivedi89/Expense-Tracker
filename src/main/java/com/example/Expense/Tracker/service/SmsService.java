package com.example.Expense.Tracker.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

    @Value("${app.sms.twilio.account-sid}")
    private String accountSid;

    @Value("${app.sms.twilio.auth-token}")
    private String authToken;

    @Value("${app.sms.twilio.phone-number}")
    private String twilioPhoneNumber;

    @Value("${app.sms.enabled:false}")
    private boolean smsEnabled;

    private static final int SMS_CHAR_LIMIT = 160;

    /**
     * Initialize Twilio SDK on first use
     */
    private void initializeTwilio() {
        if (accountSid != null && authToken != null) {
            Twilio.init(accountSid, authToken);
        }
    }

    /**
     * Send SMS to a phone number using Twilio
     */
    public boolean sendSMS(String phoneNumber, String message) {
        if (!smsEnabled) {
            log.warn("SMS is disabled. Message not sent to {}", phoneNumber);
            return false;
        }

        if (phoneNumber == null || phoneNumber.isEmpty()) {
            log.error("Phone number is null or empty");
            return false;
        }

        try {
            // Initialize Twilio
            initializeTwilio();

            // Truncate message if too long
            String truncatedMessage = message.length() > SMS_CHAR_LIMIT ?
                    message.substring(0, SMS_CHAR_LIMIT - 3) + "..." :
                    message;

            // Send SMS using Twilio
            Message twilioMessage = Message.creator(
                    new PhoneNumber(phoneNumber),      // To number
                    new PhoneNumber(twilioPhoneNumber), // From number
                    truncatedMessage                    // Message body
            ).create();

            log.info("SMS sent successfully to {}. Message SID: {}", phoneNumber, twilioMessage.getSid());
            return true;

        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Send transaction notification SMS
     */
    public boolean sendTransactionSMS(String phoneNumber, String type, Double amount, Double balance) {
        String message = String.format(
                "Expense Tracker: %s of ₹%.2f recorded. Current balance: ₹%.2f",
                type,
                amount,
                balance
        );
        return sendSMS(phoneNumber, message);
    }

    /**
     * Send budget alert SMS
     */
    public boolean sendBudgetAlertSMS(String phoneNumber, String categoryName, Double spent, Double limit) {
        String message = String.format(
                "Budget Alert: %s spending ₹%.2f / ₹%.2f limit exceeded!",
                categoryName,
                spent,
                limit
        );
        return sendSMS(phoneNumber, message);
    }

    /**
     * Send weekly summary SMS
     */
    public boolean sendWeeklySummarySMS(String phoneNumber, Double totalIncome, Double totalExpense, Double balance) {
        String message = String.format(
                "Weekly Summary - Income: ₹%.2f, Expense: ₹%.2f, Balance: ₹%.2f",
                totalIncome,
                totalExpense,
                balance
        );
        return sendSMS(phoneNumber, message);
    }
}
