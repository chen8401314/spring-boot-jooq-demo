package com.example.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.common.context.CommonException;
import com.example.common.context.ServiceContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufMono;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component
public class GatewayErrorWebExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    /**
     * 处理给定的异常
     *
     * @param exchange
     * @param ex
     * @return
     */
    @Override
    @SneakyThrows
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        Map<String, Object> errorAttributes = new HashMap<>();
        HttpStatus httpStatus;
        int status;
        String errorMsg;
        if (ex instanceof CommonException) {
            CommonException exception = (CommonException) ex;
            ResponseStatus ann = AnnotationUtils.findAnnotation(exception.getClass(), ResponseStatus.class);

            httpStatus = HttpStatus.BAD_REQUEST;
            if (ann != null) {
                httpStatus = ann.code();
            } else if (exception.getStatusCode() != 0) {
                httpStatus = HttpStatus.valueOf(exception.getStatusCode());
            }
            errorMsg = exception.getMessage();
            status = exception.getErrorCode();
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            status = httpStatus.value();
            errorMsg = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
        }
        List<String> traceIds = exchange.getRequest().getHeaders().get(ServiceContext.TRACE_ID_HEADER);
        if (CollectionUtils.isNotEmpty(traceIds)) {
            errorAttributes.put("traceId", traceIds.get(0));
        }
        errorAttributes.put("httpStatus", httpStatus);
        errorAttributes.put("status", status);
        errorAttributes.put("message", errorMsg);
        errorAttributes.put("timestamp", ZonedDateTime.now(ZoneId.of("UTC")));

        DataBuffer dataBuffer = response.bufferFactory()
                .allocateBuffer(256).write(objectMapper.writeValueAsBytes(errorAttributes));
        response.setStatusCode(httpStatus);
        //基于流形式
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response.writeAndFlushWith(Mono.just(ByteBufMono.just(dataBuffer)));
    }

}
