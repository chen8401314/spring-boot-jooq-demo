package com.example.demo.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
* @Title: 上传配置DTO
* @Description:
* @Author: chenx
* @Date: 2023/7/21
*/
@Data
public class UploadConfigDTO {

    @Schema(description = "文件路径")
    private String path;

    @Schema(description = "桶名称")
    private String bucket;

    @Schema(description = "文件类型")
    private String contentType;

    @Schema(description = "上传url")
    private String url;


}
