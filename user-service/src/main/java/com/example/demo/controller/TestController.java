package com.example.demo.controller;


import com.example.demo.common.Response;
import com.example.demo.dto.TestDTO;
import com.example.demo.jooq.tables.pojos.TestEntity;
import com.example.demo.request.QueryUserReq;
import com.example.demo.request.TestReq;
import com.example.demo.service.TestService;
import com.huagui.service.dto.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.mapper.TestMapper.TEST_MAPPER;

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

    private final TestService testService;

    @ApiOperation(value = "保存或编辑test")
    @PostMapping(value = "/save")
    public Response<String> save(@RequestBody TestReq req) {
        testService.save(TEST_MAPPER.toEntity(req));
        return Response.success();
    }

    @ApiOperation(value = "获取id删除")
    @DeleteMapping(value = "/delById")
    public Response<Void> delById(@RequestParam String id) {
        testService.deleteById(id);
        return Response.success();
    }

    @ApiOperation(value = "获取test通过ID")
    @GetMapping(value = "/findById")
    public Response<TestDTO> findById(@RequestParam String id) {
        return Response.success(TEST_MAPPER.toDTO(testService.findById(id)));
    }

    @ApiOperation(value = "更新")
    @PutMapping(value = "/update")
    public Response<Void> update(@RequestBody TestReq req) {
        TestEntity entity = testService.findById(req.getId());
        if (entity == null) {
            return Response.failure("找不到该数据！");
        }
        TEST_MAPPER.copy(req, entity);
        testService.update(entity);
        return Response.success();
    }

    @ApiOperation(value = "根据name查询分页")
    @PostMapping(value = "/findPage")
    public Response<Page<TestDTO>> findPageByName(@RequestBody QueryUserReq req) {
        return Response.success(testService.findPageByName(req));
    }
}
