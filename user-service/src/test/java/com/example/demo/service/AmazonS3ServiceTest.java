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
import com.example.common.util.Consts;
import com.example.demo.config.ContentTypeProperties;
import com.example.demo.config.S3Properties;
import com.example.demo.dto.file.FileInfo;
import com.example.demo.dto.file.UploadConfigDTO;
import com.example.demo.util.FileUtil;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@TestInstance(PER_CLASS)
@ExtendWith(MockitoExtension.class)
class AmazonS3ServiceTest {

    @Mock
    private ContentTypeProperties contentTypeProperties;

    @Mock
    private S3Properties s3Properties;

    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private AmazonS3Service amazonS3Service;

    private final String BUCKET_NAME = "test-bucket";
    private final String ACCESS_KEY = "test-access-key";
    private final String SECRET_KEY = "test-secret-key";
    private final String ENDPOINT = "http://localhost:9000";
    private final String DATE_STR = "2023-07-14";

    @BeforeEach
    void setUp() {
        Map<String, String> contentTypeMap = new HashMap<>();
        contentTypeMap.put("jpg", "image/jpeg");
        contentTypeMap.put("png", "image/png");
        contentTypeMap.put("txt", "text/plain");

        // 使用lenient()方法标记可能未使用的存根
        lenient().when(contentTypeProperties.getData()).thenReturn(contentTypeMap);
        lenient().when(s3Properties.getBucket()).thenReturn(BUCKET_NAME);

        // 使用反射注入私有字段
        ReflectionTestUtils.setField(amazonS3Service, "amazonS3", amazonS3);
    }

    @Test
    void testInit() {
        when(amazonS3.doesBucketExistV2(anyString())).thenReturn(false);

        amazonS3Service.init();

        verify(amazonS3).createBucket(BUCKET_NAME);
        verify(amazonS3).setBucketPolicy(any(SetBucketPolicyRequest.class));
    }

    @Test
    void testInitBucketExists() {
        when(amazonS3.doesBucketExistV2(anyString())).thenReturn(true);

        amazonS3Service.init();

        verify(amazonS3, never()).createBucket(anyString());
        verify(amazonS3, never()).setBucketPolicy(any(SetBucketPolicyRequest.class));
    }

    @Test
    void testGetAmazonS3() {
        // 创建一个新的AmazonS3Service实例，不使用@InjectMocks注入
        AmazonS3Service service = new AmazonS3Service(contentTypeProperties, s3Properties);

        try (MockedStatic<AmazonS3ClientBuilder> amazonS3ClientBuilderMockedStatic = Mockito.mockStatic(AmazonS3ClientBuilder.class)) {
            AmazonS3ClientBuilder builderMock = Mockito.mock(AmazonS3ClientBuilder.class);

            amazonS3ClientBuilderMockedStatic.when(AmazonS3ClientBuilder::standard).thenReturn(builderMock);

            when(builderMock.withCredentials(any(AWSStaticCredentialsProvider.class))).thenReturn(builderMock);
            when(builderMock.withPathStyleAccessEnabled(true)).thenReturn(builderMock);
            when(builderMock.withEndpointConfiguration(any(AwsClientBuilder.EndpointConfiguration.class))).thenReturn(builderMock);
            when(builderMock.build()).thenReturn(amazonS3);

            // 在此测试中需要配置这些属性
            when(s3Properties.getAccessKey()).thenReturn(ACCESS_KEY);
            when(s3Properties.getSecretKey()).thenReturn(SECRET_KEY);
            when(s3Properties.getEndpoint()).thenReturn(ENDPOINT);

            // 使用反射调用私有方法
            AmazonS3 result = (AmazonS3) ReflectionTestUtils.invokeMethod(service, "getAmazonS3");

            assertNotNull(result);
            assertEquals(amazonS3, result);
        }
    }

    @Test
    void testDoesBucketExist() {
        when(amazonS3.doesBucketExistV2(BUCKET_NAME)).thenReturn(true);

        boolean result = amazonS3Service.doesBucketExist(BUCKET_NAME);

        assertTrue(result);
        verify(amazonS3).doesBucketExistV2(BUCKET_NAME);
    }

    @Test
    void testGetUploadConfig() throws Exception {
        String module = "test-module";
        String objectName = "test.jpg";
        String expectedPath = module + "/" + DATE_STR + "/" + objectName;

        try (MockedStatic<DateUtil> dateUtilMockedStatic = Mockito.mockStatic(DateUtil.class)) {
            // 模拟DateUtil.format方法
            dateUtilMockedStatic.when(() -> DateUtil.format(any(LocalDateTime.class), eq(Consts.DATE_FORMAT)))
                                .thenReturn(DATE_STR);

            // 模拟amazonS3.doesObjectExist方法使用anyString()匹配器
            when(amazonS3.doesObjectExist(eq(BUCKET_NAME), anyString())).thenReturn(false);

            // 删除重复的contentTypeProperties.getData()模拟配置
            // 因为已经在setUp方法中全局设置过了

            // 模拟生成签名URL
            URL mockUrl = new URL("http://localhost:9000/test-bucket/" + expectedPath);
            when(amazonS3.generatePresignedUrl(any(GeneratePresignedUrlRequest.class))).thenReturn(mockUrl);

            // 执行测试
            UploadConfigDTO result = amazonS3Service.getUploadConfig(module, objectName);

            // 验证结果
            assertEquals(expectedPath, result.getPath());
            assertEquals(BUCKET_NAME, result.getBucket());
            assertEquals("image/jpeg", result.getContentType());
            assertEquals(mockUrl.toExternalForm(), result.getUrl());

            // 使用anyString()进行更灵活的验证
            verify(amazonS3).doesObjectExist(eq(BUCKET_NAME), anyString());
            verify(amazonS3).generatePresignedUrl(any(GeneratePresignedUrlRequest.class));
        }
    }

    @Test
    void testGetPath() {
        String module = "test-module";
        String fileName = "test.jpg";
        String expectedPath = module + "/" + DATE_STR + "/" + fileName;

        try (MockedStatic<DateUtil> dateUtilMockedStatic = Mockito.mockStatic(DateUtil.class)) {
            // 模拟DateUtil.format方法
            dateUtilMockedStatic.when(() -> DateUtil.format(any(LocalDateTime.class), eq(Consts.DATE_FORMAT)))
                                .thenReturn(DATE_STR);

            // 使用anyString()匹配器
            when(amazonS3.doesObjectExist(eq(BUCKET_NAME), anyString())).thenReturn(false);

            String result = amazonS3Service.getPath(module, fileName);

            assertEquals(expectedPath, result);
            verify(amazonS3).doesObjectExist(eq(BUCKET_NAME), anyString());
        }
    }

    @Test
    void testGetPathObjectExists() {
        String module = "test-module";
        String fileName = "test.jpg";
        String initialPath = module + "/" + DATE_STR + "/" + fileName;
        String uniquePath = module + "/" + DATE_STR + "/123456/" + fileName;

        try (MockedStatic<DateUtil> dateUtilMockedStatic = Mockito.mockStatic(DateUtil.class);
             MockedStatic<com.common.service.util.IdWorker> idWorkerMockedStatic = Mockito.mockStatic(com.common.service.util.IdWorker.class)) {

            dateUtilMockedStatic.when(() -> DateUtil.format(any(LocalDateTime.class), eq(Consts.DATE_FORMAT)))
                               .thenReturn(DATE_STR);
            idWorkerMockedStatic.when(com.common.service.util.IdWorker::getId).thenReturn(123456L);

            // 使用anyString()匹配器
            when(amazonS3.doesObjectExist(eq(BUCKET_NAME), anyString())).thenReturn(true);

            String result = amazonS3Service.getPath(module, fileName);

            assertEquals(uniquePath, result);
            verify(amazonS3).doesObjectExist(eq(BUCKET_NAME), anyString());
        }
    }

    @Test
    void testGetDownloadUrl() {
        String objectName = "test-module/2023-07-14/test.jpg";
        URL mockUrl = mock(URL.class);
        when(mockUrl.toExternalForm()).thenReturn("http://localhost:9000/test-bucket/test-module/2023-07-14/test.jpg");
        when(amazonS3.getUrl(BUCKET_NAME, objectName)).thenReturn(mockUrl);

        String result = amazonS3Service.getDownloadUrl(objectName);

        assertEquals("http://localhost:9000/test-bucket/test-module/2023-07-14/test.jpg", result);
        verify(amazonS3).getUrl(BUCKET_NAME, objectName);
    }

    @Test
    void testGetDownloadUrlEmpty() {
        String result = amazonS3Service.getDownloadUrl("");

        assertEquals("", result);
        verify(amazonS3, never()).getUrl(anyString(), anyString());
    }

    @Test
    void testGetDownloadUrlMap() {
        List<String> paths = Arrays.asList(
            "test-module/2023-07-14/test1.jpg",
            "test-module/2023-07-14/test2.jpg"
        );

        URL mockUrl1 = mock(URL.class);
        URL mockUrl2 = mock(URL.class);
        when(mockUrl1.toExternalForm()).thenReturn("http://localhost:9000/test-bucket/test-module/2023-07-14/test1.jpg");
        when(mockUrl2.toExternalForm()).thenReturn("http://localhost:9000/test-bucket/test-module/2023-07-14/test2.jpg");

        when(amazonS3.getUrl(BUCKET_NAME, paths.get(0))).thenReturn(mockUrl1);
        when(amazonS3.getUrl(BUCKET_NAME, paths.get(1))).thenReturn(mockUrl2);

        Map<String, String> result = amazonS3Service.getDownloadUrlMap(paths);

        assertEquals(2, result.size());
        assertEquals("http://localhost:9000/test-bucket/test-module/2023-07-14/test1.jpg", result.get(paths.get(0)));
        assertEquals("http://localhost:9000/test-bucket/test-module/2023-07-14/test2.jpg", result.get(paths.get(1)));

        verify(amazonS3, times(1)).getUrl(BUCKET_NAME, paths.get(0));
        verify(amazonS3, times(1)).getUrl(BUCKET_NAME, paths.get(1));
    }

    @Test
    void testGetContentType() {
        String fileName = "test.jpg";

        String result = amazonS3Service.getContentType(fileName);

        assertEquals("image/jpeg", result);
    }

    @Test
    void testGetContentTypeNoExtension() {
        String fileName = "testfile";

        String result = amazonS3Service.getContentType(fileName);

        assertEquals("application/octet-stream", result);
    }

    @Test
    void testGetContentTypeUnknownExtension() {
        String fileName = "test.unknown";

        String result = amazonS3Service.getContentType(fileName);

        assertEquals("application/octet-stream", result);
    }

    @Test
    void testUploadFile() {
        String objectName = "test-module/2023-07-14/test.jpg";
        File mockFile = mock(File.class);
        PutObjectResult mockResult = mock(PutObjectResult.class);
        FileInfo mockFileInfo = mock(FileInfo.class);

        when(amazonS3.putObject(BUCKET_NAME, objectName, mockFile)).thenReturn(mockResult);

        try (MockedStatic<FileUtil> fileUtilMockedStatic = Mockito.mockStatic(FileUtil.class)) {
            fileUtilMockedStatic.when(() -> FileUtil.obtainFileInfo(mockResult, BUCKET_NAME, objectName)).thenReturn(mockFileInfo);

            FileInfo result = amazonS3Service.uploadFile(objectName, mockFile);

            assertEquals(mockFileInfo, result);
            verify(amazonS3).putObject(BUCKET_NAME, objectName, mockFile);
        }
    }
}
