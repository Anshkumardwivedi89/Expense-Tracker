package com.example.Expense.Tracker.service;


import com.example.Expense.Tracker.entity.Category;
import com.example.Expense.Tracker.entity.CategoryType;
import com.example.Expense.Tracker.repo.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepo repo;

    public Category create(Category category){
        return repo.save(category);
    }

    public List<Category> getByType(Long userId, CategoryType type){
        return repo.findByUserIdAndType(userId, type);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
