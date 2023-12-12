package com.example.common.context;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonException extends RuntimeException {

    private static final long serialVersionUID = -6651154910362534400L;

    private final int errorCode;
    private final String errorMessage;

    private final int statusCode;

    public CommonException(int errorCode, String errorMessage) {
        this(errorCode, errorMessage, null);
    }

    public CommonException(int errorCode, String errorMessage, Throwable cause) {
        this(errorCode, errorMessage, cause, 0);
    }

    public CommonException(int errorCode, String errorMessage, int statusCode) {
        this(errorCode, errorMessage, null, statusCode);
    }

    public CommonException(int errorCode, String errorMessage, Throwable cause, int statusCode) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
    }
}
