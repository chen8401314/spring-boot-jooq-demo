package com.example.demo.handler;

import com.example.demo.common.Response;
import com.example.demo.context.CommonException;
import com.google.common.base.Throwables;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;


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

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                               HttpHeaders headers, HttpStatus status, WebRequest req) {
        StringBuilder sb = new StringBuilder();
        if (e.getBindingResult().hasErrors()) {
            //output all the violation
            for (ObjectError objectError : e.getBindingResult().getAllErrors()) {
                String fieldString = objectError.getCodes()[1].replaceAll("^.*\\.", "");
                sb.append(fieldString);
                sb.append(": ");
                sb.append(objectError.getDefaultMessage()).append("; ");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.failure(status.value(), sb.toString()));
    }

    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
                                                               HttpStatus status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.failure(status.value(), ex.getMessage()));
    }

}
