package com.example.common.util;


/**
 * @Title: 常量
 * @Description:
 * @Author: chenx
 * @Date: 2023/6/25
 */
public class Consts {

    /**
     * 逗号
     */
    public static final String COMMA = ",";

    /**
     * 步长
     */
    public static final int STEP = 4;

    /**
     * 默认位数
     */
    public static final int DIGIT = 4;


    public static final String POINT = ".";
    public static final String REG_POINT = "\\.";
    public static final String ELEMENT_JOIN = "^";
    /**
     * 进制值
     */
    public static final int BASE_VALUE = 36;

    /**
     * 用户缓存KEY
     */
    public static final String SYSTEM_USER = "SYSTEM_USER:";

    /**
     * 组织缓存KEY
     */
    public static final String SYSTEM_ORG = "SYSTEM_ORG:";
    /**
     * 数据总览缓存KEY
     */
    public static final String HOME_DATA = "HOME_DATA:";
    /**
     * 资产设备总览缓存KEY
     */
    public static final String HOME_EQUIP_DATA = "HOME_EQUIP_DATA:";

    /**
     * 资产设备分析缓存KEY
     */
    public static final String HOME_EQUIP_ANALYSIS = "HOME_EQUIP_ANALYSIS:";

    /**
     * 维修单元分析缓存KEY
     */
    public static final String HOME_COMPONENT_ANALYSIS = "HOME_COMPONENT_ANALYSIS:";
    /**
     * 1天秒数
     */
    public static final int ONE_DAY_SECONDS = 24 * 60 * 60;

    /**
     * 5天秒数
     */
    public static final int FIVE_DAY_SECONDS = 5 * 24 * 60 * 60;

    /**
     * 3小时秒数
     */
    public static final int THREE_HOUR_SECONDS = 3 * 60 * 60;

    /**
     * 4小时秒数
     */
    public static final int FOUR_HOUR_SECONDS = 4 * 60 * 60;

    public static final String USER_ADMIN = "admin";


    public static final String TOKEN_PRE = "Bearer ";


    public static final String UNKNOWN = "unknown";


    public static final String VERTICAL_BAR = "|";


    public static final String SLAVE = "slave";

    public static final String PARENT_NAME_JOIN_FORMAT = "%s|%s";


    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private Consts() {
        throw new IllegalStateException("Utility class");
    }

}
