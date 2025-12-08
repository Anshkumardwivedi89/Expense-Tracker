package com.example.Expense.Tracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "curr_expense")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // ← AUTO-INCREMENT from DB
    private Long id;

    private String title;
    private Double amount;
    private LocalDate date;
    private String category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
