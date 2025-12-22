package com.example.Expense.Tracker.config;

import com.example.Expense.Tracker.service.AnalyticsCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
@RequiredArgsConstructor
public class AnalyticsScheduler {

    @Autowired
    private AnalyticsCacheService analyticsCacheService;

    @Scheduled(cron="0 0 2 * * *")
    public void fetchAnalytics(){
        // Later: fetch expenses, compute summary, update cache
        // service.update(userId, summary);
    }


}
