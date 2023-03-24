/**
 * Copyright (C), 2015-2019, 华规软件（上海）有限公司
 * FileName: BaseMsgInfo
 * Author:   shenqicheng
 * Date:     2020/3/27 15:22
 * Description:
 * History:
 */
package com.huagui.common.base.exception;

/**
 * @author shenqicheng
 * @Date: 2020/3/27 15:22
 * @Description: 基础错误代码类
 */
public interface BaseMsgInfo {

    /**
     * 错误code
     *
     * @return
     */
    Integer getCode();

    /**
     * 错误信息
     *
     * @return
     */
    String getMessage();
}
