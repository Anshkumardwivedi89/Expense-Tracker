package com.example.Expense.Tracker.config;

import com.example.Expense.Tracker.service.AnalyticsCacheService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class AnalyticsScheduler {

    private final AnalyticsCacheService analyticsCacheService;

    public AnalyticsScheduler(AnalyticsCacheService analyticsCacheService) {
        this.analyticsCacheService = analyticsCacheService;
    }

    // refresh every 30 minutes
    @Scheduled(cron = "0 */30 * * * *")
    public void fetchAnalytics(){
        analyticsCacheService.refreshAll();
    }

}
