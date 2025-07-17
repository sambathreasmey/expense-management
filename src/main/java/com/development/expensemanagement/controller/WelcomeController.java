package com.development.expensemanagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class WelcomeController {
    @GetMapping("/get-start")
    public String getStart() {
        return "Welcome to Expense Management System";
    }
}
