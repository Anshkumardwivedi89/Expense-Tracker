package com.example.Expense.Tracker.controller;

import com.example.Expense.Tracker.entity.TransactionType;
import com.example.Expense.Tracker.repo.TransactionRepository;
import com.example.Expense.Tracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportsController {

    private final TransactionRepository repo;

    private final UserService userService;

    @GetMapping("/expense-by-category")
    public List<Map<String, Object>> expenseByCategory(
            @RequestParam int month,
            @RequestParam int year) {

        return repo.expenseByCategory(month, year)
                .stream()
                .map(r -> Map.of(
                        "category", r[0],
                        "amount", r[1]
                ))
                .toList();
    }

    @GetMapping("/income-vs-expense")
    public Map<String, Double> incomeVsExpense(
            Authentication auth,
            @RequestParam int month,
            @RequestParam int year) {

        Long userId = userService.getCurrentUser(auth).getId();

        Double income = repo.sumByTypeForMonth(userId, TransactionType.INCOME, month, year);
        Double expense = repo.sumByTypeForMonth(userId, TransactionType.EXPENSE, month, year);

        income = income == null ? 0.0 : income;
        expense = expense == null ? 0.0 : expense;

        return Map.of(
                "income", income,
                "expense", expense
        );
    }
}
