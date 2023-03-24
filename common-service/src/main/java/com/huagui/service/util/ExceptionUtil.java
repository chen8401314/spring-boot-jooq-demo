/**
 * Copyright (C), 2015-2020, 华规软件（上海）有限公司
 * FileName: ExceptionUtil
 * Author:   shenqicheng
 * Date:     2020/3/30 14:40
 * Description:
 * History:
 */
package com.huagui.service.util;


import com.huagui.common.base.context.OperationException;
import com.huagui.common.base.exception.BaseMsgInfo;
import com.huagui.common.base.exception.MessageStatusEnum;
import com.huagui.service.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
* @Title:
* @Description:
* @Author: chenx
* @Date: 2023/3/24
*/
@Slf4j
public class ExceptionUtil {

    public static ExceptionBuilder getExp(Exception exp) {
        return new ExceptionBuilder(exp);
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
        private BaseMsgInfo expStatus = MessageStatusEnum.UNKNOWN;

        public ExceptionBuilder(Exception exp) {
            log.error(String.format("异常入口url:%s || 异常详情:", getHttpServletRequest().getRequestURL().toString()), exp);
            this.exp = exp;
        }

        private HttpServletRequest getHttpServletRequest() {
            try {
                return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            } catch (Exception e) {
                throw new OperationException("获取 request 错误!");
            }
        }

        public ExceptionBuilder status(BaseMsgInfo status) {
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
