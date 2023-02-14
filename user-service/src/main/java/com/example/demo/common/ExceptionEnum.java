/**
 * Copyright (C), 2015-2020, 华规软件（上海）有限公司
 * FileName: AbstractErrorEnum
 * Author:   shenqicheng
 * Date:     2020/3/26 16:57
 * Description:
 * History:
 */
package com.example.demo.common;

import lombok.Getter;

/**
 * @author shenqicheng
 * @Date: 2020/3/26 16:57
 * @Description: 公共的code和message对应关系
 */
public enum ExceptionEnum {
    /**
     * 公共枚举
     */
    UNKNOWN(500, "服务器内部错误!"),
    OK(200, "操作成功!"),
    NOT_FOUND(500100, "未查询到此数据!"),
    NOT_LOGIN(500101, "未登录!"),
    /**
     * 用户异常错误码枚举
     */
    USER_NOT_PERMISSION(500200, "无当前权限!"),
    USER_NOT_EXISTS(500201, "用户未登陆或不存在!"),
    USER_EXISTS(500202, "用户已存在!"),
    USER_MOBILE_EXISTS(500203, "手机号已存在!"),
    /**
     * 服务异常错误码枚举
     */
    REQUIRED_PARAMETER(500300, "必传参数错误!"),
    ILLEGAL_REQUEST(500301, "非法的请求参数错误!"),
    PARAMETER_VALIDATION(500302, "字段验证错误!"),
    REQUEST_METHOD_NOT_SUPPORTED(500303, "不支持请求方法!"),
    DATABASE(500304, "字段重复，有外键关联，数据过长等!"),
    FEIGN(500305, "服务调用错误!"),
    WE_CHAT(500306, "微信调用错误!"),
    INVALID_METHOD_PARAMETER(500307, "方法参数无效!"),
    EXIST_DEFAULT_VALUE(500308, "该数据已存在默认值!"),
    DATA_EXIST(500309, "数据已存在!"),
    DATA_NOT_EXIST(500310, "数据不存在,请刷新后重试!"),
    NON_ADMIN_CANNOT_DELETE(500311, "非管理员无法删除记录!"),
    NAME_EXIST(500312, "名称已存在!"),
    SERVICE_NOT_EXIST(500313, "服务不存在!"),
    REDIS_EXCEPTION(500314, "Redis异常");

    @Getter
    private Integer code;
    @Getter
    private String message;

    ExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
