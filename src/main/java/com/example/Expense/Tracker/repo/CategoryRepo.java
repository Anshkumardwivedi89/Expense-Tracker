package com.example.Expense.Tracker.repo;

import com.example.Expense.Tracker.entity.Category;
import com.example.Expense.Tracker.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepo extends JpaRepository<Category,Long> {

    List<Category> findByUserIdAndType(Long userId, CategoryType type);

    boolean existsByNameAndUserIdAndType(String name, Long userId, CategoryType type);

}
