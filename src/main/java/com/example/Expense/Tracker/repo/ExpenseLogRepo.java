package com.example.Expense.Tracker.repo;

import com.example.Expense.Tracker.mongoModels.ExpenseLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ExpenseLogRepo extends MongoRepository<ExpenseLog, String> {
    List<ExpenseLog> findByUserId(String userId);
}
