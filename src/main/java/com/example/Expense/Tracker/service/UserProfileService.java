package com.example.Expense.Tracker.service;

import com.example.Expense.Tracker.entity.User;
import com.example.Expense.Tracker.mongoModels.UserProfile;
import com.example.Expense.Tracker.repo.UserProfileRepo;
import com.example.Expense.Tracker.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class UserProfileService {

    @Autowired
    UserProfileRepo userProfileRepo;

    @Autowired
    UserRepo userRepository;

    public UserProfile getProfile(Long userId){
        String mongoId = String.valueOf(userId);
        UserProfile profile = userProfileRepo.findById(mongoId)
                .orElseGet(()-> createDefaultProfile(mongoId, userId));

        // Fetch email and phoneNumber from User entity
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            profile.setEmail(user.get().getEmail());
            profile.setPhoneNumber(user.get().getPhoneNumber());
        }

        return profile;
    }
    public UserProfile updateProfile(Long userId , UserProfile userProfile){
        userProfile.setUserId(String.valueOf(userId));
        UserProfile saved = userProfileRepo.save(userProfile);
        
        // Update email and phoneNumber in User entity if provided
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User userEntity = user.get();
            if (userProfile.getEmail() != null && !userProfile.getEmail().isEmpty()) {
                userEntity.setEmail(userProfile.getEmail());
            }
            if (userProfile.getPhoneNumber() != null && !userProfile.getPhoneNumber().isEmpty()) {
                userEntity.setPhoneNumber(userProfile.getPhoneNumber());
            }
            userRepository.save(userEntity);
        }
        
        return saved;
    }

    private UserProfile createDefaultProfile(String userId, Long userIdLong) {
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
        
        // Fetch email and phoneNumber from User entity
        Optional<User> user = userRepository.findById(userIdLong);
        if (user.isPresent()) {
            profile.setEmail(user.get().getEmail());
            profile.setPhoneNumber(user.get().getPhoneNumber());
        }
        
        return userProfileRepo.save(profile);
    }


}
