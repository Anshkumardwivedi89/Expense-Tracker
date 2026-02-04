package com.example.Expense.Tracker.controller;

import com.example.Expense.Tracker.entity.TransactionType;
import com.example.Expense.Tracker.repo.TransactionRepository;
import com.example.Expense.Tracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final TransactionRepository repo;
    private final UserService userService;

    @GetMapping("/summary")
    public Map<String, Double> summary(Authentication auth) {

        Long userId = userService.getCurrentUser(auth).getId();

        Double income = repo.sumByType(userId, TransactionType.INCOME);
        Double expense = repo.sumByType(userId, TransactionType.EXPENSE);

        income = income == null ? 0 : income;
        expense = expense == null ? 0 : expense;

        return Map.of(
                "totalIncome", income,
                "totalExpense", expense,
                "currentBalance", income - expense
        );
    }
}
