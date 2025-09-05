package com.mtcoding.minigram.storage;

import com.mtcoding.minigram._core.error.ex.ExceptionApi400;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PresignService {

    private final S3Presigner presigner;  // S3Config에서 기본 체인으로 생성
    private final StorageProps props;     // 버킷/TTL 정책

    // 업로드용 presign 발급
    public PresignResponse.UploadDTO createUploadUrl(PresignRequest.UploadDTO reqDTO, Integer userId) {
        UploadType uploadType = reqDTO.getUploadType();
        String mimeType = reqDTO.getMimeType();

        // 1. uploadType, mimeType 확인
        if (uploadType == UploadType.IMAGE && !mimeType.startsWith("image/"))
            throw new ExceptionApi400("잘못된 이미지 파일 contentType입니다. (image/*)");
        if (uploadType == UploadType.VIDEO && !mimeType.startsWith("video/"))
            throw new ExceptionApi400("잘못된 영상 파일 contentType입니다. (video/*)");

        // 2. key 생성: /uploadType/사용자id/UUID.확장자
        String ext = mapExt(mimeType);
        String folder = (uploadType == UploadType.IMAGE) ? "images" : "videos";
        String key = "%s/%d/%s.%s".formatted(folder, userId, uuid(), ext);

        // 3. PutObjectRequest
        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .contentType(mimeType)
                .build();

        // 4. PutObjectPresignRequest
        Duration ttl = Duration.ofSeconds(Math.max(60, props.getPresignExpSeconds()));
        PutObjectPresignRequest preReq = PutObjectPresignRequest.builder()
                .signatureDuration(ttl)
                .putObjectRequest(putReq)
                .build();

        // 5. PresignedPutObjectRequest
        PresignedPutObjectRequest signed = presigner.presignPutObject(preReq);

        return new PresignResponse.UploadDTO(key, signed.url().toString(), (int) ttl.getSeconds(), mimeType);
    }

    private String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    // MIME 타입(Content-Type) → 파일 확장자 매핑 (상수)
    private static final Map<String, String> EXTENSION = Map.ofEntries(
            Map.entry("image/jpeg", "jpg"),
            Map.entry("image/png", "png"),
            Map.entry("image/webp", "webp"),
            Map.entry("image/gif", "gif"),
            Map.entry("video/mp4", "mp4"),
            Map.entry("video/webm", "webm"),
            Map.entry("video/quicktime", "mov")
    );

    // Content-Type을 받아서 확장자를 반환
    private String mapExt(String mimeType) {
        return EXTENSION.getOrDefault(mimeType.toLowerCase(), "bin");
    }
}