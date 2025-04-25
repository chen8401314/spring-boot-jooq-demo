package com.example.demo.controller;


import com.common.service.dto.PageDTO;
import com.common.service.dto.Response;
import com.common.service.handler.OperationException;
import com.example.demo.dto.TestDTO;
import com.example.demo.feign.FeignTestRpc;
import com.example.demo.jooq.tables.pojos.TestEntity;
import com.example.demo.request.QueryTestReq;
import com.example.demo.request.TestReq;
import com.example.demo.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
@Tag(name = "测试管理接口")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;
    private final FeignTestRpc feignTestRpc;

    @Operation(summary = "保存或编辑test")
    @PostMapping(value = "/save")
    public Response<Void> save(@RequestBody TestReq req) {
        testService.insert(TEST_MAPPER.toEntity(req));
        return Response.success();
    }

    @Operation(summary = "获取id删除")
    @DeleteMapping(value = "/delById")
    public Response<Void> delById(@RequestParam String id) {
        testService.deleteById(id);
        return Response.success();
    }

    @Operation(summary = "获取test通过ID")
    @GetMapping(value = "/findById")
    public Response<TestDTO> findById(@RequestParam String id) {
        return Response.success(TEST_MAPPER.toDTO(testService.findById(id)));
    }


    @Operation(summary = "testTimeOut")
    @GetMapping(value = "/anon/testTimeOut")
    public Response<Void> testTimeOut(@RequestParam int timeout) throws InterruptedException {
        Thread.sleep(timeout);
        return Response.success();
    }

    @Operation(summary = "testFeign")
    @GetMapping(value = "/anon/testFeign")
    public Response<String> testFeign(@RequestParam int timeout) {
        feignTestRpc.testTimeOut(timeout);
        return Response.success();
    }

    @Operation(summary = "更新")
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

    @Operation(summary = "根据name查询分页")
    @PostMapping(value = "/findPage")
    public Response<PageDTO<TestDTO>> findPageByName(@RequestBody QueryTestReq req) {
        return Response.success(testService.findPageByName(req));
    }



}
