package com.development.expense.rest.dto;

import lombok.Data;

@Data
public class SendOTPRequest {
    private Long chatId;
    private String otp;
}
