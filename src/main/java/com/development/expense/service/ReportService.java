package com.development.expense.service;

import com.development.expense.constant.CodeConstant;
import com.development.expense.constant.MessageConstant;
import com.development.expense.dto.ApiResponse;
import com.development.expense.dto.HistoryReportDto;
import com.development.expense.dto.HistoryRequestDto;
import com.development.expense.repository.CategoryRepository;
import com.development.expense.repository.ExpenseRepository;
import com.development.expense.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public ApiResponse history(HistoryRequestDto request) {
        if (request == null) {
            return new ApiResponse(CodeConstant.REQUIRED, MessageConstant.BAD_REQUEST);
        }
        if (request.userId() == null) {
            return new ApiResponse(CodeConstant.REQUIRED, MessageConstant.BAD_REQUEST);
        }
        var user = userRepository.findById(request.userId());
        if (user.isEmpty()) {
            return new ApiResponse(CodeConstant.NOT_FOUND, "User not found");
        }
        List<HistoryReportDto> historyReports = new ArrayList<>();
        var histories = expenseRepository.findAllByUserIdAndStatus(user.get().getId(), request.status());
        histories.forEach(history -> {
            var category = categoryRepository.findById(history.getCategoryId());
            String categoryName;
            if (category.isPresent()) {
                categoryName = category.get().getName();
            } else {
                categoryName = "លុបបាត់ហើយ";
            }
            var historyReport = new HistoryReportDto(
                    history.getId(),
                    history.getTitle(),
                    history.getCurrency(),
                    categoryName,
                    history.getCreatedAt()
                    );
            historyReports.add(historyReport);
        });

        return new ApiResponse(CodeConstant.SUCCESS, MessageConstant.SUCCESS, historyReports);
    }
}
