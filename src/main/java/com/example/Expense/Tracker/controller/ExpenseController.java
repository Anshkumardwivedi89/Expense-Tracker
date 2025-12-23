package com.example.Expense.Tracker.controller;

import com.example.Expense.Tracker.entity.Expense;
import com.example.Expense.Tracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;



    @GetMapping
    public List<Expense> getAllExpenses() {
        return expenseService.getExpenses();
    }

    @PostMapping
    public List<Expense> createExpenses(@RequestBody List<Expense> expenses) {
        return expenseService.saveExpense(expenses);
    }

}
