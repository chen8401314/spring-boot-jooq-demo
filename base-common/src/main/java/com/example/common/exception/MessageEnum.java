/**
 * @Title:
 * @Description:
 * @Author: chenx
 * @Date: 2023/7/14
 */
package com.example.common.exception;

import lombok.Getter;

/**
 * @Title:
 * @Description:
 * @Author: chenx
 * @Date: 2023/7/14
 */
public enum MessageEnum {
    /**
     * 公共枚举
     */
    UNKNOWN(500, "服务器内部错误!"),
    OK(200, "操作成功!"),
    NOT_FOUND(500100, "未查询到此数据!"),
    /**
     * 用户异常错误码枚举
     */
    USER_NOT_PERMISSION(500200, "无当前权限!"),
    USER_NOT_EXISTS(500201, "用户未登陆或不存在!"),
    USER_EXISTS(500202, "用户已存在!"),
    USER_MOBILE_EXISTS(500203, "手机号已存在!"),
    USER_PWD_ERROR(500204, "账号密码错误!"),
    /**
     * 服务异常错误码枚举
     */
    REQUIRED_PARAMETER(500300, "必传参数错误!"),
    ILLEGAL_REQUEST(500301, "非法的请求参数错误!"),
    PARAMETER_VALIDATION(500302, "字段验证错误!"),
    REQUEST_METHOD_NOT_SUPPORTED(500303, "不支持请求方法!"),
    DATABASE(500304, "字段重复，数据过长等!"),
    FEIGN(500305, "服务调用错误!"),
    INVALID_METHOD_PARAMETER(500307, "方法参数无效!"),
    EXIST_DEFAULT_VALUE(500308, "该数据已存在默认值!"),
    DATA_EXIST(500309, "数据已存在!"),
    DATA_NOT_EXIST(500310, "数据不存在,请刷新后重试!"),
    NON_ADMIN_CANNOT_DELETE(500311, "非管理员无法删除记录!"),
    NAME_EXIST(500312, "名称重复!"),
    SERVICE_NOT_EXIST(500313, "服务不存在!"),
    REDIS_EXCEPTION(500314, "Redis异常"),
    CODE_EXIST(500312, "编号重复!"),
    BIM_RAW_ERROR(500401, "BimRaw服务错误!"),
    EXCEL_IMPORT(503001, "excel导入错误!"),


    /**
     * 系统异常错误码枚举
     */
    OPERATION(500000, "服务器异常!");

    @Getter
    private Integer code;
    @Getter
    private String message;

    MessageEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
