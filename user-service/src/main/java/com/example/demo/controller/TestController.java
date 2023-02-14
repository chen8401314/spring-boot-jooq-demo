package com.example.demo.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Response;
import com.example.demo.dto.TestDTO;
import com.example.demo.entity.TestEntity;
import com.example.demo.request.TestReq;
import com.example.demo.service.TestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static com.example.demo.convert.TestConvert.TEST_CONVERT;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author chenx
 * @since 2019-11-21
 */
@RestController
@RequestMapping("/test")
@Slf4j
@Api(tags = "测试管理接口")
@RequiredArgsConstructor
public class TestController {

    private  final TestService testService;

    @ApiOperation(value = "保存或编辑test")
    @PostMapping(value = "/save")
    public Response<String> save(@RequestBody TestReq req) {
        TestEntity testEntity = TEST_CONVERT.toEntity(req);
        testService.saveOrUpdate(testEntity);
        return Response.success();
    }

    @ApiOperation(value = "获取test")
    @GetMapping(value = "/findAll")
    public Response<List<TestEntity>> findAll() {
        return Response.success(testService.list());
    }

    @ApiOperation(value = "获取id删除")
    @DeleteMapping(value = "/delById")
    public Response<Void> delById(@RequestParam String id) {
        testService.removeById(id);
        return Response.success();
    }

    @ApiOperation(value = "获取test通过ID")
    @GetMapping(value = "/findById")
    public Response<TestDTO> selectPage(@RequestParam String id) {
        return Response.success(testService.findById(id));
    }

    @ApiOperation(value = "获取pagetest")
    @GetMapping(value = "/selectPage")
    public Response<Page<TestDTO>> selectPage(
            @RequestParam(required = false) Integer pages,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String name
    ) {
        Page<TestEntity> page = new Page(pages, size);
        return Response.success(testService.selectPage(name, page));
    }

    @ApiOperation(value = "获取pagetest")
    @GetMapping(value = "/selectPage1")
    public Response<IPage<TestDTO>> selectPage1(
            @RequestParam(required = false) Integer pages,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String name
    ) {
        Page<TestDTO> page = new Page(pages, size);
        return Response.success(testService.selectPage1(name, page));
    }

    @ApiOperation(value = "获取pagetest")
    @GetMapping(value = "/findPage")
    public Response<IPage<TestEntity>> findPage(
            @RequestParam(required = false) Integer pages,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String name
    ) {
        return Response.success(testService.findPage(name, new Page(pages, size)));
    }


    @ApiOperation(value = "获取pagetestDTO")
    @GetMapping(value = "/findPageDTO")
    public Response<IPage<TestDTO>> findPageDTO(
            @RequestParam(required = false) Integer pages,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String name
    ) {
        return Response.success(testService.findPageDTO(name, new Page(pages, size)));
    }
}
