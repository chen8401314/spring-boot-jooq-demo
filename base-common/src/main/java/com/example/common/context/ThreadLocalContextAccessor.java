package com.example.common.context;

import lombok.SneakyThrows;

/**
* @Title:
* @Description:
* @Author: chenx
* @Date: 2023/7/11
*/
public final class ThreadLocalContextAccessor {

    private static final ThreadLocal<ServiceContext> contextHolder = new ThreadLocal<>();
    private static final ThreadLocal<ServiceContext> inheritableContextHolder = new InheritableThreadLocal<>();

    private ThreadLocalContextAccessor() {
    }

    public static ServiceContext getServiceContext() {
        ServiceContext context = contextHolder.get();
        if (context == null) {
            context = inheritableContextHolder.get();
        }
        return context;
    }

    @SneakyThrows
    public static String getUserId() {
        return getServiceContext().getUserId();
    }

    public static String getLoginName() {
        return getServiceContext().getLoginName();
    }

    public static void resetServiceContext() {
        contextHolder.remove();
        inheritableContextHolder.remove();
    }

    public static void setServiceContext(ServiceContext serviceContext, boolean inheritable) {
        if (serviceContext == null) {
            resetServiceContext();
        } else {
            if (inheritable) {
                inheritableContextHolder.set(serviceContext);
                contextHolder.remove();
            } else {
                contextHolder.set(serviceContext);
                inheritableContextHolder.remove();
            }
        }
    }

    public static void setServiceContext(ServiceContext serviceContext) {
        setServiceContext(serviceContext, true);
    }
}
