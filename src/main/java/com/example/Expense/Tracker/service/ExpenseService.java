package com.example.Expense.Tracker.service;

import com.example.Expense.Tracker.entity.Expense;
import com.example.Expense.Tracker.interfaces.IExpense;
import com.example.Expense.Tracker.repo.Mainrepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ExpenseService implements IExpense {

    private final Mainrepo mainrepo;
    private final ExpenseLogService expenseLogService;
    private final BudgetService budgetService;

    public ExpenseService(Mainrepo mainrepo,
                          ExpenseLogService expenseLogService,
                          BudgetService budgetService) {
        this.mainrepo = mainrepo;
        this.expenseLogService = expenseLogService;
        this.budgetService = budgetService;
    }

    // 🔹 GET all expenses (MySQL only)
    public List<Expense> getExpenses() {
        return mainrepo.findAll();
    }

    // 🔹 CREATE expenses
    @Override
    @Transactional   // ✅ ONLY MySQL transaction
    public List<Expense> saveExpense(List<Expense> expenses) {

        // 1️⃣ Prepare data BEFORE save
        for (Expense e : expenses) {

            // set date
            if (e.getDate() == null) {
                e.setDate(LocalDate.from(LocalDateTime.now()));
            }

            // set category
            if (e.getCategory() == null || e.getCategory().isBlank()) {
                e.setCategory("GENERAL");
            }
        }

        // 2️⃣ Save expenses in MySQL
        List<Expense> savedExpenses = mainrepo.saveAll(expenses);

        // 3️⃣ Mongo operations OUTSIDE transaction
        for (Expense e : savedExpenses) {

            // 🔹 Deduct from budget (Mongo)
            if (e.getBudgetId() != null) {
                budgetService.deductFromBudget(
                        e.getBudgetId(),
                        e.getAmount()
                );
            }

            // 🔹 Log expense creation (Mongo)
            expenseLogService.log(
                    e.getUser().getId(),
                    "CREATE",
                    e.getId(),
                    Map.of(
                            "title", e.getTitle(),
                            "amount", e.getAmount()
                    )
            );
        }

        return savedExpenses;
    }
}
