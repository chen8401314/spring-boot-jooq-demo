package com.example.demo.dto.file;

import com.amazonaws.services.s3.Headers;
import com.common.service.util.BaseUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

@Data
public class FileInfo {

    @Schema(description = "文件唯一标识")
    private String uniqueId;

    @Schema(description = "所属存储桶名称")
    private String bucketName;

    @Schema(description = "文件path")
    private String filePath;

    @Schema(description = "文件名称")
    private String fileName;

    @Schema(description = "文件类型")
    private String fileType;

    @Schema(description = "文件大小")
    private Long size;

    @Schema(description = "文件大小(带单位)")
    private String fileSize;

    @Schema(description = "源数据(所有其他(非用户自定义)标头，例如Content-Length，Content-Type，等)")
    private Map<String, Object> metadata = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public Long getSize() {
        if (this.size != null) {
            return this.size;
        }
        Object obj = this.metadata.get(Headers.CONTENT_LENGTH);
        this.size = obj == null ? 0L : (Long) obj;
        return this.size;
    }

    public String getFileSize() {
        return BaseUtil.convertByteSize(BigDecimal.valueOf(getSize()));
    }

}
