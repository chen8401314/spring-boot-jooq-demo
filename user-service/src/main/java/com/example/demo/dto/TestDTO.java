package com.example.demo.dto;

import com.example.demo.enumeration.StatusEnum;
import com.common.service.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class TestDTO extends BaseDTO {

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
