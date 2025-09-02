package com.mtcoding.minigram.advertisements;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AdvertisementsController {
    private final AdvertisementService advertisementService;
    private final HttpSession session;
}
