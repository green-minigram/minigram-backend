package com.mtcoding.minigram.reports;

import com.mtcoding.minigram._core.util.Resp;
import com.mtcoding.minigram.users.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController

public class ReportsController {

    private final ReportService reportService;

    // 1. 신고 접수
    @PostMapping("/s/api/reports")
    public ResponseEntity<?> create(@AuthenticationPrincipal User user, @Valid @RequestBody ReportRequest.SaveDTO reqDTO, Errors errors) {

        ReportResponse.DTO respDTO = reportService.create(reqDTO, user);

        return Resp.ok(respDTO);
    }
}

