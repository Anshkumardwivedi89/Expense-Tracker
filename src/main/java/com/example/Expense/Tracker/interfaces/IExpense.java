package com.example.Expense.Tracker.interfaces;

import com.example.Expense.Tracker.entity.Expense;

import java.util.List;

public interface IExpense {


    List<Expense> getExpenses();

    List<Expense> saveExpense(List<Expense> expense);
}
