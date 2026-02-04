package com.example.Expense.Tracker.service;

import com.example.Expense.Tracker.entity.Transaction;
import com.example.Expense.Tracker.entity.TransactionType;
import com.example.Expense.Tracker.repo.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repo;
    private final BudgetService budgetService;
    private final ExpenseLogService logService;

    public Transaction save(Transaction t) {

        Transaction saved = repo.save(t);

        if (t.getType() == TransactionType.EXPENSE) {

            logService.log(
                    t.getUser().getId(),
                    "CREATE",
                    saved.getId(),
                    Map.of("amount", t.getAmount())
            );

            if (t.getBudgetId() != null) {
                budgetService.deductFromBudget(
                        t.getBudgetId(),
                        t.getAmount()
                );
            }
        }
        return saved;
    }

    public List<Transaction> recent(Long userId) {
        return repo.findTop5ByUserIdOrderByDateDesc(userId);
    }
}
