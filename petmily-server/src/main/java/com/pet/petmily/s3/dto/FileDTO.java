package com.pet.petmily.s3.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class FileDTO {

        private String fileName;
        private String fileUrl;
        private String originalName;
        private String contentType;
        private long size;
}
