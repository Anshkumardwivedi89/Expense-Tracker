package com.example.Expense.Tracker.service;

import com.example.Expense.Tracker.entity.Transaction;
import com.example.Expense.Tracker.entity.TransactionType;
import com.example.Expense.Tracker.repo.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository repo;
    private final BudgetService budgetService;
    private final ExpenseLogService logService;
    private final NotificationService notificationService;
    private final DashboardService dashboardService;

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

        // Send transaction notification
        sendTransactionNotification(saved);

        return saved;
    }

    public List<Transaction> recent(Long userId) {
        return repo.findTop5ByUserIdOrderByDateDesc(userId);
    }

    public Transaction getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    /**
     * Send transaction notification via email and SMS
     */
    private void sendTransactionNotification(Transaction transaction) {
        try {
            DashboardService.DashboardSummary summary = dashboardService.getSummaryForUser(transaction.getUser().getId());
            Double currentBalance = summary.getCurrentBalance();

            notificationService.notifyTransaction(
                    transaction.getUser(),
                    transaction.getType().toString(),
                    transaction.getAmount(),
                    transaction.getCategory() != null ? transaction.getCategory().getName() : "Uncategorized",
                    transaction.getDescription(),
                    transaction.getDate() != null ? transaction.getDate().toString() : "",
                    currentBalance
            );
        } catch (Exception e) {
            log.error("Error sending transaction notification: {}", e.getMessage(), e);
        }
    }
}
