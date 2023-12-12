package com.common.service.handler;

import com.google.common.base.Throwables;
import com.example.common.context.CommonException;
import com.common.service.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
@Slf4j
public class DefaultServiceExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(value = CommonException.class)
    public ResponseEntity<Response<Void>> defaultExceptionHandler(CommonException e) {
        log.warn("LoonshotsException: {}", Throwables.getStackTraceAsString(e));
        ResponseStatus ann = AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class);

        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (ann != null) {
            status = ann.code();
        } else {
            if (e.getStatusCode() != 0) {
                try {
                    status = HttpStatus.valueOf(e.getStatusCode());
                } catch (Exception e1) {
                    log.debug("Error in parsing status code {}", e.getStatusCode());
                }
            }
        }

        String errorMsg = e.getMessage();
        int errorCode = e.getErrorCode();
        return ResponseEntity.status(status).body(Response.failure(errorCode, errorMsg));
    }

/*
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<Response<Void>> handleValidationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        StringBuilder message = new StringBuilder();
        constraintViolations.forEach(con -> {
            ConstraintViolationImpl<?> cv = (ConstraintViolationImpl<?>) con;
            message.append(cv.getMessage()).append(",");
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.failure(400, message.substring(0, message.length() - 1)));
    }
*/


}
