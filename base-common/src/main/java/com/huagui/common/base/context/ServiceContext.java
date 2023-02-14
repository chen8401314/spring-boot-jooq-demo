package com.huagui.common.base.context;

import lombok.Getter;

import java.util.Objects;

@Getter
public class ServiceContext {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String AUTHENTICATED_HEADER = "authenticatedUser";
    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String USER_NAME = "X-User-Name";
    public static final String SERVICE_ID = "X-Service-Id";


    private UserToken user;
    private String traceId;
    private String token;

    public ServiceContext(UserToken user) {
        Objects.requireNonNull(user, "the token can't be null");
        this.user = user;
    }

    public ServiceContext(UserToken user, String traceId) {
        this(user);
        this.traceId = traceId;
    }

    public String getUserID() {
        return user.getId();
    }


    public String getLoginName() {
        return user.getName();
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public void setUser(UserToken user) {
        this.user = user;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

