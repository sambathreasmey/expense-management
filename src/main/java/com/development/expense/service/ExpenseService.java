package com.development.expense.service;

import com.development.expense.dto.BookingExpenseDto;
import com.development.expense.entity.ExpenseEntity;
import com.development.expense.enums.StatusEnum;
import com.development.expense.repository.CategoryRepository;
import com.development.expense.repository.ExpenseRepository;
import com.development.expense.repository.UserRepository;
import com.development.expense.rest.TelegramClient;
import com.development.expense.rest.dto.SendBookingNotificationRequest;
import com.development.expense.util.GlobalUtil;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ExpenseService {

    final ExpenseRepository expenseRepository;
    final UserRepository userRepository;
    final CategoryRepository categoryRepository;
    final TelegramClient telegramClient;
    public ExpenseService(ExpenseRepository expenseRepository,
                          UserRepository userRepository,
                          CategoryRepository categoryRepository, TelegramClient telegramClient) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.telegramClient = telegramClient;
    }

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
        return expenseRepository.findAll();
    }
}
