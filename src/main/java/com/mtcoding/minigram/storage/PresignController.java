package com.mtcoding.minigram.storage;

import com.mtcoding.minigram._core.util.Resp;
import com.mtcoding.minigram.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PresignController {

    private final PresignService presignService;

    @PostMapping("/s/api/storage/presignedUrl")
    public ResponseEntity<?> createUploadUrl(@RequestBody PresignRequest.UploadDTO reqDTO, @AuthenticationPrincipal User user) {
        PresignResponse.UploadDTO respDTO = presignService.createUploadUrl(reqDTO, user.getId());
        return Resp.ok(respDTO);
    }
}