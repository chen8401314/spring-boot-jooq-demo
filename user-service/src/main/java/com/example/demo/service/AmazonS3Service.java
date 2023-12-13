package com.example.demo.service;

import cn.hutool.core.date.DateUtil;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.SetBucketPolicyRequest;
import com.common.service.util.IdWorker;
import com.example.common.util.Consts;
import com.example.demo.config.ContentTypeProperties;
import com.example.demo.config.S3Properties;
import com.example.demo.dto.file.FileInfo;
import com.example.demo.dto.file.UploadConfigDTO;
import com.example.demo.util.FileUtil;
import com.google.common.collect.Maps;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.amazonaws.services.s3.Headers.CONTENT_TYPE;
import static com.example.common.util.Consts.POINT;
import static com.example.demo.util.UserConsts.DEFAULT_CONTENT_TYPE;

/**
 * @Title:
 * @Description:
 * @Author: chenx
 * @Date: 2023/7/14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AmazonS3Service {

    private final ContentTypeProperties contentTypeProperties;
    private final S3Properties s3Properties;
    private AmazonS3 amazonS3;

    /**
     * minio custom策略
     */
    private String policyJson = """
            {
                  "Statement": [
                      {
                          "Action": "s3:GetObject",
                          "Effect": "Allow",
                          "Principal": "*",
                          "Resource": "arn:aws:s3:::%s/*"
                      }
                  ],
                  "Version": "2012-10-17"
              }
            """;

    /**
     * 启动初始化桶的创建
     */
    @PostConstruct
    public void init() {
        if (!doesBucketExist(s3Properties.getBucket())) {
            getAmazonS3().createBucket(s3Properties.getBucket());
            getAmazonS3().setBucketPolicy(new SetBucketPolicyRequest(s3Properties.getBucket(), String.format(policyJson, s3Properties.getBucket())));
        }
    }

    //上传连接失效时间，单位（分钟）
    private long expire = 15;

    /**
     * 获取amazonS3
     *
     * @return
     */
    private AmazonS3 getAmazonS3() {
        if (amazonS3 != null) {
            return amazonS3;
        }
        amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(s3Properties.getAccessKey(), s3Properties.getSecretKey())))
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(new AwsClientBuilder
                        .EndpointConfiguration(s3Properties.getEndpoint(), "us-east-1"))
                .build();
        return amazonS3;
    }

    /**
     * 判断桶是否存在
     *
     * @param bucketName 桶名
     * @return
     */
    public boolean doesBucketExist(String bucketName) {
        return getAmazonS3().doesBucketExistV2(bucketName);
    }

    /**
     * 获取上传配置信息
     *
     * @param module     模块名称
     * @param objectName 文件名称
     * @return
     */
    public UploadConfigDTO getUploadConfig(String module, String objectName) {
        var dto = new UploadConfigDTO();
        dto.setPath(getPath(module, objectName));
        dto.setBucket(s3Properties.getBucket());
        //过期时间
        var expiry = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(expire));
        var request = new GeneratePresignedUrlRequest(s3Properties.getBucket(), dto.getPath(), HttpMethod.PUT)
                .withExpiration(expiry);
        dto.setContentType(getContentType(objectName));
        //强制前端需要在的上传方法添加对应的 Request Header( key: Content-Type, value: {fileType} )
        //不开启需要前端自行添加没有强制要求
        //用于解决文件上传到文件服务器之后没有对应的文件类型问题
        request.putCustomRequestHeader(CONTENT_TYPE, dto.getContentType());
        var url = getAmazonS3().generatePresignedUrl(request);
        dto.setUrl(url.toExternalForm());
        return dto;
    }

    /**
     * 获取拼接路径
     *
     * @param module   模块名称
     * @param fileName 文件名称
     * @return
     */
    public String getPath(String module, String fileName) {
        String dayStr = DateUtil.format(LocalDateTime.now(), Consts.DATE_FORMAT);
        String path = String.format("%s/%s/%s", module, dayStr, fileName);
        if (getAmazonS3().doesObjectExist(s3Properties.getBucket(), path)) {
            return String.format("%s/%s/%s/%s", module, dayStr, IdWorker.getId(), fileName);
        }
        return path;
    }

    /**
     * 获取下载地址
     *
     * @param objectName 文件路径
     * @return
     */
    public String getDownloadUrl(String objectName) {
        if (StringUtils.isBlank(objectName)) {
            return "";
        }
        return getAmazonS3().getUrl(s3Properties.getBucket(), objectName).toExternalForm();
    }


    /**
     * 获取下载地址
     *
     * @param paths 文件路径集合
     * @return
     */
    public Map<String, String> getDownloadUrlMap(List<String> paths) {
        Map<String, String> result = Maps.newHashMap();
        paths.forEach(path -> result.put(path, getDownloadUrl(path)));
        return result;
    }

    /**
     * 获取文件contentType
     *
     * @param fileName
     * @return
     */
    public String getContentType(String fileName) {
        int index = fileName.lastIndexOf(POINT);
        if (index < 0) {
            //获取不到扩展名
            return DEFAULT_CONTENT_TYPE;
        }
        fileName = fileName.toLowerCase();
        var ext = fileName.substring(index + 1);
        return contentTypeProperties.getData().getOrDefault(ext.toLowerCase(), DEFAULT_CONTENT_TYPE);
    }

    public FileInfo uploadFile(String objectName, File file) {
        PutObjectResult result = getAmazonS3().putObject(s3Properties.getBucket(), objectName, file);
        return FileUtil.obtainFileInfo(result, s3Properties.getBucket(), objectName);
    }

}
