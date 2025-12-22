package com.example.Expense.Tracker.repo;

import com.example.Expense.Tracker.mongoModels.BudgetSettings;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BudgetSettingsReop extends MongoRepository<BudgetSettings,String> {

}
