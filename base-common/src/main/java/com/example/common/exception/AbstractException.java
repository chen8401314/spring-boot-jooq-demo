/**
 * @Title:
 * @Description:
 * @Author: chenx
 * @Date: 2023/7/14
 */
package com.example.common.exception;

import lombok.Getter;

import java.io.Serializable;

/**
 * @Title:
 * @Description:
 * @Author: chenx
 * @Date: 2023/7/14
 */
public abstract class AbstractException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1048410464172506448L;

    @Getter
    private MessageEnum expMsg;

    protected AbstractException() {
        super();
    }

    protected AbstractException(MessageEnum msg) {
        super(msg.getMessage());
        expMsg = msg;
    }

    protected AbstractException(String message) {
        super(message);
    }

    protected AbstractException(String message, Throwable cause) {
        super(message, cause);
    }

    protected AbstractException(Throwable cause) {
        super(cause);
    }

}
