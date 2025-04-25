package com.example.demo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.common.service.dto.Response;

/**
 * @Title: 模型服务远程调用
 * @Description:
 * @Author: chenx
 * @Date: 2023/8/17
 */
@FeignClient(name = "testFeign", path = "/user",url = "http://localhost:8059")
public interface FeignTestRpc {


    /**
     * 根据fileGuid删除模型
     *
     * @return
     */
    @GetMapping(value = "/test/anon/testTimeOut")
    Response<String> testTimeOut(@RequestParam int timeout);

}
