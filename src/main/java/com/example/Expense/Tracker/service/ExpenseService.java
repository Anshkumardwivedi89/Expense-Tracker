package com.example.Expense.Tracker.service;


import com.example.Expense.Tracker.entity.Expense;
import com.example.Expense.Tracker.interfaces.IExpense;
import com.example.Expense.Tracker.repo.Mainrepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ExpenseService implements IExpense {

    @Autowired
    Mainrepo mainrepo;

    public List<Expense> getExpenses() {
        return mainrepo.findAll();

    }

    @Override
    public List<Expense> saveExpense(List<Expense> expense) {
        return  mainrepo.saveAll(expense);
    }


}
