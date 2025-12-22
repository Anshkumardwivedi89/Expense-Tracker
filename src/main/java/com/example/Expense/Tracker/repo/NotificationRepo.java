package com.example.Expense.Tracker.repo;

import com.example.Expense.Tracker.mongoModels.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepo extends MongoRepository<Notification, String> {

    List<Notification> findByUserId(String userId);
    List<Notification> findBySentFalse();
}
