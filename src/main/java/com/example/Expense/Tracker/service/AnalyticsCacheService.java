package com.example.Expense.Tracker.service;

import com.example.Expense.Tracker.mongoModels.AnalyticsCache;
import com.example.Expense.Tracker.repo.AnalyticsCacheRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsCacheService {

    @Autowired
    private AnalyticsCacheRepo analyticsCacheRepo;

    public AnalyticsCache get(Long userId){
        return analyticsCacheRepo.findById(String.valueOf(userId)).orElse(null);
    }

    public AnalyticsCache update(Long userId, Map<String, Map<String,Double>>summary){
        AnalyticsCache cache = AnalyticsCache.builder()
                .userId(String.valueOf(userId))
                .monthlySummary(summary)
                .lastUpdated(LocalDateTime.now())
                .build();

        return analyticsCacheRepo.save(cache);
    }
}
