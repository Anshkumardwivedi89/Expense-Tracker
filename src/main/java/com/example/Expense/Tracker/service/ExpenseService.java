package com.example.Expense.Tracker.service;


import com.example.Expense.Tracker.entity.Expense;
import com.example.Expense.Tracker.interfaces.IExpense;
import com.example.Expense.Tracker.repo.Mainrepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseService implements IExpense {

    @Autowired
    Mainrepo mainrepo;

    @Autowired
    ExpenseLogService expenseLogService;

    @Autowired
    BudgetService budgetService;

    public ExpenseService(Mainrepo mainrepo, ExpenseLogService expenseLogService, BudgetService budgetService){
        this.mainrepo = mainrepo;
        this.expenseLogService = expenseLogService;
        this.budgetService=budgetService;

    }

    public List<Expense> getExpenses() {
        return mainrepo.findAll();

    }

    @Override
    public List<Expense> saveExpense(List<Expense> expenses) {

        // 1️⃣ Save all expenses in MySQL
        List<Expense> savedExpenses = mainrepo.saveAll(expenses);

        // 2️⃣ For each expense → log + deduct budget
        for (Expense e : savedExpenses) {

            Long userId = e.getUser().getId(); // assuming Expense has User mapped

            // 🔹 LOG EXPENSE CREATION
            expenseLogService.log(
                    userId,
                    "CREATE",
                    e.getId(),
                    Map.of(
                            "title", e.getTitle(),
                            "amount", e.getAmount()
                    )
            );

            // 🔹 DEDUCT FROM BUDGET (if linked)
            if (e.getBudgetId() != null) {
                budgetService.deductFromBudget(
                        e.getBudgetId(),
                        e.getAmount()
                );
            }
        }

        return savedExpenses;
    }


}
