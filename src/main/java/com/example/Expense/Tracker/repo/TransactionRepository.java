package com.example.Expense.Tracker.repo;

import com.example.Expense.Tracker.entity.Transaction;
import com.example.Expense.Tracker.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findTop5ByUserIdOrderByDateDesc(Long userId);

    @Query("""
      SELECT SUM(t.amount)
      FROM Transaction t
      WHERE t.user.id = :userId AND t.type = :type
    """)
    Double sumByType(Long userId, TransactionType type);

    @Query("""
      SELECT t.category.name, SUM(t.amount)
      FROM Transaction t
      WHERE t.type = 'EXPENSE'
        AND FUNCTION('MONTH', t.date) = :month
        AND FUNCTION('YEAR', t.date) = :year
      GROUP BY t.category.name
    """)
    List<Object[]> expenseByCategory(int month, int year);
}
