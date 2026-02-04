package com.example.Expense.Tracker.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @ManyToOne
    private Category category;

    private Double amount;

    private LocalDate date;

    private String description;

    private String budgetId; // Mongo budget _id

    @ManyToOne
    private User user;
}
