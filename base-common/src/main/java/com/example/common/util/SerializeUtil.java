package com.example.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Slf4j
public class SerializeUtil {

    /**
     * 字节数组转换为字符串
     */
    public static String bytesToString(byte[] bytes) {
        //转换成base64
        return org.apache.commons.codec.binary.Base64.encodeBase64String(bytes);
    }

    /**
     * 字符串转换为字节数组
     *
     * @param str
     * @return
     */
    public static byte[] stringToByte(String str) {
        //转换成base64
        return org.apache.commons.codec.binary.Base64.decodeBase64(str);
    }

    /**
     * 序列化对象（依赖commons-lang3包）
     *
     * @param obj 序列化对象
     * @return 对象序列化之后的字符串
     */
    public static String serialize(Serializable obj) {
        try {
            if (obj != null) {
                byte[] bytes = SerializationUtils.serialize(obj);
                return bytesToString(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反序列化对象（依赖commons-lang3包）
     *
     * @param str 反序列化字符串
     * @return 反序列化之后的对象
     */
    public static <T extends Serializable> T deserialize(String str) {
        try {
            if (StringUtils.isNotEmpty(str)) {
                return SerializationUtils.deserialize(stringToByte(str));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private SerializeUtil() {
        throw new IllegalStateException("Utility class");
    }
}
