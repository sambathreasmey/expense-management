package com.development.expense.util;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GlobalUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a");
    private static final DecimalFormat DECIMAL_FORMAT_USD = new DecimalFormat("#,##0.00");
    private static final DecimalFormat DECIMAL_FORMAT_KHR = new DecimalFormat("#,##0");

    public static String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        LocalDateTime ldt = timestamp.toLocalDateTime();
        return ldt.format(FORMATTER);
    }

    public static String formatAmount(Double amount, String currency) {
        if (amount == null || currency == null) return null;
        DecimalFormat dt;
        switch (currency) {
            case "USD" -> {
                currency = "ដុល្លារ";
                dt = DECIMAL_FORMAT_USD;
            }
            case "KHR" -> {
                currency = "រៀល";
                dt = DECIMAL_FORMAT_KHR;
            }
            default -> {
                return null;
            }
        }
        return dt.format(amount) + " " + currency;
    }

    public static String generateOTP(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10)); // 0–9
        }
        return otp.toString();
    }
}