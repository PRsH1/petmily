package com.pet.petmily.s3.controller;


import com.pet.petmily.s3.service.FileUploadService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class DownloadController {

    private final FileUploadService fileUploadService;

    @ApiOperation(value = "이미지 다운로드", notes = "이미지를 다운로드합니다")
    @GetMapping(value = "image/download")
    public ResponseEntity<byte[]> downloadImage(String fileName, @RequestHeader(value = "Accept") String acceptHeader) {
        log.info("이미지 다운로드 api 호출");
        byte[] imageData = fileUploadService.downloadImage(fileName);

        HttpHeaders headers = new HttpHeaders();


        MediaType mediaType = getMediaType(acceptHeader);
        if (mediaType == null) {

            mediaType = MediaType.IMAGE_JPEG;
        }

        headers.setContentType(mediaType);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(imageData.length)
                .body(imageData);
    }

    private MediaType getMediaType(String acceptHeader) {
        if (acceptHeader != null) {
            if (acceptHeader.contains(MediaType.IMAGE_JPEG_VALUE)) {
                return MediaType.IMAGE_JPEG;
            } else if (acceptHeader.contains(MediaType.IMAGE_PNG_VALUE)) {
                return MediaType.IMAGE_PNG;
            } else if (acceptHeader.contains(MediaType.IMAGE_GIF_VALUE)) {
                return MediaType.IMAGE_GIF;
            }
        }
        return null;
    }

}
