package com.huagui.service.beanfactory;

import com.huagui.common.base.context.ServiceContext;
import com.huagui.common.base.context.ThreadLocalContextAccessor;
import com.huagui.common.base.context.UserToken;
import com.huagui.common.base.util.JWTUtils;
import com.huagui.service.handler.DefaultServiceExceptionHandler;
import com.huagui.service.util.HttpReqUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.huagui.common.base.context.ServiceContext.*;


/**
 * Created by lsk on 2017/4/30.
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
                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/api-docs**", "/swagger-ui.html**", "/doc.html**", "/error**")
                .addPathPatterns("/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui.html**", "/doc.html**")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
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
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                if (request.getServletPath().contains("anon")) {
                    return true;
                }
                ThreadContext.put(SERVICE_ID, serviceId);
                Optional<ServiceContext> scOptional = buildContextFromUserToken(request, response);
                if (scOptional.isPresent()) {
                    setServiceContext(scOptional.get());
                    return true;
                }

                scOptional = buildContextFromEncryptedToken(request);
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

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedHeaders("*").allowedMethods("*")
                .maxAge(3600).allowedOrigins("*").allowCredentials(true)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD");
    }

    private void setServiceContext(ServiceContext sc) {
        ThreadContext.put(TRACE_ID_HEADER, sc.getTraceId());
        ThreadContext.put(USER_NAME, sc.getLoginName());
        ThreadLocalContextAccessor.setServiceContext(sc);
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedOrigin("http://localhost:8080");
        corsConfiguration.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }


}
