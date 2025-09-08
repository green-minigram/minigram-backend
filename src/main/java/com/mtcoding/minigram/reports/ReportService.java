package com.mtcoding.minigram.reports;

import com.mtcoding.minigram._core.error.ex.ExceptionApi404;
import com.mtcoding.minigram.reports.reasons.ReportReason;
import com.mtcoding.minigram.reports.reasons.ReportReasonRepository;
import com.mtcoding.minigram.users.User;
import com.mtcoding.minigram.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ReportReasonRepository reasonRepository;


    @Transactional
    public ReportResponse.SaveResultDTO createReport(ReportRequest.SaveDTO reqDTO, SessionUser sessionUser) {

        User reporter = userRepository.findById(sessionUser.getId())
                .orElseThrow();

        ReportReason reason = reasonRepository.findById(reqDTO.getReasonId())
                .orElseThrow(() -> new ExceptionApi404("신고 사유 없음"));

        Report report = reqDTO.toEntity(reporter, reason);

        reportRepository.save(report);

        return ReportResponse.SaveResultDTO.from(report);
    }


}



