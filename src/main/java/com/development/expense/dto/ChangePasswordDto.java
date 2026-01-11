package com.development.expense.dto;

public record ChangePasswordDto(
        String username,
        String oldPassword,
        String newPassword,
        String confirmPassword
) {
}