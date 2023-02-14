package com.example.demo.controller;


import com.example.demo.common.Response;
import com.example.demo.config.MinIOTemplate;
import com.example.demo.dto.FileDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author chenx
 * @since 2019-11-21
 */
@RestController
@RequestMapping("/minio")
@Slf4j
@Api(tags = "文件管理接口")
@RequiredArgsConstructor
public class MinioController {

    private final MinIOTemplate minIOTemplate;

    @ApiOperation(value = "上传文件")
    @PostMapping(value = "/anon/uploadFile")
    public Response<FileDTO> save(@RequestParam("file") MultipartFile file) {
        try {
            return Response.success(minIOTemplate.uploadFile("", file.getOriginalFilename(), file.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
