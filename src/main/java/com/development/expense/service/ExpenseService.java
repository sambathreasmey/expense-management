package com.development.expense.service;

import com.development.expense.dto.BookingExpenseDto;
import com.development.expense.entity.ExpenseEntity;
import com.development.expense.enums.StatusEnum;
import com.development.expense.repository.CategoryRepository;
import com.development.expense.repository.ExpenseRepository;
import com.development.expense.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ExpenseService {

    final ExpenseRepository expenseRepository;
    final UserRepository userRepository;
    final CategoryRepository categoryRepository;
    public ExpenseService(ExpenseRepository expenseRepository,
                          UserRepository userRepository,
                          CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public String bookingExpense(BookingExpenseDto bookingExpenseDto) {
        ExpenseEntity expenseEntity = new ExpenseEntity();
        expenseEntity.setTitle(bookingExpenseDto.title());
        expenseEntity.setCurrency(bookingExpenseDto.currency());
        expenseEntity.setAmount(bookingExpenseDto.amount());

        if (bookingExpenseDto.userId() == null) {
            return "user is null or empty";
        }
        var findUser = userRepository.findById(bookingExpenseDto.userId());
        if (findUser.isEmpty()) {
            return "user not found";
        }
        expenseEntity.setUserId(bookingExpenseDto.userId());

        if (bookingExpenseDto.categoryId() == null) {
            return "category is null or empty";
        }
        var findCategory = categoryRepository.findById(bookingExpenseDto.categoryId());
        if (findCategory.isEmpty()) {
            return "category not invalid";
        }
        expenseEntity.setCategoryId(bookingExpenseDto.categoryId());

        expenseEntity.setStatus(StatusEnum.ACTIVE);
        expenseEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        expenseRepository.save(expenseEntity);
        return "booking success";
    }

    public List<ExpenseEntity> getAll() {
        return expenseRepository.findAll();
    }
}
