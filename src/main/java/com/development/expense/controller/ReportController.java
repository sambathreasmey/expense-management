package com.development.expense.controller;

import com.development.expense.dto.ApiResponse;
import com.development.expense.dto.HistoryRequestDto;
import com.development.expense.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping("/history")
    public ResponseEntity<ApiResponse> history(@RequestBody HistoryRequestDto request) {
        return ResponseEntity.ok(reportService.history(request));
    }
}
