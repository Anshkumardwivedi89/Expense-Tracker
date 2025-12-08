package com.example.Expense.Tracker.mongoControllers;

import com.example.Expense.Tracker.mongoModels.UserProfile;
import com.example.Expense.Tracker.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {


    @Autowired
    UserProfileService userProfileService;

    @GetMapping
    public UserProfile getProfile(@RequestParam Long userId){
        return userProfileService.getProfile(userId);
    }

    @PutMapping
    public UserProfile updateProfile(@RequestParam Long userId, @RequestBody UserProfile userPf){
        return userProfileService.updateProfile(userId,userPf);
    }


}
