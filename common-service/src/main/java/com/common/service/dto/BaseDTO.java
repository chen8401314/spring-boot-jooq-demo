package com.common.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class BaseDTO implements Serializable {

    @Schema(description = "id")
    private String id;

    @Schema(description = "创建时间")
    private LocalDateTime createdDt;

    @Schema(description = "创建人")
    private String createdBy;

    @Schema(description = "创建人姓名")
    private String creator;

    @Schema(description = "创建时间")
    private LocalDateTime updatedDt;

    @Schema(description = "更新人")
    private String updatedBy;
}
