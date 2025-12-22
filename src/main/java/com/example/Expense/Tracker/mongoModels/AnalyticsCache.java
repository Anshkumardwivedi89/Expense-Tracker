package com.example.Expense.Tracker.mongoModels;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "analytics_cache")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsCache {

    @Id
    private String userId;

    private Map<String, Map<String,Double>>monthlySummary;
    private LocalDateTime lastUpdated;
}
