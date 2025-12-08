package com.example.Expense.Tracker.repo;

import com.example.Expense.Tracker.entity.Budget;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BudgetRepo extends MongoRepository<Budget, String> {
    List<Budget> findByUserId(String userId);

}
