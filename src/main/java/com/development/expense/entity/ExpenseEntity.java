package com.development.expense.entity;

import com.development.expense.enums.CurrencyEnum;
import com.development.expense.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "expenses")
public class ExpenseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Double amount;
    private CurrencyEnum currency;
    private Long userId;
    private Long categoryId;
    private StatusEnum status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
