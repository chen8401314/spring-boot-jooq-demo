package com.example.demo.context;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.OK)
public class OperationException extends RuntimeException {

    private static final long serialVersionUID = 3510775765637913007L;

    public OperationException() {
    }


    public OperationException(String message) {
        super(message);
    }

    public OperationException(Throwable cause) {
        super(cause);
    }

    /**
     * 带format的异常输出
     *
     * @param format    format
     * @param arguments 参数
     */
    public OperationException(String format, Object... arguments) {
        super(String.format(format, arguments));
    }

}
