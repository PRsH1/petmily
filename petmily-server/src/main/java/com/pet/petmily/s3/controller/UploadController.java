package com.pet.petmily.s3.controller;




import com.pet.petmily.s3.response.ImageResponse;
import com.pet.petmily.s3.service.FileUploadService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor // final이 선언된 모든 필드를 인자값으로 하는 생성자를 대신 생성해줌
@RestController
public class UploadController {

    private final FileUploadService fileUploadService;

    @ApiOperation(value = "이미지 업로드", notes = "이미지 업로드")
    @PostMapping("image/upload")
    public ImageResponse uploadImages(@RequestPart("file") MultipartFile[] files) {
        log.info("uploadImages() 호출");
        return new ImageResponse("이미지 업로드 성공", "이미지 업로드 성공", fileUploadService.uploadImages(files));
    }


}