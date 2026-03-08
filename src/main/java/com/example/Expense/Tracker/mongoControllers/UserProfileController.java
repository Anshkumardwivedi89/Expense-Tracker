package com.example.Expense.Tracker.mongoControllers;

import com.example.Expense.Tracker.mongoModels.UserProfile;
import com.example.Expense.Tracker.service.UserProfileService;
import com.example.Expense.Tracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    private final UserService userService;

    @GetMapping
    public UserProfile getProfile(Authentication auth){
        Long userId = userService.getCurrentUser(auth).getId();
        return userProfileService.getProfile(userId);
    }

    @PutMapping
    public UserProfile updateProfile(Authentication auth, @RequestBody UserProfile userPf){
        Long userId = userService.getCurrentUser(auth).getId();
        return userProfileService.updateProfile(userId,userPf);
    }

}
