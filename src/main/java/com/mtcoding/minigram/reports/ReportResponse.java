package com.mtcoding.minigram.reports;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mtcoding.minigram.posts.Post;
import com.mtcoding.minigram.posts.images.PostImage;
import com.mtcoding.minigram.reports.reasons.ReportReasonCode;
import com.mtcoding.minigram.stories.Story;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ReportResponse {

    // ===================== 사유 목록 =====================
    @Data
    public static class ReasonListDTO {
        private List<ReasonItemDTO> reasonList;

        public ReasonListDTO(List<ReasonItemDTO> reasonList) {
            this.reasonList = reasonList;
        }
    }

    @Data
    @Builder
    public static class ReasonItemDTO {
        private Integer id;
        private String code;
        private String label;

        public static ReasonItemDTO from(ReportReasonCode reasonCode) {
            return ReasonItemDTO.builder()
                    .id(reasonCode.getId())
                    .code(reasonCode.name())
                    .label(reasonCode.getLabel())
                    .build();
        }
    }


    @Data
    public static class DTO {
        private Integer reportId;
        private ReportType reportType;
        private Integer targetId;
        private Integer userId;
        private Integer reasonId;
        private ReportStatus status;

        public DTO(Report report) {
            this.reportId = report.getId();
            this.reportType = report.getType();
            this.targetId = report.getTargetId();
            this.userId = report.getReporter().getId();
            this.reasonId = report.getReason().getId();
            this.status = report.getStatus();
        }
    }

    // ===================== 관리자 뷰(확장) =====================

    @Data
    @Builder
    public static class AdminViewDTO {
        private Integer reportId;
        private ReportType type;
        private Integer targetId;
        private String targetContent;         // 필요 없으면 제거 가능
        private String reporterUsername;
        private String reasonLabel;           // label 사용
        private ReportStatus status;
        private LocalDateTime createdAt;

        public static AdminViewDTO from(Report report) {
            return AdminViewDTO.builder()
                    .reportId(report.getId())
                    .type(report.getType())
                    .targetId(report.getTargetId())
                    .reporterUsername(report.getReporter().getUsername())
                    .reasonLabel(report.getReason().getLabel())
                    .status(report.getStatus())
                    .createdAt(report.getCreatedAt())
                    .build();
        }
    }

    // ===================== 신고 상세(기본) =====================

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdminDetailDTO {

        private Integer reportId;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime reportedAt;

        private ReporterDTO reporter;
        private ReportedObjectDTO reportedObject;
        private String reportReasonLabel; // label만 노출
        private String status;            // PENDING/APPROVED/REJECTED

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ReporterDTO {
            private Integer userId;
            private String username;
            private String profileImageUrl;
        }

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ReportedObjectDTO {
            private String type;       // POST / STORY
            private Integer objectId;
            private AuthorDTO author;
            private List<MediaDTO> mediaList;
            private String content;    // post.content (스토리는 null)
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            private LocalDateTime postedAt;
            private LikesDTO likes;
            private Integer commentsCount;
        }

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class AuthorDTO {
            private Integer userId;
            private String username;
            private String profileImageUrl;
        }

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class MediaDTO {
            private String type; // IMAGE / VIDEO
            private String url;
        }

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class LikesDTO {
            private Integer count;
            private boolean isLiked;
        }

        public static AdminDetailDTO fromStory(Report report, Story story,
                                               int likeCount, int commentCount) {
            var media = new java.util.ArrayList<MediaDTO>();
            if (story.getVideoUrl() != null) media.add(new MediaDTO("VIDEO", story.getVideoUrl()));
            if (story.getThumbnailUrl() != null) media.add(new MediaDTO("IMAGE", story.getThumbnailUrl()));

            var author = new AuthorDTO(
                    story.getUser().getId(),
                    story.getUser().getUsername(),
                    story.getUser().getProfileImageUrl()
            );

            var object = ReportedObjectDTO.builder()
                    .type("STORY")
                    .objectId(story.getId())
                    .author(author)
                    .mediaList(media)
                    .content(null)
                    .postedAt(story.getCreatedAt())
                    .likes(new LikesDTO(likeCount, false))
                    .commentsCount(commentCount)
                    .build();

            return AdminDetailDTO.builder()
                    .reportId(report.getId())
                    .reportedAt(report.getCreatedAt())
                    .reporter(new ReporterDTO(
                            report.getReporter().getId(),
                            report.getReporter().getUsername(),
                            report.getReporter().getProfileImageUrl()))
                    .reportedObject(object)
                    .reportReasonLabel(report.getReason().getLabel())
                    .status(report.getStatus().name())
                    .build();
        }

        public static AdminDetailDTO fromPost(Report report, Post post,
                                              java.util.List<PostImage> images,
                                              int likeCount, int commentCount) {
            var media = images.stream()
                    .map(i -> new MediaDTO("IMAGE", i.getUrl()))
                    .toList();

            var author = new AuthorDTO(
                    post.getUser().getId(),
                    post.getUser().getUsername(),
                    post.getUser().getProfileImageUrl()
            );

            var object = ReportedObjectDTO.builder()
                    .type("POST")
                    .objectId(post.getId())
                    .author(author)
                    .mediaList(media)
                    .content(post.getContent())
                    .postedAt(post.getCreatedAt())
                    .likes(new LikesDTO(likeCount, false))
                    .commentsCount(commentCount)
                    .build();

            return AdminDetailDTO.builder()
                    .reportId(report.getId())
                    .reportedAt(report.getCreatedAt())
                    .reporter(new ReporterDTO(
                            report.getReporter().getId(),
                            report.getReporter().getUsername(),
                            report.getReporter().getProfileImageUrl()))
                    .reportedObject(object)
                    .reportReasonLabel(report.getReason().getLabel())
                    .status(report.getStatus().name())
                    .build();
        }
    }
}
