package com.development.expense.controller;

import com.development.expense.dto.ApiResponse;
import com.development.expense.dto.BookingExpenseDto;
import com.development.expense.dto.UpdateExpenseDto;
import com.development.expense.dto.UserDto;
import com.development.expense.entity.ExpenseEntity;
import com.development.expense.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<String> bookingExpense(@RequestBody BookingExpenseDto bookingExpenseDto) {
        return new ResponseEntity<>(expenseService.bookingExpense(bookingExpenseDto), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseEntity>> getAllExpenses() {
        return new ResponseEntity<>(expenseService.getAll(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @RequestBody UpdateExpenseDto request) {
        return new ResponseEntity<>(expenseService.update(id, request), HttpStatus.OK);
    }
}
