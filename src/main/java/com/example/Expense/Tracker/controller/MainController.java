package com.example.Expense.Tracker.controller;


import com.example.Expense.Tracker.entity.Expense;
import com.example.Expense.Tracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/main")
public class MainController {

    @Autowired
    ExpenseService expenseService ;


    @GetMapping("/expense/test")
    public String Status(){

        return "Page Loaded Successfully";
    }

    @GetMapping("/expense/fetch")
    public List<Expense> getExpenses(){
        return expenseService.getExpenses();

    }

    @PostMapping("expense/push")
    public List<Expense> pushExpense(@RequestBody List<Expense>  expense){
        return expenseService.saveExpense(expense);
    }


}
