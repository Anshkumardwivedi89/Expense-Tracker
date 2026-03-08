package com.example.Expense.Tracker.config;

import com.example.Expense.Tracker.entity.Budget;
import com.example.Expense.Tracker.repo.BudgetRepo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class BudgetResetScheduler {

    private final BudgetRepo budgetRepo;

    public BudgetResetScheduler(BudgetRepo budgetRepo) {
        this.budgetRepo = budgetRepo;
    }

    // run hourly to avoid tight scheduling; reset budgets that have passed resetsOn
    @Scheduled(cron = "0 0 * * * *")
    public void resetBudgets() {
        List<Budget> budgets = budgetRepo.findAll();
        LocalDateTime now = LocalDateTime.now();
        for (Budget b : budgets) {
            if (b.getResetsOn() != null && b.getResetsOn().isBefore(now)) {
                b.setRemainingAmount(b.getAllocatedAmount());
                // schedule next reset
                if ("MONTHLY".equalsIgnoreCase(b.getPeriod())) {
                    b.setResetsOn(b.getResetsOn().plusMonths(1));
                } else {
                    b.setResetsOn(b.getResetsOn().plusWeeks(1));
                }
                budgetRepo.save(b);
            }
        }
    }
}
