package com.example.demo.request;

import com.common.service.dto.PageReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class QueryTestReq extends PageReq {

    @Schema(description = "名字")
    private String name;
}
