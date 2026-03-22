package com.pet.petmily.s3.component;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;



@Component
@Getter
@Setter

public class S3Component {

        private String bucket="petmily-bucket";

        @Value("${cloud.aws.region.static}")
        private String region;
        @Value("${cloud.aws.credentials.access-key}")
        private String accessKey;
        @Value("${cloud.aws.credentials.secret-key}")
        private String secretKey;


}
