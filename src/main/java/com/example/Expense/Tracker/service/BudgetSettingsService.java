package com.example.Expense.Tracker.service;


import com.example.Expense.Tracker.mongoModels.BudgetSettings;
import com.example.Expense.Tracker.repo.BudgetSettingsReop;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class BudgetSettingsService {


    @Autowired
    private BudgetSettingsReop repo;

    public BudgetSettings getBudget(Long userId) {
        String id = String.valueOf(userId);
        return repo.findById(id).orElseGet(() -> createDefault(id));
    }

    public BudgetSettings update(Long userId, BudgetSettings b ){
        b.setUserId(String.valueOf(userId));
        return repo.save(b);
    }

    private BudgetSettings createDefault(String userId){
        BudgetSettings b = BudgetSettings.builder()
                .userId(userId)
                .monthlyBudget(0.0)
                .categoryBudgets(Map.of())
                .build();

        return repo.save(b);
    }
}
