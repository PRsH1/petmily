package com.pet.petmily.s3.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.pet.petmily.s3.dto.FileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileUploadService {

    private final UploadService s3Service;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );

    public List<String> uploadImages(MultipartFile[] files) {
        List<String> uploadedFileUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            validateFile(file);

            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try (InputStream inputStream = file.getInputStream()) {
                s3Service.uploadFile(inputStream, objectMetadata, fileName);
                String fileUrl = s3Service.getFileUrl(fileName);
                log.info("fileUrl = {}", fileUrl);
                uploadedFileUrls.add(fileUrl);
            } catch (IOException e) {
                throw new IllegalArgumentException(String.format("파일 변환 에러 (%s)", file.getOriginalFilename()));
            }
        }
        return uploadedFileUrls;
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "빈 파일은 업로드할 수 없습니다.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("파일 크기가 제한을 초과합니다. 최대 %dMB까지 업로드 가능합니다. (현재: %.1fMB)",
                            MAX_FILE_SIZE / 1024 / 1024, file.getSize() / 1024.0 / 1024.0));
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "허용되지 않는 파일 형식입니다. JPG, PNG, GIF, WEBP 이미지만 업로드 가능합니다.");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "허용되지 않는 확장자입니다. 허용 확장자: " + String.join(", ", ALLOWED_EXTENSIONS));
            }
        }
    }

    private String createFileName(String originalFileName) {
        String folderName = "pet";
        return folderName + "/" + UUID.randomUUID().toString().concat(getFileExtension(originalFileName));
    }

    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        } catch (StringIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(String.format("잘못된 형식의 파일 (%s) 입니다", fileName));
        }
    }

    public byte[] downloadImage(String fileName) {
        log.info("downloadImage() 호출");
        return s3Service.downloadFile(fileName);
    }
}
