package com.example.Expense.Tracker.mongoControllers;

import com.example.Expense.Tracker.entity.Budget;
import com.example.Expense.Tracker.service.BudgetService;
import com.example.Expense.Tracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    private final UserService userService;

    @PostMapping
    public Budget create(Authentication auth, @RequestBody Budget budget){
        Long userId = userService.getCurrentUser(auth).getId();
        return  budgetService.createBudget(userId,budget);

    }

    @GetMapping
    public List<Budget> getBudgets(Authentication auth){
        Long userId = userService.getCurrentUser(auth).getId();
        return budgetService.getBudgets(userId);
    }

}
