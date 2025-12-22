package com.example.Expense.Tracker.mongoControllers;


import com.example.Expense.Tracker.mongoModels.BudgetSettings;
import com.example.Expense.Tracker.service.BudgetSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budget")
@RequiredArgsConstructor
public class BudgetSettingsController {

    @Autowired
    private BudgetSettingsService budgetSettingsService;

    @GetMapping
    public BudgetSettings  get(@RequestParam Long userId){
        return budgetSettingsService.getBudget(userId);
    }

    @PutMapping
    public BudgetSettings update(@RequestParam Long userId, @RequestBody BudgetSettings settings){
        return budgetSettingsService.update(userId, settings);
    }
}
