package com.development.expense.rest.dto;

import lombok.Data;

@Data
public class SendBookingNotificationRequest {
    private Long chatId;
    private String title;
    private String fullName;
    private String categoryName;
    private String amount;
    private String bookingDate;
}