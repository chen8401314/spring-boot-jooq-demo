/**
 * @Title:
 * @Description:
 * @Author: chenx
 * @Date: 2023/7/14
 */
package com.common.service.util;


import com.common.service.handler.OperationException;
import com.example.common.exception.MessageEnum;
import com.common.service.dto.Response;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * @Title:
 * @Description:
 * @Author: chenx
 * @Date: 2023/3/24
 */
@Slf4j
public class ExceptionUtil {

    public static ExceptionBuilder getExp(Exception exp) {
        return new ExceptionBuilder(exp, true);
    }

    public static ExceptionBuilder getExpNoLog(Exception exp) {
        return new ExceptionBuilder(exp,false);
    }


    private ExceptionUtil() {
        throw new IllegalStateException("Utility class");
    }

    @Slf4j
    public static class ExceptionBuilder {
        //异常
        private Exception exp;
        //响应status
        private HttpStatus status = HttpStatus.OK;
        // 返回类型
        private MediaType contentType = MediaType.APPLICATION_JSON;
        //异常枚举
        private MessageEnum expStatus = MessageEnum.UNKNOWN;

        public ExceptionBuilder(Exception exp, Boolean infoLog) {
            if (Boolean.TRUE.equals(infoLog)) {
                log.error(String.format("异常入口url:%s || 异常详情:", getHttpServletRequest().getRequestURL().toString()), exp);
            }
            this.exp = exp;
        }


        private HttpServletRequest getHttpServletRequest() {
            try {
                return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            } catch (Exception e) {
                throw new OperationException("获取 request 错误!");
            }
        }

        public ExceptionBuilder status(MessageEnum status) {
            this.expStatus = status;
            return this;
        }

        public ExceptionBuilder status(HttpStatus status) {
            this.status = status;
            return this;
        }

        public ExceptionBuilder contentType(MediaType contentType) {
            this.contentType = contentType;
            return this;
        }

        public ResponseEntity<Response<Void>> doResponse() {
            return ResponseEntity.status(status).contentType(contentType).body(Response.failure(expStatus));
        }

        public ResponseEntity<Response<Void>> doResponse(String message) {
            return ResponseEntity.status(status).contentType(contentType).body(Response.failure(expStatus.getCode(), message));
        }


    }
}
