package com.huagui.service.handler;

import com.huagui.common.base.context.OperationException;
import com.huagui.service.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(value = OperationException.class)
    @ResponseBody
    public Response exceptionGet(OperationException e) {
        return Response.failure(e.getMessage());
    }

}
