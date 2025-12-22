package com.example.Expense.Tracker.repo;

import com.example.Expense.Tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long> {

    Optional<User> findbyUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
