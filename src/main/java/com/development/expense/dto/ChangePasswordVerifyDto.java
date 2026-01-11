package com.development.expense.dto;

public record ChangePasswordVerifyDto(
        String username,
        String newPassword,
        String otp
) {
}
