package com.mtcoding.minigram.reports;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController

public class ReportController {

    private final ReportService reportService;

    // 1. 신고 접수
    @PostMapping("/api/reports")
    public ResponseEntity<?> createReport(HttpServletRequest request, @Valid @RequestBody ReportRequest.SaveDTO reqDto, Errors errors) {
        if (sessionUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }
        Report report = reportService.createReport(reqDto, sessionUser);

        return ResponseEntity.ok(ReportResponse.SaveResultDTO.from(report));
    }

