package com.example.demo.config;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
* @Title: s3配置
* @Description:
* @Author: chenx
* @Date: 2023/7/21
*/
@Data
@Component
public class S3Properties {

    @Value("${s3.accessKey}")
    private String accessKey;
    @Value("${s3.secretKey}")
    private String secretKey;
    @Value("${s3.bucket}")
    private String bucket;
    @Value("${s3.endpoint}")
    private String endpoint;
}
