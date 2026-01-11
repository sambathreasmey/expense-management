package com.development.expense.service;

import com.development.expense.constant.CodeConstant;
import com.development.expense.constant.MessageConstant;
import com.development.expense.dto.ApiResponse;
import com.development.expense.dto.BookingExpenseDto;
import com.development.expense.dto.UpdateExpenseDto;
import com.development.expense.entity.CategoryEntity;
import com.development.expense.entity.ExpenseEntity;
import com.development.expense.enums.StatusEnum;
import com.development.expense.repository.CategoryRepository;
import com.development.expense.repository.ExpenseRepository;
import com.development.expense.repository.UserRepository;
import com.development.expense.rest.TelegramClient;
import com.development.expense.rest.dto.SendBookingNotificationRequest;
import com.development.expense.util.GlobalUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    final ExpenseRepository expenseRepository;
    final UserRepository userRepository;
    final CategoryRepository categoryRepository;
    final TelegramClient telegramClient;

    public String bookingExpense(BookingExpenseDto bookingExpenseDto) {
        ExpenseEntity expenseEntity = new ExpenseEntity();
        expenseEntity.setTitle(bookingExpenseDto.title());
        expenseEntity.setCurrency(bookingExpenseDto.currency());
        expenseEntity.setAmount(bookingExpenseDto.amount());

        if (bookingExpenseDto.userId() == null) {
            return "user is null or empty";
        }
        var findUser = userRepository.findById(bookingExpenseDto.userId());
        if (findUser.isEmpty()) {
            return "user not found";
        }
        expenseEntity.setUserId(bookingExpenseDto.userId());

        if (bookingExpenseDto.categoryId() == null) {
            return "category is null or empty";
        }
        var findCategory = categoryRepository.findById(bookingExpenseDto.categoryId());
        if (findCategory.isEmpty()) {
            return "category not invalid";
        }
        expenseEntity.setCategoryId(bookingExpenseDto.categoryId());

        expenseEntity.setStatus(StatusEnum.ACTIVE);
        expenseEntity.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        var bookingData = expenseRepository.saveAndFlush(expenseEntity);

        // call to expense-notification service
        if (findUser.get().getTelegramChatId() != null) {
            new Thread(() -> {
                SendBookingNotificationRequest notificationRequest = new SendBookingNotificationRequest();
                notificationRequest.setTitle(bookingData.getTitle());
                notificationRequest.setChatId(findUser.get().getTelegramChatId());
                notificationRequest.setBookingDate(GlobalUtil.formatTimestamp(bookingData.getCreatedAt()));
                notificationRequest.setFullName(findUser.get().getFullName());
                notificationRequest.setAmount(GlobalUtil.formatAmount(bookingData.getAmount(), bookingData.getCurrency().name()));
                notificationRequest.setCategoryName(findCategory.get().getName());
                telegramClient.sendBookingNotification(notificationRequest);
            }).start();
        }

        return "booking success";
    }

    public List<ExpenseEntity> getAll() {
        var data = expenseRepository.findAll();
        if(data.isEmpty()) {
            return new ArrayList<>();
        }
        return data;
    }

    public ApiResponse update(Long id, UpdateExpenseDto request) {
        ApiResponse apiResponse = new ApiResponse();
        if (id == null) {
            apiResponse.setCode(CodeConstant.REQUIRED);
            apiResponse.setMessage("id is null");
            return apiResponse;
        }
        if (request == null) {
            apiResponse.setCode(CodeConstant.REQUIRED);
            apiResponse.setMessage("request is null");
            return apiResponse;
        }
        if (request.categoryId() != null) {
            Optional<CategoryEntity> category = categoryRepository.findById(request.categoryId());
            if (category.isEmpty()) {
                apiResponse.setCode(CodeConstant.NOT_FOUND);
                apiResponse.setMessage("category not found");
                return apiResponse;
            }
        }
        Optional<ExpenseEntity> find = expenseRepository.findById(id);
        if (find.isEmpty()) {
            apiResponse.setCode(CodeConstant.NOT_FOUND);
            apiResponse.setMessage(MessageConstant.NOT_FOUND);
            return apiResponse;
        }
        // Convert from Optional<ExpenseEntity> -> ExpenseEntity
        ExpenseEntity expenseEntity = find.get();
        // Option 1 basic
        if (request.title() == null) {
            expenseEntity.setTitle(expenseEntity.getTitle());
        } else {
            if (request.title().isEmpty()) {
                expenseEntity.setTitle(expenseEntity.getTitle());
            } else {
                expenseEntity.setTitle(request.title());
            }
        }
        // Option 2 smart logic
        if (request.title() != null && !request.title().isEmpty()) {
            expenseEntity.setTitle(request.title());
        }
        // Option 3 advance
        expenseEntity.setTitle(request.title() == null ? expenseEntity.getTitle() : request.title().isEmpty() ? expenseEntity.getTitle() : request.title());

        expenseEntity.setCurrency(request.currency() == null ? expenseEntity.getCurrency() : request.currency());
        expenseEntity.setAmount(request.amount() == null ? expenseEntity.getAmount() : request.amount());
        if (request.amount() != null && request.amount() <= 0) {
            apiResponse.setCode(CodeConstant.INVALID);
            apiResponse.setMessage("Amount is negative");
            return apiResponse;
        }
        expenseEntity.setCategoryId(request.categoryId() == null ? expenseEntity.getCategoryId() : request.categoryId());
        expenseEntity.setStatus(request.status() == null ? expenseEntity.getStatus() : request.status());
        expenseEntity.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        expenseRepository.save(expenseEntity);
        apiResponse.setCode(CodeConstant.SUCCESS);
        apiResponse.setMessage(MessageConstant.SUCCESS);
        return apiResponse;
    }
}
