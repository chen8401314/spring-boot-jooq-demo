/**
 * Copyright (C), 2015-2020, 华规软件（上海）有限公司
 * FileName: HgException
 * Author:   shenqicheng
 * Date:     2020/3/26 15:49
 * Description:
 * History:
 */
package com.huagui.common.base.context;

/**
 * @author shenqicheng
 * @Date: 2020/3/26 15:49
 * @Description: 公共抽象异常类
 */
public abstract class AbstractException extends RuntimeException {

    private static final long serialVersionUID = 1048410464172506448L;

    public AbstractException() {
        super();
    }

    public AbstractException(String message) {
        super(message);
    }

    public AbstractException(String message, Throwable cause) {
        super(message, cause);
    }

    public AbstractException(Throwable cause) {
        super(cause);
    }


}
