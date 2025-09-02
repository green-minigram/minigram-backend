package com.mtcoding.minigram.reports;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ReportsController {
    private final ReportService reportService;
    private final HttpSession session;
}
