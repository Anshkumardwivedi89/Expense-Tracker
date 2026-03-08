package com.example.Expense.Tracker.service;

import com.example.Expense.Tracker.entity.Transaction;
import com.example.Expense.Tracker.entity.TransactionType;
import com.example.Expense.Tracker.repo.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionRepository transactionRepository;

    /**
     * Get weekly report for a user starting from Monday
     */
    public WeeklyReportData getWeeklyReport(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(userId, weekStart, weekEnd);

        double totalIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpense = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double balance = totalIncome - totalExpense;

        String topCategory = findTopCategory(transactions);
        Transaction largestTransaction = transactions.stream()
                .max((t1, t2) -> Double.compare(t1.getAmount(), t2.getAmount()))
                .orElse(null);

        double averageAmount = transactions.isEmpty() ? 0 :
                transactions.stream().mapToDouble(Transaction::getAmount).average().orElse(0);

        return WeeklyReportData.builder()
                .weekStart(weekStart)
                .weekEnd(weekEnd)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .balance(balance)
                .transactionCount(transactions.size())
                .topCategory(topCategory)
                .largestTransaction(largestTransaction)
                .averageTransactionAmount(averageAmount)
                .transactions(transactions)
                .build();
    }

    /**
     * Get monthly report for a user
     */
    public MonthlyReportData getMonthlyReport(Long userId, int month, int year) {
        LocalDate monthStart = LocalDate.of(year, month, 1);
        LocalDate monthEnd = monthStart.with(TemporalAdjusters.lastDayOfMonth());

        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(userId, monthStart, monthEnd);

        double totalIncome = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpense = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double balance = totalIncome - totalExpense;

        // Category breakdown for expenses
        Map<String, Double> categoryBreakdown = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory() != null ? t.getCategory().getName() : "Uncategorized",
                        Collectors.summingDouble(Transaction::getAmount)
                ));

        return MonthlyReportData.builder()
                .monthStart(monthStart)
                .monthEnd(monthEnd)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .balance(balance)
                .transactionCount(transactions.size())
                .categoryBreakdown(categoryBreakdown)
                .transactions(transactions)
                .build();
    }

    /**
     * Find top spending category
     */
    public String findTopCategory(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory() != null ? t.getCategory().getName() : "Uncategorized",
                        Collectors.summingDouble(Transaction::getAmount)
                ))
                .entrySet().stream()
                .max((e1, e2) -> Double.compare(e1.getValue(), e2.getValue()))
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }

    @Data
    @Builder
    public static class WeeklyReportData {
        private LocalDate weekStart;
        private LocalDate weekEnd;
        private double totalIncome;
        private double totalExpense;
        private double balance;
        private int transactionCount;
        private String topCategory;
        private Transaction largestTransaction;
        private double averageTransactionAmount;
        private List<Transaction> transactions;
    }

    @Data
    @Builder
    public static class MonthlyReportData {
        private LocalDate monthStart;
        private LocalDate monthEnd;
        private double totalIncome;
        private double totalExpense;
        private double balance;
        private int transactionCount;
        private Map<String, Double> categoryBreakdown;
        private List<Transaction> transactions;
    }
}
