package com.example.Expense.Tracker.repo;

import com.example.Expense.Tracker.entity.Transaction;
import com.example.Expense.Tracker.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findTop5ByUserIdOrderByDateDesc(Long userId);

    List<Transaction> findByUserIdOrderByDateDesc(Long userId);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.date BETWEEN :startDate AND :endDate ORDER BY t.date DESC")
    List<Transaction> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("""
      SELECT SUM(t.amount)
      FROM Transaction t
      WHERE t.user.id = :userId AND t.type = :type
    """)
    Double sumByType(Long userId, TransactionType type);

    @Query("""
      SELECT SUM(t.amount)
      FROM Transaction t
      WHERE t.user.id = :userId AND t.type = :type
        AND FUNCTION('MONTH', t.date) = :month
        AND FUNCTION('YEAR', t.date) = :year
    """)
    Double sumByTypeForMonth(Long userId, TransactionType type, int month, int year);

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
