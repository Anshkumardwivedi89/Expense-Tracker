package com.example.Expense.Tracker.service;

import com.example.Expense.Tracker.entity.User;
import com.example.Expense.Tracker.entity.TransactionType;
import com.example.Expense.Tracker.mongoModels.AnalyticsCache;
import com.example.Expense.Tracker.repo.AnalyticsCacheRepo;
import com.example.Expense.Tracker.repo.TransactionRepository;
import com.example.Expense.Tracker.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsCacheService {

    private final AnalyticsCacheRepo analyticsCacheRepo;

    private final TransactionRepository transactionRepository;

    private final UserRepo userRepo;

    public AnalyticsCache get(Long userId){
        return analyticsCacheRepo.findById(String.valueOf(userId)).orElse(null);
    }

    public AnalyticsCache update(Long userId, Map<String, Map<String,Double>>summary){
        AnalyticsCache cache = AnalyticsCache.builder()
                .userId(String.valueOf(userId))
                .monthlySummary(summary)
                .lastUpdated(LocalDateTime.now())
                .build();

        return analyticsCacheRepo.save(cache);
    }

    // Refresh analytics for all users (simple monthly totals)
    public void refreshAll(){
        List<User> users = userRepo.findAll();
        YearMonth ym = YearMonth.now();
        int month = ym.getMonthValue();
        int year = ym.getYear();
        String key = ym.toString(); // e.g., 2026-02

        for(User u: users){
            Long uid = u.getId();
            Double income = transactionRepository.sumByTypeForMonth(uid, TransactionType.INCOME, month, year);
            Double expense = transactionRepository.sumByTypeForMonth(uid, TransactionType.EXPENSE, month, year);
            income = income == null ? 0.0 : income;
            expense = expense == null ? 0.0 : expense;

            Map<String, Double> monthSummary = new HashMap<>();
            monthSummary.put("totalIncome", income);
            monthSummary.put("totalExpense", expense);

            Map<String, Map<String,Double>> monthly = new HashMap<>();
            monthly.put(key, monthSummary);

            update(uid, monthly);
        }
    }
}
