package com.example.demo.config;

import cn.hutool.core.util.StrUtil;
import com.example.demo.dto.FileDTO;
import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class MinIOTemplate {

    private MinioClient minioClient;
    private MinIOProperties minIOProperties;

    public MinIOTemplate(MinIOProperties minIOProperties) {
        this.minIOProperties = minIOProperties;
        this.minioClient = MinioClient
                .builder()
                .credentials(minIOProperties.getAccessKey(), minIOProperties.getSecretKey())
                .endpoint(minIOProperties.getEndpoint())
                .build();
    }


    private final static String separator = "/";

    /**
     * @param dirPath
     * @param filename yyyy/mm/dd/file.jpg
     * @return
     */
    public String builderFilePath(String dirPath, String filename) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!StringUtils.isEmpty(dirPath)) {
            stringBuilder.append(dirPath).append(separator);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String todayStr = sdf.format(new Date());
        stringBuilder.append(todayStr).append(separator);
        stringBuilder.append(System.currentTimeMillis()).append(separator);
        stringBuilder.append(filename);
        return stringBuilder.toString();
    }

    /**
     * 上传图片文件
     *
     * @param prefix      文件前缀
     * @param filename    文件名
     * @param inputStream 文件流
     * @return 文件全路径
     */
    public FileDTO uploadFile(String prefix, String filename, InputStream inputStream) {
        String filePath = builderFilePath(prefix, filename);
        FileDTO dto = new FileDTO();
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object(filePath)
                    .bucket(minIOProperties.getBucket()).stream(inputStream, inputStream.available(), -1)
                    .build();
            minioClient.putObject(putObjectArgs);
            ObjectStat objectStat = minioClient.statObject(StatObjectArgs.builder().bucket(minIOProperties.getBucket()).object(filePath).build());
            String file = StrUtil.subAfter(objectStat.name(), separator, true);
            dto.setBucketName(objectStat.bucketName());
            dto.setUploadTime(objectStat.createdTime());
            dto.setFileType(StrUtil.subAfter(file, ".", true));
            dto.setFileName(StrUtil.subBefore(file, ".", true));
            dto.setFilePath(objectStat.name());
            dto.setSize(objectStat.length());
            dto.setFullFilePath(new StringBuilder(minIOProperties.getReadPath()).append(separator).append(dto.getBucketName()).append(separator).append(dto.getFilePath()).toString());
            return dto;
        } catch (Exception ex) {
            log.error("minio put file error.", ex);
            throw new RuntimeException("上传文件失败");
        }
    }

    /**
     * 上传html文件
     *
     * @param prefix      文件前缀
     * @param filename    文件名
     * @param inputStream 文件流
     * @return 文件全路径
     */
    public String uploadHtmlFile(String prefix, String filename, InputStream inputStream) {
        String filePath = builderFilePath(prefix, filename);
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object(filePath)
                    .contentType("text/html")
                    .bucket(minIOProperties.getBucket()).stream(inputStream, inputStream.available(), -1)
                    .build();
            minioClient.putObject(putObjectArgs);
            StringBuilder urlPath = new StringBuilder(minIOProperties.getReadPath());
            urlPath.append(separator + minIOProperties.getBucket());
            urlPath.append(separator);
            urlPath.append(filePath);
            return urlPath.toString();
        } catch (Exception ex) {
            log.error("minio put file error.", ex);
            ex.printStackTrace();
            throw new RuntimeException("上传文件失败");
        }
    }

    /**
     * 删除文件
     *
     * @param pathUrl 文件全路径
     */
    public void delete(String pathUrl) {
        String key = pathUrl.replace(minIOProperties.getEndpoint() + "/", "");
        int index = key.indexOf(separator);
        String bucket = key.substring(0, index);
        String filePath = key.substring(index + 1);
        // 删除Objects
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket(bucket).object(filePath).build();
        try {
            minioClient.removeObject(removeObjectArgs);
        } catch (Exception e) {
            log.error("minio remove file error.  pathUrl:{}", pathUrl);
            e.printStackTrace();
        }
    }


    /**
     * 下载文件
     *
     * @param pathUrl 文件全路径
     * @return 文件流
     */
    public byte[] downLoadFile(String pathUrl) {
        String key = pathUrl.replace(minIOProperties.getEndpoint() + "/", "");
        int index = key.indexOf(separator);
        String bucket = key.substring(0, index);
        String filePath = key.substring(index + 1);
        InputStream inputStream = null;
        try {
            inputStream = minioClient.getObject(GetObjectArgs.builder().bucket(minIOProperties.getBucket()).object(filePath).build());
        } catch (Exception e) {
            log.error("minio down file error.  pathUrl:{}", pathUrl);
            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while (true) {
            try {
                if (!((rc = inputStream.read(buff, 0, 100)) > 0)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            byteArrayOutputStream.write(buff, 0, rc);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
