package com.example.Expense.Tracker.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "budgets")
@AllArgsConstructor


@Builder
public class Budget {

    @Id
    private String id;

    private String userId;

    private String name;

    private String category;

    private Double allocatedAmount;

    private Double remainingAmount;

    private String period;

    private LocalDateTime createdAt;

    private LocalDateTime resetsOn;

}
