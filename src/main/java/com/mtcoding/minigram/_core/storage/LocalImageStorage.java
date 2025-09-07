package com.mtcoding.minigram._core.storage;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class LocalImageStorage implements ImageStorage {

    private final String uploadDir = "uploads"; // 환경설정으로 분리 권장

    @Override
    public List<String> upload(List<MultipartFile> files) {
        try {
            Files.createDirectories(Path.of(uploadDir));
            List<String> urls = new ArrayList<>();
            for (MultipartFile f : files) {
                String saved = System.currentTimeMillis() + "_" + f.getOriginalFilename();
                Path target = Path.of(uploadDir, saved);
                f.transferTo(target.toFile());
                // 정적리소스 매핑(/uploads/**)에 맞게 조정
                urls.add("/uploads/" + saved);
            }
            return urls;
        } catch (Exception e) {
            throw new RuntimeException("이미지 업로드 실패", e);
        }
    }
}

