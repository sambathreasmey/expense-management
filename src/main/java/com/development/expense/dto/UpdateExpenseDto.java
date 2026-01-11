package com.development.expense.dto;

import com.development.expense.enums.CurrencyEnum;
import com.development.expense.enums.StatusEnum;

public record UpdateExpenseDto(
        String title,
        Long categoryId,
        Double amount,
        CurrencyEnum currency,
        StatusEnum status
) {
}
