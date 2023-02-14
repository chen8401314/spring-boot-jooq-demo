package com.example.demo.context;

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
           return "";
        }
        return context.getUserID();
    }

    public static String getUserName() {
        ServiceContext context = getServiceContext();
        if (context == null) {
            return "";
        }
        return context.getUserName();
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
