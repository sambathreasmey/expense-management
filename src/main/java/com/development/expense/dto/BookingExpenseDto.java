package com.development.expense.dto;

import com.development.expense.enums.CurrencyEnum;

public record BookingExpenseDto(
        String title,
        Long userId,
        Long categoryId,
        Double amount,
        CurrencyEnum currency
) {
}
