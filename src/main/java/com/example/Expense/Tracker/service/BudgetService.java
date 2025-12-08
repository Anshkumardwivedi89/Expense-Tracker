package com.example.Expense.Tracker.service;


import com.example.Expense.Tracker.entity.Budget;
import com.example.Expense.Tracker.repo.BudgetRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BudgetService {

    @Autowired
    BudgetRepo budgetRepo;

    public Budget createBudget(Long userId, Budget b){
        b.setUserId(String.valueOf(userId));
        b.setCreatedAt(LocalDateTime.now());
        b.setAllocatedAmount(b.getAllocatedAmount());

        if("MONTHLY".equals(b.getPeriod())){
            b.setResetOn(LocalDateTime.now().plusMonths(1));
        }else{
            b.setResetOn(LocalDateTime.now().plusWeeks(1));
        }
        return budgetRepo.save(b);
    }

    public List<Budget> getBudgets(Long userId){
        return budgetRepo.findByUserId(String.valueOf(userId));

    }

    public void deductFromBudget(String budgetId , Double amount){
        budgetRepo.findById(budgetId).ifPresent(
                b-> {
                    b.setRemainingAmount(b.getRemainingAmount() - amount);
                    budgetRepo.save(b);
                }
        );
    }




}
