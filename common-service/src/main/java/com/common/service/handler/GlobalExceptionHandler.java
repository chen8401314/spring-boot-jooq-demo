package com.common.service.handler;

import com.common.service.util.ExceptionUtil;
import com.example.common.exception.AbstractException;
import com.example.common.exception.MessageEnum;
import com.common.service.dto.Response;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 必传参数异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    ResponseEntity<Response<Void>> handleException(MissingServletRequestParameterException e) {
        return ExceptionUtil.getExp(e).status(MessageEnum.REQUIRED_PARAMETER).doResponse();
    }

    /**
     * 参数格式不正确
     *
     * @param e
     * @return
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<Response<Void>> handleException(HttpMessageNotReadableException e) {
        return ExceptionUtil.getExp(e).status(MessageEnum.ILLEGAL_REQUEST).doResponse();
    }

    /**
     * 处理所有接口数据验证异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Response<Void>> handleException(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        FieldError fieldError = result.getFieldError();
        String message = String.format("%s:%s", fieldError.getField(), fieldError.getDefaultMessage());
        return ExceptionUtil.getExp(e).status(MessageEnum.INVALID_METHOD_PARAMETER).doResponse(message);
    }

    /**
     * 实体字段验证失败
     *
     * @param e
     * @return
     */
    @ExceptionHandler(ValidationException.class)
    ResponseEntity<Response<Void>> handleException(ValidationException e) {
        return ExceptionUtil.getExp(e).status(MessageEnum.PARAMETER_VALIDATION).doResponse();
    }

    /* *//**
     * 请求参数验证
     *
     * @param e
     * @return
     *//*
    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<Response<Void>> handleException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        StringBuilder message = new StringBuilder();
        constraintViolations.forEach(con -> {
            ConstraintViolationImpl<?> cv = (ConstraintViolationImpl<?>) con;
            message.append(cv.getMessage()).append(",");
        });
        String resultMsg = message.substring(0, message.length() - 1);
        return ExceptionUtil.getExp(e).status(MessageEnum.PARAMETER_VALIDATION).doResponse(resultMsg);
    }*/

    /**
     * 不支持请求方法
     *
     * @param e
     * @return
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<Response<Void>> handleException(HttpRequestMethodNotSupportedException e) {
        return ExceptionUtil.getExp(e).status(MessageEnum.REQUEST_METHOD_NOT_SUPPORTED).doResponse();
    }

    /**
     * 操作数据库出现异常:名称重复，外键关联
     *
     * @param e
     * @return
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<Response<Void>> handleException(DataIntegrityViolationException e) {
        return ExceptionUtil.getExp(e).status(MessageEnum.DATABASE).doResponse();
    }


    /**
     * 处理所有不可知的异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    ResponseEntity<Response<Void>> handleException(Exception e) {
        return ExceptionUtil.getExp(e).doResponse();
    }

    /**
     * 处理所有自定义异常(权限异常, 普通业务异常, 跨服务调用异常等)
     *
     * @param e
     * @return
     */
    @ExceptionHandler(AbstractException.class)
    ResponseEntity<Response<Void>> handleException(AbstractException e) {
        return ExceptionUtil.getExpNoLog(e).status(e.getExpMsg() == null ? MessageEnum.OPERATION : e.getExpMsg()).doResponse(e.getMessage());
    }


    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Response<Void>> illegalArgumentException(IllegalArgumentException e) {
        return ExceptionUtil.getExp(e).doResponse(e.getMessage());
    }


}
