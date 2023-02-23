package com.example.demo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
* @Title: 登录请求
* @Description:
* @Author: chenx
* @Date: 2020/11/9
*/
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserReq {

    @ApiModelProperty(value = "登录账号")
    @NotEmpty
    private String username;


}
