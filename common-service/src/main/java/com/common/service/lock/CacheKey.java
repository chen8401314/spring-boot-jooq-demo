package com.common.service.lock;


/**
 * 缓存键
 *
 * @author cx
 */
public final class CacheKey {

    /**
     * 功能项操作
     */
    public static final String LOCK_MENU = "LOCK_MENU";

    /**
     * 模型操作
     */
    public static final String LOCK_MODEL = "LOCK_MODEL";
    /**
     * 组织操作
     */
    public static final String LOCK_ORG = "LOCK_ORG";

    /**
     * 数据字典操作
     */
    public static final String LOCK_DICTIONARY = "LOCK_DICTIONARY";

    /**
     * 项目结构操作
     */
    public static final String LOCK_PROJECT = "LOCK_PROJECT";
    /**
     * 角色操作
     */
    public static final String LOCK_ROLE = "LOCK_ROLE";


    private CacheKey() {
        throw new IllegalStateException("Utility class");
    }
}
