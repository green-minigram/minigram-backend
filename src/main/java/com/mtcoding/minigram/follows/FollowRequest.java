package com.mtcoding.minigram.follows;

import com.mtcoding.minigram.stories.Story;
import com.mtcoding.minigram.stories.StoryStatus;
import com.mtcoding.minigram.users.User;
import lombok.Data;

public class FollowRequest {

    @Data
    public static class CreateDTO {
        private Integer followeeId;

        public Follow toEntity(User follower, User followee) {
            return Follow.builder()
                    .follower(follower)
                    .followee(followee)
                    .build();
        }
    }
}
