package com.example.Expense.Tracker.service;


import com.example.Expense.Tracker.mongoModels.ExpenseLog;
import com.example.Expense.Tracker.repo.ExpenseLogRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExpenseLogService {

    @Autowired
    private ExpenseLogRepo expenseLogRepo;

    public void log(Long userId, String action, Long expenseId, Map<String,Object> meta){
        ExpenseLog log  = ExpenseLog.builder()
                .userId(String.valueOf(userId))
                .action(action)
                .expenseId(expenseId)
                .timestamp(LocalDateTime.now())
                .metadata(meta)
                .build();

        expenseLogRepo.save(log);
    }


}
