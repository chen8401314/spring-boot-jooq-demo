package com.example.demo.request;

import com.example.demo.enumeration.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TestReq {

    @Schema(description = "id")
    private String id;
    @Schema(description = "名字")
    private String name;
    @Schema(description = "年龄")
    private Integer age;
    @Schema(description = "头像")
    private String photo;
    @Schema(description = "是否结婚")
    private Boolean marry;
    @Schema(description = "生日")
    private LocalDate birthday;
    @Schema(description = "状态")
    private StatusEnum status;
}
