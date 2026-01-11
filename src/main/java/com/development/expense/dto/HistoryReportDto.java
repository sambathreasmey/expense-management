package com.development.expense.dto;

import com.development.expense.enums.CurrencyEnum;

import java.sql.Timestamp;

public record HistoryReportDto(
        Long id,
        String title,
        CurrencyEnum currency,
        String categoryName,
        Timestamp createdAt
) {
}
