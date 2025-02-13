package com.ssafy.roCatRun.global.s3.controller;

import com.ssafy.roCatRun.domain.gameCharacter.service.GameCharacterService;
import com.ssafy.roCatRun.global.common.ApiResponse;
import com.ssafy.roCatRun.global.s3.dto.response.UploadResponse;
import com.ssafy.roCatRun.global.s3.exception.InvalidFileException;
import com.ssafy.roCatRun.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/domain/upload")
@CrossOrigin(origins = "*")
public class FileUploadController {
    private final S3Service s3Service;
    private final GameCharacterService gameCharacterService;

    /**
     * 캐릭터 이미지를 업로드하고 해당 URL을 캐릭터 정보에 업데이트합니다.
     * @param file 업로드할 이미지 파일
     * @param authentication 현재 인증된 사용자 정보
     * @return 업로드된 이미지 URL 정보
     * @throws IllegalStateException 인증 정보가 없는 경우
     * @throws InvalidFileException 파일이 유효하지 않은 경우
     */
    @PostMapping("/character-image")
    public ApiResponse<UploadResponse> uploadCharacterImage(
            @RequestParam("image") MultipartFile file,
            Authentication authentication
    ) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }

        String memberId = authentication.getPrincipal().toString();
        log.debug("Uploading character image for member: {}", memberId);

        if (file.isEmpty()) {
            throw new InvalidFileException("파일이 비어있습니다.");
        }

        validateFileSize(file);
        validateFileType(file);

        String imageUrl = s3Service.uploadFile(file);
        gameCharacterService.updateCharacterImage(Long.parseLong(memberId), imageUrl);

        return ApiResponse.success(new UploadResponse(imageUrl));
    }

    private void validateFileSize(MultipartFile file) {
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new InvalidFileException("파일 크기는 5MB를 초과할 수 없습니다.");
        }
    }

    private void validateFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidFileException("이미지 파일만 업로드 가능합니다.");
        }
    }
}