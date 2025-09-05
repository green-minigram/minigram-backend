package com.mtcoding.minigram.reports;

import com.mtcoding.minigram.posts.Post;
import com.mtcoding.minigram.posts.PostRepository;
import com.mtcoding.minigram.stories.Story;
import com.mtcoding.minigram.stories.StoryRepository;
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
    private final PostRepository postRepository;
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;


    @Transactional
    public Report createReport(ReportRequest.SaveDTO dto, SessionUser sessionUser) {
        // 세션 사용자로 User 엔티티 조회
        var reporter = userRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("신고자(User) 없음"));

        // 신고 사유 조회
        var reason = reasonRepository.findById(dto.getReasonId())
                .orElseThrow(() -> new IllegalArgumentException("신고 사유 없음"));

        Report report = Report.builder()
                .type(dto.getType())
                .targetId(dto.getTargetId())
                .reporter(reporter)  // 세션 사용자 기반
                .reason(reason)
                .status(ReportStatus.PENDING)
                .build();

        return reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public ReportResponse.AdminViewDTO getReportDetail(Integer reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("해당 신고 없음"));

        // 대상 정보 가져오기
        String targetContent = null;
        if (report.getType() == ReportType.POST) {
            Post post = postRepository.findById(report.getTargetId())
                    .orElseThrow(() -> new IllegalArgumentException("신고 대상 포스트 없음"));
            targetContent = post.getContent();
        } else if (report.getType() == ReportType.STORY) {
            Story story = storyRepository.findById(report.getTargetId())
                    .orElseThrow(() -> new IllegalArgumentException("신고 대상 스토리 없음"));
            targetContent = story.getThumbnailUrl(); // 예시로 썸네일
        }

        return ReportResponse.AdminViewDTO.from(report, targetContent);
    }
}



