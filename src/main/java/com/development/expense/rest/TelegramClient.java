package com.development.expense.rest;

import com.development.expense.constant.CodeConstant;
import com.development.expense.constant.MessageConstant;
import com.development.expense.dto.ApiResponse;
import com.development.expense.enums.StatusEnum;
import com.development.expense.rest.dto.SendBookingNotificationRequest;
import com.development.expense.rest.dto.SendOTPRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TelegramClient {

    @Autowired
    private RestTemplate restTemplate;

    public void sendBookingNotification(SendBookingNotificationRequest request) {
        String url = "http://localhost:8181/api/notification";
        restTemplate.postForEntity(url, request, String.class);
    }

    public ApiResponse sendOTP(SendOTPRequest request) {
        ApiResponse apiResponse = new ApiResponse();
        String url = "http://localhost:8181/api/verification/send-otp";
        try {
            var response = restTemplate.postForEntity(url, request, ApiResponse.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                apiResponse.setCode(CodeConstant.INTERNAL_SERVER_ERROR);
                apiResponse.setMessage(MessageConstant.INTERNAL_SERVER_ERROR);
                return apiResponse;
            }

            if (response.getBody() == null) {
                apiResponse.setCode(CodeConstant.INTERNAL_SERVER_ERROR);
                apiResponse.setMessage(MessageConstant.INTERNAL_SERVER_ERROR);
                return apiResponse;
            }

            if (response.getBody().getCode() != CodeConstant.SUCCESS) {
                apiResponse.setCode(response.getBody().getCode());
                apiResponse.setMessage(response.getBody().getMessage());
            }
        } catch (Exception e) {
            apiResponse.setCode(CodeConstant.INTERNAL_SERVER_ERROR);
            apiResponse.setMessage(MessageConstant.INTERNAL_SERVER_ERROR);
            return apiResponse;
        }
        apiResponse.setCode(CodeConstant.SUCCESS);
        apiResponse.setMessage(MessageConstant.SUCCESS);
        return apiResponse;

    }
}
