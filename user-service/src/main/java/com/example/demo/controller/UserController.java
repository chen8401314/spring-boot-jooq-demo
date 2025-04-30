package com.example.demo.controller;

import com.example.common.context.ThreadLocalContextAccessor;
import com.example.demo.dto.UserDTO;
import com.example.demo.jooq.tables.pojos.UserEntity;
import com.example.demo.request.LoginReq;
import com.example.demo.request.UserReq;
import com.example.demo.service.UserService;
import com.example.demo.util.SecurityUtil;
import com.google.common.base.Throwables;
import com.example.common.util.JWTUtils;
import com.common.service.dto.Response;
import com.common.service.util.HttpReqUtil;
import com.common.service.util.IdWorker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import static com.example.demo.mapper.UserMapper.USER_MAPPER;
import static com.common.service.dto.Response.DEFAULT_CODE_SUCCESS;

/**
 * <p>
 *
 * </p>
 *
 * @author chenx
 * @since 2020-11-09
 */
@RestController
@RequestMapping("/user")
@Slf4j
@Tag(name = "用户管理接口")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "保存user")
    @PostMapping(value = "/save")
    public Response<String> save(@RequestBody UserReq req) {
        UserEntity user = new UserEntity();
        user.setId(IdWorker.getIdStr());
        user.setUsername(req.getUsername());
        user.setPassword(SecurityUtil.getMD5(req.getPassword()));
        userService.insert(user);
        return Response.success();
    }

    @PostMapping(value = "/anon/login")
    @Operation(summary = "用户登陆")
    public Response<String> login(@RequestBody @Validated LoginReq req, HttpServletResponse response) {
        try {
            // 对传入的密码进行解密
            UserEntity userEntity = userService.findByUsername(req.getUsername());
            if (userEntity == null) {
                return Response.failure("该用户不存在！");
            }
            if (!StringUtils.equals(userEntity.getPassword(), SecurityUtil.getMD5(req.getPassword()))) {
                log.info("账号密码错误");
                return Response.failure("账号密码错误");
            }
            String jwt = "";
            //如果是移动端登录token失效时间为一年
            jwt = JWTUtils.createToken(userEntity.getId(), userEntity.getUsername());
            HttpReqUtil.setTokenCookies(jwt, response);
            return Response.success(jwt);
        } catch (Exception e) {
            log.info(Throwables.getStackTraceAsString(e));
            return Response.failure(e.getMessage());
        }
    }

    @GetMapping(value = "/logout")
    @Operation(summary = "用户登出")
    public Response<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        // 暂时隐藏用户退出删除缓存操作
        HttpReqUtil.setTokenCookies("", response);
        return Response.success(DEFAULT_CODE_SUCCESS, "登出成功");
    }

    @GetMapping(value = "/getUserInfo")
    @Operation(summary = "获取登录用户信息")
    public Response<UserDTO> getUserInfo() {
        return Response.success(USER_MAPPER.toDTO(userService.findById(ThreadLocalContextAccessor.getUserId())));
    }

}
