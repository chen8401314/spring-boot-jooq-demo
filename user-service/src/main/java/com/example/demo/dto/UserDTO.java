package com.example.demo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserDTO implements Serializable {


    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "用户名")
    private String username;

}
