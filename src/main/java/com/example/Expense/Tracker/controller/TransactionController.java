package com.example.Expense.Tracker.controller;

import com.example.Expense.Tracker.entity.Transaction;
import com.example.Expense.Tracker.service.TransactionService;
import com.example.Expense.Tracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;
    private final UserService userService;

    @PostMapping
    public Transaction create(@RequestBody Transaction t,
                              Authentication auth) {
        t.setUser(userService.getCurrentUser(auth));
        return service.save(t);
    }

    @GetMapping("/recent")
    public List<Transaction> recent(Authentication auth) {
        return service.recent(
                userService.getCurrentUser(auth).getId()
        );
    }
}
