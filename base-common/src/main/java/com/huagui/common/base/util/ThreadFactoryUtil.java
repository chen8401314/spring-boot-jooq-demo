package com.huagui.common.base.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ThreadFactory;


/**
 */
public class ThreadFactoryUtil {

    public static ThreadFactory newThreadFactory(String name) {
        return new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat(name + "-%d").build();
    }
}
