package com.mtcoding.minigram.advertisements;

import com.mtcoding.minigram._core.util.Resp;
import com.mtcoding.minigram.users.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/s/api/admin/advertisements")
public class AdvertisementsController {
    private final AdvertisementService advertisementService;
    private final HttpSession session;

    @PostMapping()
    public ResponseEntity<?> create(@AuthenticationPrincipal User user, @RequestBody AdvertisementRequest.CreateDTO reqDTO) {
        AdvertisementResponse.CreateDTO respDTO = advertisementService.create(reqDTO, user.getId());
        return Resp.ok(respDTO); // DetailDTO 반환
    }

    @PutMapping("/{adId}")
    public ResponseEntity<?> update(@PathVariable Integer adId, @AuthenticationPrincipal User user,
                                    @RequestBody AdvertisementRequest.UpdateDTO reqDTO) {
        AdvertisementResponse.UpdateDTO respDTO = advertisementService.update(adId, reqDTO, user.getId());
        return Resp.ok(respDTO);
    }

}
