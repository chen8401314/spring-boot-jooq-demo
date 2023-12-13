package com.example.demo.controller;


import com.common.service.dto.Response;
import com.example.demo.dto.file.UploadConfigDTO;
import com.example.demo.service.AmazonS3Service;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Title:
 * @Description:
 * @Author: chenx
 * @Date: 2023/6/21
 */
@RestController
@RequestMapping("/file")
@Slf4j
@Tag(name = "文件管理")
@RequiredArgsConstructor
public class FileController {

    private final AmazonS3Service amazonS3Service;


    @ApiOperationSupport(order = 5)
    @Operation(summary = "获取put上传配置（单个）")
    @GetMapping(value = "/getUploadConfig")
    @Parameter(name = "module", description = "模块名称", in = ParameterIn.QUERY, required = true)
    @Parameter(name = "file", description = "文件名称", in = ParameterIn.QUERY, required = true)
    public Response<UploadConfigDTO> getUploadConfig(@RequestParam("module") String module, @RequestParam("file") String file) {
        return Response.success(amazonS3Service.getUploadConfig(module, file));
    }

    @ApiOperationSupport(order = 10)
    @Operation(summary = "获取put上传配置(多个)")
    @PostMapping(value = "/getUploadConfigs")
    @Parameter(name = "module", description = "模块名称", in = ParameterIn.QUERY, required = true)
    public Response<List<UploadConfigDTO>> getUploadConfigs(@RequestParam("module") String module, @RequestBody List<String> files) {
        List<UploadConfigDTO> results = Lists.newArrayList();
        files.forEach(file -> results.add(amazonS3Service.getUploadConfig(module, file)));
        return Response.success(results);
    }

    @ApiOperationSupport(order = 15)
    @Operation(summary = "文件下载接口(单个)")
    @GetMapping(value = "/getDownloadUrl")
    @Parameter(name = "path", description = "地址", in = ParameterIn.QUERY, required = true)
    public Response<String> getOpenUrl(@RequestParam("path") String path) {
        return Response.success(amazonS3Service.getDownloadUrl(path));
    }

    @ApiOperationSupport(order = 20)
    @Operation(summary = "文件下载接口(多个)")
    @PostMapping(value = "/download/anon/getUrls")
    public Response<Map<String, String>> getUrls(@Validated @RequestBody List<String> paths) {
        return Response.success(amazonS3Service.getDownloadUrlMap(paths));
    }

}
