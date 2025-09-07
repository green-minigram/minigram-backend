package com.mtcoding.minigram._core.storage;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageStorage {
    List<String> upload(List<MultipartFile> files);
}
