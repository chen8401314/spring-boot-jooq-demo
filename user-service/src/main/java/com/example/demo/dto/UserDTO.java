package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserDTO implements Serializable {


    @Schema(description = "主键ID")
    private String id;

    @Schema(description = "用户名")
    private String username;

}
