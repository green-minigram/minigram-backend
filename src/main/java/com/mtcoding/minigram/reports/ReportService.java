package com.mtcoding.minigram.reports;

import com.mtcoding.minigram._core.error.ex.ExceptionApi400;
import com.mtcoding.minigram._core.error.ex.ExceptionApi404;
import com.mtcoding.minigram.posts.Post;
import com.mtcoding.minigram.posts.PostRepository;
import com.mtcoding.minigram.posts.images.PostImage;
import com.mtcoding.minigram.reports.reasons.ReportReason;
import com.mtcoding.minigram.reports.reasons.ReportReasonCode;
import com.mtcoding.minigram.reports.reasons.ReportReasonRepository;
import com.mtcoding.minigram.stories.Story;
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

    @Transactional(readOnly = true)
    public ReportResponse.AdminDetailDTO find(Integer reportId) {

        // 1) 신고 본문 + 신고자 + 신고 사유 로딩 (존재 검증)
        var report = reportRepository.findWithReporterAndReasonById(reportId)
                .orElseThrow(() -> new ExceptionApi404("존재하지 않는 신고입니다."));

        // 2) 대상 타입 분기 (STORY / POST)
        if (report.getType() == ReportType.STORY) {
            // 2-1) STORY 조회 (작성자 포함) + 존재 검증
            Story story = storyRepository.findWithAuthorById(report.getTargetId())
                    .orElseThrow(() -> new ExceptionApi404("대상 스토리가 존재하지 않습니다."));

            // 2-2) STORY 집계: 좋아요/댓글 수
            int likeCount = storyRepository.countLikesByStoryId(story.getId());

            // 2-3) STORY 상세 DTO 조립 및 반환
            return ReportResponse.AdminDetailDTO.fromStory(report, story, likeCount);
        }

        // 3) POST 경로

        // 3-1) POST 조회 (작성자 포함) + 존재 검증
        Post post = postRepository.findByIdWithAuthor(report.getTargetId())
                .orElseThrow(() -> new ExceptionApi404("대상 게시글이 존재하지 않습니다."));

        // 3-2) POST 미디어 목록 로딩
        List<PostImage> images = postRepository.findImagesByPostId(post.getId());

        // 3-3) POST 집계: 좋아요/댓글 수
        int likeCount = postRepository.countLikesByPostId(post.getId());
        int commentCount = postRepository.countCommentsByPostId(post.getId());

        // 3-4) POST 상세 DTO 조립 및 반환
        return ReportResponse.AdminDetailDTO.fromPost(report, post, images, likeCount, commentCount);
    }
}




