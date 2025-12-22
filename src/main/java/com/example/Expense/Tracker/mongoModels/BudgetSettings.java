package com.example.Expense.Tracker.mongoModels;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "budget_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetSettings {

    @Id
    private String userId;

    private Double monthlyBudget;

    private Map<String, Double> categoryBudgets;


}
