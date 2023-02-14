package com.example.demo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class FileDTO {
    @ApiModelProperty(value = "所属存储桶名称")
    private String bucketName;

    @ApiModelProperty(value = "上传时间")
    private ZonedDateTime uploadTime;

    @ApiModelProperty(value = "文件path")
    private String filePath;

    @ApiModelProperty(value = "文件完整path")
    private String fullFilePath;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "文件类型")
    private String fileType;

    @ApiModelProperty(value = "文件大小")
    private Long size;
}
