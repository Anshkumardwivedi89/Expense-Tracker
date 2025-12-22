package com.example.Expense.Tracker.mongoControllers;

import com.example.Expense.Tracker.mongoModels.Notification;
import com.example.Expense.Tracker.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    @Autowired
     NotificationService service;

    @PostMapping("/create")
    public Notification create(
            @RequestParam Long userId,
            @RequestParam String message,
            @RequestParam String type
    ) {
        return service.create(String.valueOf(userId), message, type);
    }

    @GetMapping
    public List<Notification> get(@RequestParam Long userId) {
        return service.getUserNotification(userId);
    }
}

