package com.huagui.common.base.context;

/**
 * Created by lsk on 2017/5/13.
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

    public static String getUserID() {
        ServiceContext context = getServiceContext();
        if (context == null) {
            throw new RuntimeException("no user context bound to current thread");
        }
        return context.getUserID();
    }

    public static String getLoginName() {
        ServiceContext context = getServiceContext();
        if (context == null) {
            throw new RuntimeException("no user context bound to current thread");
        }
        return context.getLoginName();
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
