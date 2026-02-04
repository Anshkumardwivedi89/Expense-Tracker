package com.example.Expense.Tracker.controller;

import com.example.Expense.Tracker.entity.Category;
import com.example.Expense.Tracker.entity.CategoryType;
import com.example.Expense.Tracker.service.CategoryService;
import com.example.Expense.Tracker.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    @PostMapping
    public Category create(@RequestBody Category category,
                           Authentication auth) {
        category.setUser(userService.getCurrentUser(auth));
        return categoryService.create(category);
    }

    @GetMapping
    public List<Category> get(@RequestParam CategoryType type,
                              Authentication auth) {
        return categoryService.getByType(
                userService.getCurrentUser(auth).getId(),
                type
        );
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }
}
