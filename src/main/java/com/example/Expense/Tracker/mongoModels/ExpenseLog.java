package com.example.Expense.Tracker.mongoModels;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "expense_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseLog {

    @Id
    private String id;

    private String userId;
    private String action;
    private Long expenseId;

    private LocalDateTime timestamp;
    private Map<String , Object> metadata;
}
