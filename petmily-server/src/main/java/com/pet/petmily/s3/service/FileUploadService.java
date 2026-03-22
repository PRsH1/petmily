package com.pet.petmily.s3.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.pet.petmily.s3.dto.FileDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileUploadService {

    private final UploadService s3Service;

    public List<String> uploadImages(MultipartFile[] files) {
        FileDTO fileDTO = new FileDTO();
        List<String> uploadedFileUrls = new ArrayList<>();

        for (MultipartFile file : files) {

            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try (InputStream inputStream = file.getInputStream()) {
                s3Service.uploadFile(inputStream, objectMetadata, fileName);
                String fileUrl = s3Service.getFileUrl(fileName);
                log.info("fileUrl = " + fileUrl);
                uploadedFileUrls.add(fileUrl);
            } catch (IOException e) {
                throw new IllegalArgumentException(String.format("파일 변환 에러 (%s)", file.getOriginalFilename()));
            }
        }

        return uploadedFileUrls;
    }

    // 기존 확장자명을 유지한 채, 유니크한 파일의 이름을 생성하는 로직
    private String createFileName(String originalFileName) {
        log.info("createFileName() 호출");

        String folderName = "pet";
        return folderName+"/"+UUID.randomUUID().toString().concat(getFileExtension(originalFileName));
    }

    // 파일의 확장자명을 가져오는 로직
    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(String.format("잘못된 형식의 파일 (%s) 입니다", fileName));
        }
    }

    public byte[] downloadImage(String fileName) {
        log.info("downloadImage() 호출");
        return s3Service.downloadFile(fileName);
    }
}