package com.example.Expense.Tracker.service;

import com.example.Expense.Tracker.entity.User;
import com.example.Expense.Tracker.repo.UserRepo;
import com.example.Expense.Tracker.utility.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {


    private final UserRepo userRepo;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    public AuthService(UserRepo userRepo,
                        PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil){
        this.userRepo=userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    //Register
    public String register(String username, String email, String password){
        if(userRepo.existsByUsername(username)){
            throw new RuntimeException("Username already exists");
        }
        if(userRepo.existsByEmail(email)){
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);

        userRepo.save(user);

        return jwtUtil.generateToken( user.getUsername());
    }

    //Login
    public String login(String username, String password){
        User user = userRepo.findByUsername(username)
                .orElseThrow(()->  new RuntimeException("Invalid Username"));

        if(!passwordEncoder.matches(password,user.getPassword())){
            throw new RuntimeException("Password does not match ");
        }

        return jwtUtil.generateToken(user.getUsername());
    }

}
