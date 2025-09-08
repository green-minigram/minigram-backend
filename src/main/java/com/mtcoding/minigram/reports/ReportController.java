package com.mtcoding.minigram.reports;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController

public class ReportController {

    private final ReportService reportService;

    // 1. 신고 접수
    @PostMapping("/api/reports")
    public ResponseEntity<?> createReport(HttpServletRequest request, @Valid @RequestBody ReportRequest.SaveDTO reqDto, Errors errors) {

        if (errors.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(new ApiUtil<>(errors.getAllErrors()));
        }

        SessionUser sessionUser = (SessionUser) request.getAttribute("sessionUser");

        ReportResponse.SaveResultDTO respDTO = reportService.createReport(reqDto, sessionUser);

        return ResponseEntity.ok(new ApiUtil<>(respDTO));
    }
}

