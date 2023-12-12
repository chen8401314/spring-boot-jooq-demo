package com.common.service.util;

import com.common.service.dto.Response;
import com.common.service.handler.OperationException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.example.common.util.Consts;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.example.common.util.Consts.ELEMENT_JOIN;
import static java.util.stream.Collectors.toList;

public class BaseUtil {

    private final static Base64.Decoder DECODER = Base64.getDecoder();

    private static final int BASE = 1024;

    private static final BigDecimal BASIC_B = BigDecimal.ONE;

    private static final BigDecimal BASIC_KB = new BigDecimal(BASE);

    private static final BigDecimal BASIC_MB = new BigDecimal(BASE * BASE);

    private static final BigDecimal BASIC_GB = new BigDecimal(BASE * BASE * BASE);

    /**
     * base64加密使用
     */
    private final static Base64.Encoder ENCODER = Base64.getEncoder();


    public static int getRandomNum(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }


    public static String base64Encoder(String enPassword) {
        return ENCODER.encodeToString(enPassword.getBytes());
    }

    /**
     * base64解密
     *
     * @param dePassword
     * @return
     */
    public static String base64Decoder(String dePassword) {
        return new String(DECODER.decode(dePassword), StandardCharsets.UTF_8);
    }

    public static boolean admin(String username) {
        return StringUtils.equals(username, Consts.USER_ADMIN);
    }


    /**
     * 将BT为单位的文件大小转换成对应的BT,KB,MB,GB
     *
     * @param byteSize
     * @return
     */
    public static String convertByteSize(BigDecimal byteSize) {
        BigDecimal divisor;
        String unit;
        float value;
        if (byteSize.longValue() < BASE) {
            unit = "B";
            divisor = BASIC_B;
        } else if (byteSize.longValue() < BASE * BASE) {
            divisor = BASIC_KB;
            unit = "KB";
        } else if (byteSize.longValue() < BASE * BASE * BASE) {
            unit = "MB";
            divisor = BASIC_MB;
        } else {
            unit = "GB";
            divisor = BASIC_GB;
        }
        value = byteSize.divide(divisor, 2, RoundingMode.UP).floatValue();
        return value + unit;
    }

    /**
     * 枚举集合转字符串集合
     *
     * @param list
     * @return
     */
    public static <E extends Enum> List<String> convertString(Collection<E> list) {
        return list.stream().map(Enum::name).collect(Collectors.toList());
    }

    /**
     * 统一的返回解析
     *
     * @param response
     * @param <T>
     * @return
     */
    public static <T> T responseResult(Response<T> response, Class cls) {
        if (response.valid() || cls == null) {
            return response.getData();
        }
        if (cls == ArrayList.class) {
            return (T) Lists.newArrayList();
        } else if (cls == HashSet.class) {
            return (T) Sets.newHashSet();
        } else if (cls == HashMap.class) {
            return (T) Maps.newHashMap();
        } else if (cls == Boolean.class) {
            return (T) Boolean.FALSE;
        } else if (cls == Integer.class) {
            return (T) Integer.valueOf(0);
        }
        throw new OperationException("未知的类型!");
    }

    public static LocalDateTime getFirstDayByYear(String year) {
        return LocalDateTime.parse(StringUtils.join(year, "-01-01 00:00:00"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static LocalDateTime getLastDayByYear(String year) {
        return LocalDateTime.parse(StringUtils.join(year, "-12-31 23:59:59"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private BaseUtil() {
        throw new IllegalStateException("Utility class");
    }


    public static List<String> getHandleElementIds(String fileGuid, Collection<String> elementSns) {
        return elementSns.stream()
                .map(elementSn -> StringUtils.join(fileGuid, ELEMENT_JOIN, elementSn)).collect(toList());
    }

    public static String getHandleElementId(String fileGuid, String elementSn) {
        return StringUtils.join(fileGuid, ELEMENT_JOIN, elementSn);
    }
}
