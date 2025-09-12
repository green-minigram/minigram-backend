package com.mtcoding.minigram.feed;

import com.mtcoding.minigram._core.constants.FeedConstants;
import com.mtcoding.minigram.posts.Post;
import com.mtcoding.minigram.posts.images.PostImage;
import com.mtcoding.minigram.stories.Story;
import com.mtcoding.minigram.users.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class FeedResponse {
    @Data
    public static class PostListDTO {
        private Integer current;     // 현재 페이지(0-base)
        private Integer size;        // 페이지당 개수
        private Integer totalCount;  // 전체 글 수
        private Integer totalPage;   // 전체 페이지 수
        private Integer prev;        // current - 1
        private Integer next;        // current + 1
        private Boolean isFirst;     // current == 0
        private Boolean isLast;      // (totalPage - 1) == current
        private List<PostItemDTO> postList;

        public PostListDTO(List<PostItemDTO> postList, Integer current, Integer postTotalCount) {
            this.postList = postList;
            this.current = current;
            this.size = postList.size();
            this.totalCount = postTotalCount;
            this.totalPage = makeTotalPage(totalCount);
            this.prev = Math.max(0, current - 1);
            this.next = totalPage == 0 ? 0 : Math.min(totalPage - 1, current + 1);
            this.isFirst = current == 0;
            this.isLast = totalPage == 0 || current.equals(totalPage - 1);
        }

        private int makeTotalPage(int totalCount) {
            int postsPerPage = FeedConstants.POSTS_PER_PAGE;
            return (totalCount + postsPerPage - 1) / postsPerPage;
        }
    }

    @Data
    public static class PostItemDTO {
        private Boolean isAdvertisement;
        private Integer postId;
        private String content;
        private Boolean isOwner;
        private Boolean isLiked;
        private Integer likesCount;
        private Integer commentCount;
        private LocalDateTime createdAt;
        private UserDTO user;
        private List<PostImageDTO> postImageList;


        @Data
        public class UserDTO {
            private Integer userId;
            private String username;
            private String profileImageUrl;
            private Boolean isFollowing;

            public UserDTO(User user, Boolean isFollowing) {
                this.userId = user.getId();
                this.username = user.getUsername();
                this.profileImageUrl = user.getProfileImageUrl();
                this.isFollowing = isFollowing;
            }
        }

        @Data
        public class PostImageDTO {
            private Integer postImageId;
            private String url;

            public PostImageDTO(PostImage postImage) {
                this.postImageId = postImage.getId();
                this.url = postImage.getUrl();
            }
        }

        public PostItemDTO(Post post, Boolean isAdvertisement, Boolean isFollowing, Boolean isOwner, Boolean isLiked, Integer likesCount, Integer commentCount, List<PostImage> postImageList) {
            this.user = new UserDTO(post.getUser(), isFollowing);
            this.postImageList = postImageList.stream().map(postImage -> new PostImageDTO(postImage)).toList();
            this.isAdvertisement = isAdvertisement;
            this.postId = post.getId();
            this.content = post.getContent();
            this.isOwner = isOwner;
            this.isLiked = isLiked;
            this.likesCount = likesCount;
            this.commentCount = commentCount;
            this.createdAt = post.getCreatedAt();
        }
    }

    @Data
    public static class PreviewListDTO {
        private Integer current;     // 현재 페이지(0-base)
        private Integer size;        // 페이지당 개수
        private Integer totalCount;  // 전체 글 수
        private Integer totalPage;   // 전체 페이지 수
        private Integer prev;        // current - 1
        private Integer next;        // current + 1
        private Boolean isFirst;     // current == 0
        private Boolean isLast;      // (totalPage - 1) == current
        private List<PreviewItemDTO> previewList;

        public PreviewListDTO(List<PreviewItemDTO> previewList, Integer current, Integer totalCount) {
            this.previewList = previewList;
            this.current = current;
            this.size = 10;
            this.totalCount = totalCount;
            this.totalPage = makeTotalPage(totalCount, size);
            this.prev = Math.max(0, current - 1);
            this.next = totalPage == 0 ? 0 : Math.min(totalPage - 1, current + 1);
            this.isFirst = current == 0;
            this.isLast = (totalPage - 1) == current;
        }

        private int makeTotalPage(int totalCount, int size) {
            if (size <= 0) return 0;
            int rest = (totalCount % size) > 0 ? 1 : 0;
            return (totalCount / size) + rest;
        }
    }

    @Data
    public static class PreviewItemDTO {
        private Integer userId;
        private String username;
        private String profileImageUrl;
        private Boolean hasUnseen; // 최근 5개의 스토리 중 한 개라도 안읽은 스토리가 있는지 여부

        public PreviewItemDTO(Integer userId, String username, String profileImageUrl, Boolean hasUnseen) {
            this.userId = userId;
            this.username = username;
            this.profileImageUrl = profileImageUrl;
            this.hasUnseen = hasUnseen;
        }
    }

    @Data
    public static class StoryListDTO {
        private List<StoryItemDTO> storyList;

        public StoryListDTO(List<StoryItemDTO> storyList) {
            this.storyList = storyList;
        }
    }

    @Data
    public static class StoryItemDTO {
        private UserDTO user;
        private StoryDTO story;
        private Boolean isFollowing;
        private Boolean isOwner;
        private Boolean isLiked;
        private Integer likeCount;

        @Data
        public class UserDTO {
            private Integer userId;
            private String username;
            private String profileImageUrl;

            public UserDTO(User user) {
                this.userId = user.getId();
                this.username = user.getUsername();
                this.profileImageUrl = user.getProfileImageUrl();
            }
        }

        @Data
        public static class StoryDTO {
            private Integer storyId;
            private String videoUrl;
            private String thumbnailUrl;
            private LocalDateTime createdAt;

            public StoryDTO(Story story) {
                this.storyId = story.getId();
                this.videoUrl = story.getVideoUrl();
                this.thumbnailUrl = story.getThumbnailUrl();
                this.createdAt = story.getCreatedAt();
            }
        }

        public StoryItemDTO(Story story, Boolean isFollowing, Boolean isOwner, Boolean isLiked, Integer likeCount) {
            this.user = new UserDTO(story.getUser());
            this.story = new StoryDTO(story);
            this.isFollowing = isFollowing;
            this.isOwner = isOwner;
            this.isLiked = isLiked;
            this.likeCount = likeCount;
        }
    }

    @Data
    public static class ListDTO {
        private PreviewListDTO previews;
        private PostListDTO posts;

        public ListDTO(PreviewListDTO previews, PostListDTO posts) {
            this.previews = previews;
            this.posts = posts;
        }
    }
}
