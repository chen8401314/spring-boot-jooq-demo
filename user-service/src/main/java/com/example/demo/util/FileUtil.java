package com.example.demo.util;

import cn.hutool.core.text.CharSequenceUtil;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.example.common.util.Consts;
import com.example.demo.dto.file.FileInfo;

public class FileUtil {

    private FileUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static FileInfo obtainFileInfo(PutObjectResult data, String bucketName, String objectName) {
        FileInfo result = new FileInfo();
        result.setBucketName(bucketName);
        result.setFilePath(objectName);
        result.setFileName(determineFileName(objectName));
        result.setFileType(determineFileType(objectName));
        result.setMetadata(data.getMetadata().getRawMetadata());
        return result;
    }

    public static String determineFileName(String objectName) {
        String fileName = CharSequenceUtil.subAfter(objectName, "/", true);
        return CharSequenceUtil.subBefore(fileName, Consts.POINT, true);
    }

    public static String determineFileType(String objectName) {
        return CharSequenceUtil.subAfter(objectName, Consts.POINT, true);
    }

}
