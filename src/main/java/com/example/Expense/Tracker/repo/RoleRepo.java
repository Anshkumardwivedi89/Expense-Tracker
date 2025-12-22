package com.example.Expense.Tracker.repo;

import com.example.Expense.Tracker.entity.Role;
import com.example.Expense.Tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role,Long> {
    Optional<Role> findByName(String name);

}
