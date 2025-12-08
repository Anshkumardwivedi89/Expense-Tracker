package com.example.Expense.Tracker.mongoModels;


import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "user_profiles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfile {

    @Id
    private String userId;

    private String avatarUrl;

    private Map<String, Object> preferences;

    private Map<String, Boolean> notificationSettings;

}
