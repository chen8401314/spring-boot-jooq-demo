package com.example.demo.util;

import cn.hutool.core.text.CharSequenceUtil;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.example.common.util.Consts;
import com.example.demo.dto.file.FileInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
@ExtendWith(MockitoExtension.class)
class FileUtilTest {

    @Test
    void testObtainFileInfo() {
        PutObjectResult putObjectResult = Mockito.mock(PutObjectResult.class);
        ObjectMetadata metadata = Mockito.mock(ObjectMetadata.class);
        Map<String, Object> rawMetadata = new HashMap<>();
        rawMetadata.put("Content-Length", 123L);
        Mockito.when(metadata.getRawMetadata()).thenReturn(rawMetadata);
        Mockito.when(putObjectResult.getMetadata()).thenReturn(metadata);

        String bucketName = "test-bucket";
        String objectName = "dir/test.txt";

        try (MockedStatic<CharSequenceUtil> charSequenceUtilMockedStatic = Mockito.mockStatic(CharSequenceUtil.class)) {
            charSequenceUtilMockedStatic.when(() -> CharSequenceUtil.subAfter(objectName, "/", true)).thenReturn("test.txt");
            charSequenceUtilMockedStatic.when(() -> CharSequenceUtil.subBefore("test.txt", Consts.POINT, true)).thenReturn("test");
            charSequenceUtilMockedStatic.when(() -> CharSequenceUtil.subAfter(objectName, Consts.POINT, true)).thenReturn("txt");

            FileInfo fileInfo = FileUtil.obtainFileInfo(putObjectResult, bucketName, objectName);
            assertEquals(bucketName, fileInfo.getBucketName());
            assertEquals(objectName, fileInfo.getFilePath());
            assertEquals("test", fileInfo.getFileName());
            assertEquals("txt", fileInfo.getFileType());
            assertEquals(rawMetadata, fileInfo.getMetadata());
        }
    }

    @Test
    void testDetermineFileName() {
        String objectName = "dir/abc.docx";
        try (MockedStatic<CharSequenceUtil> charSequenceUtilMockedStatic = Mockito.mockStatic(CharSequenceUtil.class)) {
            charSequenceUtilMockedStatic.when(() -> CharSequenceUtil.subAfter(objectName, "/", true)).thenReturn("abc.docx");
            charSequenceUtilMockedStatic.when(() -> CharSequenceUtil.subBefore("abc.docx", Consts.POINT, true)).thenReturn("abc");
            String fileName = FileUtil.determineFileName(objectName);
            assertEquals("abc", fileName);
        }
    }

    @Test
    void testDetermineFileType() {
        String objectName = "dir/abc.docx";
        try (MockedStatic<CharSequenceUtil> charSequenceUtilMockedStatic = Mockito.mockStatic(CharSequenceUtil.class)) {
            charSequenceUtilMockedStatic.when(() -> CharSequenceUtil.subAfter(objectName, Consts.POINT, true)).thenReturn("docx");
            String fileType = FileUtil.determineFileType(objectName);
            assertEquals("docx", fileType);
        }
    }
} 