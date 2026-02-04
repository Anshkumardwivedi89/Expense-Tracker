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
}
