package com.example.Expense.Tracker.repo;

import com.example.Expense.Tracker.mongoModels.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserProfileRepo extends MongoRepository<UserProfile, String> {
}
