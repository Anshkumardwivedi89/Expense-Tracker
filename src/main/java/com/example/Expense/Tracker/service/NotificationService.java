package com.example.Expense.Tracker.service;

import com.example.Expense.Tracker.mongoModels.Notification;
import com.example.Expense.Tracker.repo.NotificationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    NotificationRepo repo;

    public Notification create(String userId, String message, String type){
        Notification n = Notification.builder()
                .userId(userId)
                .message(message)
                .type(type)
                .createdAt(LocalDateTime.now())
                .build();

        return repo.save(n);
    }

  public List<Notification> getUserNotification(Long userId){
        return repo.findByUserId(String.valueOf(userId));
  }

  public void markSent(String id){
        repo.findById(id).ifPresent(n -> {
            n.setSent(true);
            repo.save(n);
        });
  }
}
