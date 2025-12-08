package com.example.Expense.Tracker.repo;

import com.example.Expense.Tracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Mainrepo extends JpaRepository<Expense,Long> {
}
