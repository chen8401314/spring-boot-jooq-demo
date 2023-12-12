package com.common.service.beanfactory;

import com.common.service.handler.DefaultServiceExceptionHandler;
import com.example.common.context.ServiceContext;
import com.example.common.context.ThreadLocalContextAccessor;
import com.example.common.context.UserToken;
import com.example.common.util.JWTUtils;
import com.example.common.util.SerializeUtil;
import com.common.service.util.HttpReqUtil;
import feign.RequestInterceptor;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.example.common.context.ServiceContext.*;


/**
 * @Title:
 * @Description:
 * @Author: chenx
 * @Date: 2023/7/11
 */
@Slf4j
@Configuration
@EnableConfigurationProperties
public class ServiceBeanFactory implements WebMvcConfigurer {

    @Value("${spring.application.name}")
    private String serviceName;
    private String serviceId;

    UserToken anonToken = UserToken.builder().id("anonymous").name("anon").version(1).build();

    @Bean
    String serviceId() {
        String instanceId = UUID.randomUUID().toString().split("-")[1];
        log.info("Assign service {} with id {}", serviceName, instanceId);
        serviceId = serviceName + "-" + instanceId;
        return serviceId;
    }

    @Bean
    @Primary
    DefaultServiceExceptionHandler exceptionHandler() {
        return new DefaultServiceExceptionHandler();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        AntPathMatcher pathMatcher = new AntPathMatcher() {
            @Override
            public boolean match(String pattern, String path) {
                return doMatch(pattern, path, false, null);
            }
        };

        registry.addInterceptor(tokenInterceptorAdapter())
                .pathMatcher(pathMatcher)
                .excludePathPatterns("/webjars/**", "/doc.html**", "/swagger-ui/**", "/v3/api-docs.yaml", "/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**")
                .addPathPatterns("/**");
    }


    Optional<ServiceContext> buildContextFromUserToken(HttpServletRequest request, HttpServletResponse response) {
        String jwtStr = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (StringUtils.equals(cookie.getName(), TOKEN_HEADER)) {
                    jwtStr = cookie.getValue();
                    break;
                }
            }

        }
        if (StringUtils.isEmpty(jwtStr)) {
            jwtStr = request.getHeader(TOKEN_HEADER);
            if (StringUtils.isEmpty(jwtStr)) {
                log.debug("No user token attached to the request");
                return Optional.empty();
            }
        }
        //validate the token
        UserToken user = JWTUtils.extractToken(jwtStr);
        if (LocalDateTime.now().plusHours(4).isAfter(user.getExpireAt())) {
            jwtStr = JWTUtils.createToken(user.getId(), user.getName());
            HttpReqUtil.setTokenCookies(jwtStr, response);
        }

        ServiceContext sc = new ServiceContext(user);

        String traceId = request.getHeader(TRACE_ID_HEADER);
        if (!StringUtils.isEmpty(traceId)) {
            sc.setTraceId(traceId);
        }
        return Optional.of(sc);
    }

    Optional<ServiceContext> buildContextFromEncryptedToken(HttpServletRequest request) {
        UserToken user = JWTUtils.extractToken(request.getHeader(TOKEN_HEADER));
        return Optional.of(new ServiceContext(user, UUID.randomUUID().toString()));
    }

    @Bean
    HandlerInterceptor tokenInterceptorAdapter() {
        return new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                if (!request.getServletPath().contains("anon")) {
                    ThreadContext.put(SERVICE_ID, serviceId);
                    Optional<ServiceContext> scOptional = buildContextFromUserToken(request, response);
                    if (scOptional.isEmpty()) {
                        scOptional = buildContextFromEncryptedToken(request);
                    }
                    if (scOptional.isPresent()) {
                        setServiceContext(scOptional.get());
                    } else {
                        ServiceContext anonContext = new ServiceContext(anonToken);
                        String traceId = request.getHeader(TRACE_ID_HEADER);
                        if (!StringUtils.isEmpty(traceId)) {
                            anonContext.setTraceId(traceId);
                        }
                        setServiceContext(anonContext);
                    }
                }
                return true;
            }

            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
                log.debug("clean thread context");
                ThreadLocalContextAccessor.resetServiceContext();
                ThreadContext.clearAll();
            }
        };

    }

    private void setServiceContext(ServiceContext sc) {
        ThreadContext.put(TRACE_ID_HEADER, sc.getTraceId());
        ThreadContext.put(USER_NAME, sc.getLoginName());
        ThreadLocalContextAccessor.setServiceContext(sc);
    }


    @Bean
    RequestInterceptor requestInterceptor() {
        return template -> {
            if (!template.headers().containsKey(AUTHENTICATED_HEADER)) {
                ServiceContext ctx = ThreadLocalContextAccessor.getServiceContext();
                if (ctx != null) {
                    template.header(AUTHENTICATED_HEADER, SerializeUtil.serialize(ctx.getUser()));
                    template.header(TRACE_ID_HEADER, ctx.getTraceId());
                }
            }
        };
    }

}
