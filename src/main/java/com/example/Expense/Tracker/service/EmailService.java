package com.example.Expense.Tracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.from-name}")
    private String fromName;

    /**
     * Send a simple text email
     */
    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);

            mailSender.send(message);
            log.info("Simple email sent to: {}", to);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send simple email to {}: {}", to, e.getMessage(), e);
        }
    }

    /**
     * Send an HTML email
     */
    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML

            mailSender.send(message);
            log.info("HTML email sent to: {}", to);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send HTML email to {}: {}", to, e.getMessage(), e);
        }
    }

    /**
     * Send transaction notification email
     */
    public void sendTransactionNotification(String email, String name, String type, Double amount,
                                           String category, String description, String date, Double balance) {
        String subject = "Transaction Alert - " + type;
        String htmlBody = buildTransactionEmailHTML(name, type, amount, category, description, date, balance);
        sendHtmlEmail(email, subject, htmlBody);
    }

    /**
     * Send category notification email
     */
    public void sendCategoryNotification(String email, String name, String categoryName, String categoryType) {
        String subject = "New Category Created - " + categoryName;
        String htmlBody = buildCategoryEmailHTML(name, categoryName, categoryType);
        sendHtmlEmail(email, subject, htmlBody);
    }

    /**
     * Send weekly report email
     */
    public void sendWeeklyReport(String email, String name, String reportHtml) {
        String subject = "Your Weekly Expense Report";
        sendHtmlEmail(email, subject, reportHtml);
    }

    /**
     * Build transaction email HTML
     */
    private String buildTransactionEmailHTML(String name, String type, Double amount, String category,
                                            String description, String date, Double balance) {
        String typeColor = type.equalsIgnoreCase("EXPENSE") ? "#e74c3c" : "#27ae60";
        String typeBgColor = type.equalsIgnoreCase("EXPENSE") ? "#fdeaea" : "#eafaf1";
        
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<style>" +
                "body { margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background-color: #f5f5f5; }" +
                ".container { max-width: 600px; margin: 20px auto; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }" +
                ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 40px 30px; text-align: center; }" +
                ".header h1 { margin: 0; font-size: 28px; font-weight: 600; }" +
                ".content { padding: 40px 30px; }" +
                ".greeting { font-size: 16px; color: #333; margin-bottom: 30px; }" +
                ".amount-box { background: " + typeBgColor + "; border-left: 5px solid " + typeColor + "; padding: 20px; margin: 25px 0; border-radius: 4px; }" +
                ".amount-box .amount { font-size: 36px; font-weight: bold; color: " + typeColor + "; margin: 10px 0; }" +
                ".amount-box .type { font-size: 14px; text-transform: uppercase; letter-spacing: 1px; color: " + typeColor + "; font-weight: 600; }" +
                ".details-box { background: #f8f9fa; border-radius: 4px; padding: 20px; margin: 20px 0; }" +
                ".detail-item { display: table; width: 100%; margin-bottom: 15px; }" +
                ".detail-item:last-child { margin-bottom: 0; }" +
                ".detail-label { display: table-cell; width: 35%; font-weight: 600; color: #555; padding-right: 15px; vertical-align: top; font-size: 13px; }" +
                ".detail-value { display: table-cell; color: #333; font-size: 14px; vertical-align: top; word-break: break-word; }" +
                ".divider { height: 1px; background: #e0e0e0; margin: 15px 0; }" +
                ".balance-info { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; border-radius: 4px; margin-top: 25px; }" +
                ".balance-label { font-size: 13px; opacity: 0.9; margin-bottom: 5px; }" +
                ".balance-amount { font-size: 28px; font-weight: bold; }" +
                ".footer { background: #f5f5f5; padding: 20px 30px; text-align: center; border-top: 1px solid #e0e0e0; font-size: 12px; color: #999; }" +
                ".footer p { margin: 5px 0; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>💰 Transaction Alert</h1>" +
                "</div>" +
                "<div class='content'>" +
                "<div class='greeting'>Hi " + name + ",</div>" +
                "<p style='color: #666; font-size: 14px;'>A new transaction has been recorded to your account.</p>" +
                "<div class='amount-box'>" +
                "<div class='type'>" + type + "</div>" +
                "<div class='amount'>₹" + String.format("%.2f", amount) + "</div>" +
                "</div>" +
                "<div class='details-box'>" +
                "<div class='detail-item'>" +
                "<div class='detail-label'>Category</div>" +
                "<div class='detail-value'>" + category + "</div>" +
                "</div>" +
                "<div class='divider'></div>" +
                "<div class='detail-item'>" +
                "<div class='detail-label'>Description</div>" +
                "<div class='detail-value'>" + (description != null && !description.isEmpty() ? description : "—") + "</div>" +
                "</div>" +
                "<div class='divider'></div>" +
                "<div class='detail-item'>" +
                "<div class='detail-label'>Date</div>" +
                "<div class='detail-value'>" + date + "</div>" +
                "</div>" +
                "</div>" +
                "<div class='balance-info'>" +
                "<div class='balance-label'>Current Account Balance</div>" +
                "<div class='balance-amount'>₹" + String.format("%.2f", balance) + "</div>" +
                "</div>" +
                "<p style='color: #999; font-size: 12px; margin-top: 25px; text-align: center;'>This is an automated notification from Expense Tracker. Please do not reply to this email.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>Expense Tracker © 2024</p>" +
                "<p>Stay in control of your finances</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    /**
     * Build category email HTML
     */
    private String buildCategoryEmailHTML(String name, String categoryName, String categoryType) {
        String typeColor = categoryType.equalsIgnoreCase("EXPENSE") ? "#e74c3c" : "#27ae60";
        String typeBgColor = categoryType.equalsIgnoreCase("EXPENSE") ? "#fdeaea" : "#eafaf1";
        
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<style>" +
                "body { margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background-color: #f5f5f5; }" +
                ".container { max-width: 600px; margin: 20px auto; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }" +
                ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 40px 30px; text-align: center; }" +
                ".header h1 { margin: 0; font-size: 28px; font-weight: 600; }" +
                ".content { padding: 40px 30px; }" +
                ".greeting { font-size: 16px; color: #333; margin-bottom: 30px; }" +
                ".category-box { background: " + typeBgColor + "; border-left: 5px solid " + typeColor + "; padding: 30px; margin: 25px 0; border-radius: 4px; text-align: center; }" +
                ".category-name { font-size: 32px; font-weight: bold; color: " + typeColor + "; margin: 15px 0; }" +
                ".category-type { font-size: 13px; text-transform: uppercase; letter-spacing: 1px; color: " + typeColor + "; font-weight: 600; }" +
                ".benefits { background: #f8f9fa; border-radius: 4px; padding: 20px; margin: 25px 0; }" +
                ".benefit-title { font-weight: 600; color: #333; margin-bottom: 15px; }" +
                ".benefit-item { padding: 8px 0; color: #555; font-size: 14px; }" +
                ".benefit-item:before { content: '✓ '; color: #27ae60; font-weight: bold; margin-right: 8px; }" +
                ".footer { background: #f5f5f5; padding: 20px 30px; text-align: center; border-top: 1px solid #e0e0e0; font-size: 12px; color: #999; }" +
                ".footer p { margin: 5px 0; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>📂 New Category Created</h1>" +
                "</div>" +
                "<div class='content'>" +
                "<div class='greeting'>Hi " + name + ",</div>" +
                "<p style='color: #666; font-size: 14px;'>You have successfully created a new category for organizing your transactions.</p>" +
                "<div class='category-box'>" +
                "<div class='category-type'>" + categoryType + "</div>" +
                "<div class='category-name'>" + categoryName + "</div>" +
                "</div>" +
                "<div class='benefits'>" +
                "<div class='benefit-title'>What you can do now:</div>" +
                "<div class='benefit-item'>Organize transactions by this new category</div>" +
                "<div class='benefit-item'>Track spending patterns for " + categoryName + "</div>" +
                "<div class='benefit-item'>Generate reports with this category breakdown</div>" +
                "<div class='benefit-item'>Set budgets specific to this category</div>" +
                "</div>" +
                "<p style='color: #666; font-size: 14px; text-align: center;'>Start using this category to better organize and track your finances.</p>" +
                "<p style='color: #999; font-size: 12px; margin-top: 25px; text-align: center;'>This is an automated notification from Expense Tracker.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>Expense Tracker © 2024</p>" +
                "<p>Manage your money with confidence</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
