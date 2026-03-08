package com.example.Expense.Tracker.service;

import com.example.Expense.Tracker.entity.TransactionType;
import com.example.Expense.Tracker.repo.TransactionRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;

    /**
     * Get dashboard summary for a user
     */
    public DashboardSummary getSummary(Long userId) {
        return getSummaryForUser(userId);
    }

    /**
     * Get dashboard summary for any user
     */
    public DashboardSummary getSummaryForUser(Long userId) {
        Double totalIncome = transactionRepository.sumByType(userId, TransactionType.INCOME);
        Double totalExpense = transactionRepository.sumByType(userId, TransactionType.EXPENSE);
        
        totalIncome = totalIncome != null ? totalIncome : 0.0;
        totalExpense = totalExpense != null ? totalExpense : 0.0;
        
        double currentBalance = totalIncome - totalExpense;

        return DashboardSummary.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .currentBalance(currentBalance)
                .transactionCount(transactionRepository.findByUserIdOrderByDateDesc(userId).size())
                .build();
    }

    @Data
    @Builder
    public static class DashboardSummary {
        private Double totalIncome;
        private Double totalExpense;
        private Double currentBalance;
        private int transactionCount;
    }
}
