package com.mtcoding.minigram.reports;

import com.mtcoding.minigram._core.error.ex.ExceptionApi400;
import com.mtcoding.minigram._core.error.ex.ExceptionApi404;
import com.mtcoding.minigram._core.error.ex.ExceptionApi409;
import com.mtcoding.minigram.posts.PostRepository;
import com.mtcoding.minigram.reports.reasons.ReportReason;
import com.mtcoding.minigram.reports.reasons.ReportReasonCode;
import com.mtcoding.minigram.reports.reasons.ReportReasonRepository;
import com.mtcoding.minigram.stories.StoryRepository;
import com.mtcoding.minigram.users.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportReasonRepository reportReasonRepository;
    private final PostRepository postRepository;
    private final StoryRepository storyRepository;

    @Transactional
    public ReportResponse.DTO create(ReportRequest.SaveDTO reqDTO, User user) {

        //1. targer 존재 확인
        if (reqDTO.getReportType() == ReportType.POST) {
            if (!postRepository.existsById(reqDTO.getTargetId())) {
                throw new ExceptionApi404("대상 게시글을 찾을 수 없습니다.");
            }
        } else if (reqDTO.getReportType() == ReportType.STORY) {
            if (!storyRepository.existsById(reqDTO.getTargetId())) {
                throw new ExceptionApi404("대상 스토리를 찾을 수 없습니다.");
            }
        } else {
            throw new ExceptionApi400("지원하지 않는 신고 유형입니다.");
        }

        //2. reason 존재확인
        ReportReason reportReason = reportReasonRepository.findById(reqDTO.getReportReasonId())
                .orElseThrow(() -> new ExceptionApi404("지원하지 않는 신고 사유 유형입니다."));

        //3. 중복 신고 체크
        boolean isDuplicate = reportRepository.existsByTypeAndTargetIdAndReporterId(
                reqDTO.getReportType(), reqDTO.getTargetId(), user.getId());
        if (isDuplicate) throw new ExceptionApi400("이미 동일한 대상을 신고한 내역이 존재합니다.");

        //4. report 엔티티 생성
        Report report = reqDTO.toEntity(user, reportReason);

        //5. 저장 (영속성 컨텍스트)
        Report reportPS = reportRepository.save(report);

        return new ReportResponse.DTO(reportPS);
    }


    public ReportResponse.ReasonListDTO getReasons() {
        List<ReportResponse.ReasonItemDTO> reasonItemList = Arrays.stream(ReportReasonCode.values())
                .map(ReportResponse.ReasonItemDTO::from)
                .collect(Collectors.toList());
        return new ReportResponse.ReasonListDTO(reasonItemList);

    }

    // 승인
    @Transactional
    public ReportResponse.ApproveDTO approve(Integer reportId) {
        // 1) 잠금 걸고 신고 로딩 (중복 처리 방지)
        Report report = reportRepository.findByIdForUpdate(reportId)
                .orElseThrow(() -> new ExceptionApi404("신고를 찾을 수 없습니다."));

        // 2) 상태 검증
        if (!report.getStatus().equals(ReportStatus.PENDING)) {
            throw new ExceptionApi409("이미 처리된 신고입니다.");
        }

        // 3) 대상 조치
        String action;
        if (report.getType() == ReportType.POST) {
            postRepository.updateStatusHidden(report.getTargetId()); // 게시글 숨김
            action = "POST_HIDDEN";
        } else {
            storyRepository.updateStatusHidden(report.getTargetId()); // 스토리 숨김
            action = "STORY_HIDDEN";
        }

        // 4) 상태 변경
        report.approve(); // 내부에서 status=APPROVED, updatedAt 갱신

        // 5) 응답
        return new ReportResponse.ApproveDTO(report.getId(), report.getStatus().name(), action);
    }

    // 거절
    @Transactional
    public ReportResponse.RejectDTO reject(Integer reportId) {
        // 1) 잠금 걸고 신고 로딩
        Report report = reportRepository.findByIdForUpdate(reportId)
                .orElseThrow(() -> new ExceptionApi404("신고를 찾을 수 없습니다."));

        // 2) 상태 검증
        if (!report.getStatus().equals(ReportStatus.PENDING)) {
            throw new ExceptionApi409("이미 처리된 신고입니다.");
        }

        // 3) 상태 변경
        report.reject();   // status=REJECTED

        // 4) 응답
        return new ReportResponse.RejectDTO(report.getId(), report.getStatus().name());
    }
}



