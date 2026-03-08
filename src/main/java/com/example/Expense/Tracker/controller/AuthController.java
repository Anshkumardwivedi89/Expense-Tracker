package com.example.Expense.Tracker.controller;


import com.example.Expense.Tracker.dto.*;
import com.example.Expense.Tracker.entity.*;

import com.example.Expense.Tracker.repo.RoleRepo;
import com.example.Expense.Tracker.repo.UserRepo;
import com.example.Expense.Tracker.service.UserProfileService;
import com.example.Expense.Tracker.utility.JwtUtil;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder encoder;
    private final UserProfileService userProfileService;

    public AuthController(AuthenticationManager authManager,
                          JwtUtil jwtUtil,
                          UserRepo userRepo,
                          RoleRepo roleRepo,
                          PasswordEncoder encoder,
                          UserProfileService userProfileService) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
        this.userProfileService = userProfileService;
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest req) {

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));

        Role role = roleRepo.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName("ROLE_USER");
                    return roleRepo.save(r);
                });

        user.setRoles(Set.of(role));

        User saved = userRepo.save(user);

        // create default profile in MongoDB
        userProfileService.getProfile(saved.getId());

        return "User registered";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest req) {

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getUsername(), req.getPassword()
                )
        );

        return jwtUtil.generateToken(req.getUsername());
    }
}
