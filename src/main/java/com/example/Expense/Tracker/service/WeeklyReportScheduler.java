package com.example.Expense.Tracker.service;

import com.example.Expense.Tracker.entity.User;
import com.example.Expense.Tracker.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class WeeklyReportScheduler {

    private final EmailService emailService;
    private final ReportService reportService;
    private final UserRepo userRepo;

    @Value("${app.notifications.report.enabled:true}")
    private boolean reportNotificationsEnabled;

    /**
     * Send weekly reports every Monday at 9:00 AM IST
     * Cron: minute hour day month dayOfWeek
     * 0 0 9 ? * MON = 9:00 AM every Monday
     */
    @Scheduled(cron = "0 0 9 ? * MON", zone = "Asia/Kolkata")
    public void sendWeeklyReports() {
        if (!reportNotificationsEnabled) {
            log.info("Weekly report notifications are disabled");
            return;
        }

        log.info("Starting weekly report generation and sending...");
        List<User> users = userRepo.findAll();

        for (User user : users) {
            try {
                sendReportToUser(user);
            } catch (Exception e) {
                log.error("Error sending weekly report to user {}: {}", user.getId(), e.getMessage(), e);
            }
        }

        log.info("Weekly report sending completed");
    }

    /**
     * Send report to individual user
     */
    private void sendReportToUser(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            log.warn("User {} has no email address, skipping weekly report", user.getId());
            return;
        }

        ReportService.WeeklyReportData reportData = reportService.getWeeklyReport(user.getId());
        String htmlReport = buildWeeklyReportHTML(reportData, user.getUsername());

        emailService.sendWeeklyReport(user.getEmail(), user.getUsername(), htmlReport);
        log.info("Weekly report sent to user: {} ({})", user.getId(), user.getEmail());
    }

    /**
     * Build professional HTML weekly report
     */
    private String buildWeeklyReportHTML(ReportService.WeeklyReportData reportData, String userName) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        
        StringBuilder transactionsTable = new StringBuilder();
        for (var transaction : reportData.getTransactions()) {
            String typeClass = transaction.getType().toString().toLowerCase();
            transactionsTable.append("<tr style=\"border-bottom: 1px solid #eee;\">")
                    .append("<td style=\"padding: 10px;\">").append(transaction.getDate()).append("</td>")
                    .append("<td style=\"padding: 10px;\">").append(transaction.getCategory() != null ? transaction.getCategory().getName() : "N/A").append("</td>")
                    .append("<td style=\"padding: 10px; text-align: right; color: ").append(typeClass.equals("expense") ? "#c33" : "#3c3").append(";\">")
                    .append(String.format("₹%.2f", transaction.getAmount())).append("</td>")
                    .append("<td style=\"padding: 10px;\">").append(transaction.getDescription() != null ? transaction.getDescription() : "-").append("</td>")
                    .append("</tr>");
        }

        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "* { margin: 0; padding: 0; box-sizing: border-box; }" +
                "body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333; background: #f5f5f5; }" +
                ".container { max-width: 800px; margin: 0 auto; padding: 20px; }" +
                ".email-wrapper { background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }" +
                ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 40px 30px; text-align: center; }" +
                ".header h1 { font-size: 28px; margin-bottom: 10px; }" +
                ".header p { font-size: 14px; opacity: 0.9; }" +
                ".content { padding: 40px 30px; }" +
                ".greeting { font-size: 16px; margin-bottom: 30px; }" +
                ".cards { display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 20px; margin: 30px 0; }" +
                ".card { padding: 20px; border-radius: 8px; text-align: center; }" +
                ".card h3 { font-size: 12px; color: #999; text-transform: uppercase; margin-bottom: 10px; }" +
                ".card .amount { font-size: 24px; font-weight: bold; }" +
                ".card.income { background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%); color: #27ae60; }" +
                ".card.expense { background: linear-gradient(135deg, #fed6e3 0%, #ffeaa7 100%); color: #e74c3c; }" +
                ".card.balance { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; }" +
                ".card.count { background: linear-gradient(135deg, #ffeaa7 0%, #fab1a0 100%); color: #d63031; }" +
                ".insights { background: #f9f9f9; padding: 20px; border-radius: 8px; margin: 30px 0; border-left: 4px solid #667eea; }" +
                ".insights h4 { color: #667eea; margin-bottom: 15px; }" +
                ".insight-item { margin-bottom: 10px; font-size: 14px; }" +
                ".transactions-section { margin-top: 30px; }" +
                ".transactions-section h4 { margin: 20px 0 15px 0; color: #333; }" +
                "table { width: 100%; border-collapse: collapse; font-size: 13px; }" +
                "th { background: #f5f5f5; padding: 12px; text-align: left; font-weight: bold; color: #666; }" +
                "td { padding: 10px; }" +
                ".footer { background: #f5f5f5; padding: 20px 30px; text-align: center; font-size: 12px; color: #999; border-top: 1px solid #eee; }" +
                ".footer p { margin-bottom: 5px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"container\">" +
                "<div class=\"email-wrapper\">" +
                "<div class=\"header\">" +
                "<h1>📊 Weekly Expense Report</h1>" +
                "<p>" + reportData.getWeekStart().format(formatter) + " to " + reportData.getWeekEnd().format(formatter) + "</p>" +
                "</div>" +
                "<div class=\"content\">" +
                "<div class=\"greeting\">" +
                "<p>Hi " + userName + ",</p>" +
                "<p>Here's your weekly expense summary:</p>" +
                "</div>" +
                "<div class=\"cards\">" +
                "<div class=\"card income\">" +
                "<h3>Income</h3>" +
                "<div class=\"amount\">₹" + String.format("%.0f", reportData.getTotalIncome()) + "</div>" +
                "</div>" +
                "<div class=\"card expense\">" +
                "<h3>Expense</h3>" +
                "<div class=\"amount\">₹" + String.format("%.0f", reportData.getTotalExpense()) + "</div>" +
                "</div>" +
                "<div class=\"card balance\">" +
                "<h3>Balance</h3>" +
                "<div class=\"amount\">₹" + String.format("%.0f", reportData.getBalance()) + "</div>" +
                "</div>" +
                "<div class=\"card count\">" +
                "<h3>Transactions</h3>" +
                "<div class=\"amount\">" + reportData.getTransactionCount() + "</div>" +
                "</div>" +
                "</div>" +
                "<div class=\"insights\">" +
                "<h4>📈 Key Insights</h4>" +
                "<div class=\"insight-item\"><strong>Top Category:</strong> " + reportData.getTopCategory() + "</div>" +
                "<div class=\"insight-item\"><strong>Largest Transaction:</strong> ₹" + (reportData.getLargestTransaction() != null ? String.format("%.2f", reportData.getLargestTransaction().getAmount()) : "N/A") + "</div>" +
                "<div class=\"insight-item\"><strong>Average Transaction:</strong> ₹" + String.format("%.2f", reportData.getAverageTransactionAmount()) + "</div>" +
                "</div>" +
                "<div class=\"transactions-section\">" +
                "<h4>📝 Transaction Details</h4>" +
                "<table>" +
                "<thead><tr style=\"background: #f5f5f5;\"><th>Date</th><th>Category</th><th>Amount</th><th>Description</th></tr></thead>" +
                "<tbody>" + transactionsTable.toString() + "</tbody>" +
                "</table>" +
                "</div>" +
                "</div>" +
                "<div class=\"footer\">" +
                "<p>This is an automated weekly report from Expense Tracker.</p>" +
                "<p>© 2024 Expense Tracker. All rights reserved.</p>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
