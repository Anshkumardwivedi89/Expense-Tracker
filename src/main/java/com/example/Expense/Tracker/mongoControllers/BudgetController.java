package com.example.Expense.Tracker.mongoControllers;

import com.example.Expense.Tracker.entity.Budget;
import com.example.Expense.Tracker.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @Autowired
    BudgetService budgetService;


    @PostMapping
    public Budget Create(@RequestParam Long userId, @RequestBody Budget budget){
        return  budgetService.createBudget(userId,budget);

    }

    public List<Budget> getBudgets(@RequestParam Long userId){
        return budgetService.getBudgets(userId);
    }





}
