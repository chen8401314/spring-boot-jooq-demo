package com.example.demo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


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
public class LoginReq {

    @Schema(description = "登录账号")
    @NotEmpty
    private String username;

    @Schema(description = "登录密码")
    @NotEmpty
    private String password;

}
