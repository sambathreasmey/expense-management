package com.development.expense.dto;

import com.development.expense.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

public record UserDto(
    @JsonProperty(value = "full_name")
    String fullName,
    String username,
    StatusEnum status,
    Long telegramChatId
) {
}