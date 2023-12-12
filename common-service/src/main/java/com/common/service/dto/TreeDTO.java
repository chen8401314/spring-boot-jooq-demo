package com.common.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TreeDTO extends BaseDTO {

    @Schema(description = "父级ID")
    private String parentId;

    @Schema(description = "层级编号")
    private String outline;
}
