package com.mtcoding.minigram.storage;

import com.mtcoding.minigram._core.util.Resp;
import com.mtcoding.minigram.users.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PresignController {

    private final PresignService presignService;
    private final HttpSession session;

    @PostMapping("/api/storage/presignUrl")
    public ResponseEntity<?> createUploadUrl(@RequestBody PresignRequest.UploadDTO reqDTO) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        PresignResponse.UploadDTO respDTO = presignService.createUploadUrl(reqDTO, sessionUser);
        return Resp.ok(respDTO);
    }
}