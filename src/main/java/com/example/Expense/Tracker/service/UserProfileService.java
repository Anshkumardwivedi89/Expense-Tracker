package com.example.Expense.Tracker.service;

import com.example.Expense.Tracker.mongoModels.UserProfile;
import com.example.Expense.Tracker.repo.UserProfileRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserProfileService {

    @Autowired
    UserProfileRepo userProfileRepo;

    public UserProfile getProfile(Long userId){

        String mongoId = String.valueOf(userId);

        return userProfileRepo.findById(mongoId)
                .orElseGet(()-> createDefaultProfile(mongoId));

    }
    public UserProfile updateProfile(Long userId , UserProfile userProfile){
        userProfile.setUserId(String.valueOf(userId));
        return userProfileRepo.save(userProfile);
    }

    private UserProfile createDefaultProfile(String userId) {
        UserProfile profile = UserProfile.builder()
                .userId(userId)
                .avatarUrl(null)
                .preferences(Map.of(
                        "theme", "light",
                        "currency", "INR"
                ))
                .notificationSettings(Map.of(
                        "email", true,
                        "sms", false
                ))
                .build();
return  userProfileRepo.save(profile);
    }


}
