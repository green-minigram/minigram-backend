package com.mtcoding.minigram.reports;

import com.mtcoding.minigram._core.util.Resp;
import com.mtcoding.minigram.users.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

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

    //2. 신고이유 목록 보내기
    @GetMapping("/s/api/reports/reasons")
    public ResponseEntity<?> getReasons() {
        ReportResponse.ReasonListDTO reasonListDTO = reportService.getReasons();

        return Resp.ok(reasonListDTO);

    }

    //3. 관리자용 신고 상세
    @GetMapping("/s/api/admin/reports/{reportId}")
    public ResponseEntity<?> find(@PathVariable Integer reportId) {
        ReportResponse.AdminDetailDTO respDTO = reportService.find(reportId);
        return Resp.ok(respDTO);
    }
}

