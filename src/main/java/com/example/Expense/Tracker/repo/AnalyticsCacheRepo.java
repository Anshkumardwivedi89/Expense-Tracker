package com.example.Expense.Tracker.repo;

import com.example.Expense.Tracker.mongoModels.AnalyticsCache;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnalyticsCacheRepo extends MongoRepository<AnalyticsCache, String> {
}
