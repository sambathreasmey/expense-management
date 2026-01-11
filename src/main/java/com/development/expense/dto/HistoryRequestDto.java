package com.development.expense.dto;

import com.development.expense.enums.StatusEnum;

public record HistoryRequestDto(Long userId, StatusEnum status) {
}
