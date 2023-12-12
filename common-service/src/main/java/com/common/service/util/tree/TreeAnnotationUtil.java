package com.common.service.util.tree;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class TreeAnnotationUtil {

    private TreeAnnotationUtil() { throw new IllegalStateException("Utility class"); }

    /**
     * 获取注解 annotation 标识的字段值
     * @param t entity
     * @param annotation 注解
     * @return java.lang.Object
     */
    public static <T> Object getFieldValue(T t, Class<? extends Annotation> annotation) throws IllegalAccessException {
        Object fieldValue = null;
        Class<?> clazz = t.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if(field.isAnnotationPresent(annotation)){
                field.setAccessible(true);
                fieldValue = field.get(t);
                break;
            }
        }
        return fieldValue;
    }
}
