package com.example.demo.dto;

import com.example.demo.enumeration.StatusEnum;
import com.huagui.service.dto.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class TestDTO extends BaseDTO {

    @ApiModelProperty(value = "名字")
    private String name;

    @ApiModelProperty(value = "是否是首页")
    private Boolean home;

    @ApiModelProperty(value = "生日")
    private LocalDate birthday;

    @ApiModelProperty(value = "状态")
    private StatusEnum status;
}
