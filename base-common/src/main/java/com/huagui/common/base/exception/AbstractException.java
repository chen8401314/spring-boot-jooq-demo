/**
 * Copyright (C), 2015-2020, 华规软件（上海）有限公司
 * FileName: HgException
 * Author:   shenqicheng
 * Date:     2020/3/26 15:49
 * Description:
 * History:
 */
package com.huagui.common.base.exception;

import lombok.Getter;

/**
 * @author shenqicheng
 * @Date: 2020/3/26 15:49
 * @Description: 公共抽象异常类
 */
public abstract class AbstractException extends RuntimeException {

    private static final long serialVersionUID = 1048410464172506448L;

    @Getter
    private BaseMsgInfo expMsg;

    protected AbstractException() {
        super();
    }

    protected AbstractException(BaseMsgInfo msg) {
        super(msg.getMessage());
        expMsg = msg;
    }

    protected AbstractException(String message) {
        super(message);
    }

    protected AbstractException(String message, Throwable cause) {
        super(message, cause);
    }

    public AbstractException(Throwable cause) {
        super(cause);
    }

    /**
     * 得到具体的错误枚举信息
     *
     * @return
     */
    public abstract BaseMsgInfo resolverExp();

}
