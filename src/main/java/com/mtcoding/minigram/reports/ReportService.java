package com.mtcoding.minigram.reports;

import com.mtcoding.minigram._core.error.ex.ExceptionApi400;
import com.mtcoding.minigram._core.error.ex.ExceptionApi404;
import com.mtcoding.minigram.posts.PostRepository;
import com.mtcoding.minigram.reports.reasons.ReportReason;
import com.mtcoding.minigram.reports.reasons.ReportReasonRepository;
import com.mtcoding.minigram.stories.StoryRepository;
import com.mtcoding.minigram.users.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
            if (!postRepository.exitsById(reqDTO.getTargetId())) {
                throw new ExceptionApi404("대상 게시글을 찾을 수 없습니다.");
            }
        } else if (reqDTO.getReportType() == ReportType.STORY) {
            if (!storyRepository.existsById(reqDTO.getTargetId())) {
                throw new ExceptionApi404("대상 스토리를 찾을 수 없습니다.");
            }
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
}



