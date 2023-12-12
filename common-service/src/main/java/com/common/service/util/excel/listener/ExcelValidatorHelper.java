package com.common.service.util.excel.listener;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * excel导入字段校验处理
 *
 * @author linjuliang
 */
public class ExcelValidatorHelper {

    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private ExcelValidatorHelper() {
        throw new IllegalStateException("Utility class");
    }


    /**
     * 获取检验信息
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String validate(T obj) {
        StringBuilder result = new StringBuilder();
        Set<ConstraintViolation<T>> set = validator.validate(obj, Default.class);
        if (set != null && !set.isEmpty()) {
            for (ConstraintViolation<T> cv : set) {
                if (result.length() > 0) {
                    result.append(";");
                }
                result.append(cv.getMessage());
            }
        }
        return result.toString();
    }


    /**
     * 返回字段与对应错误信息
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> Map<String, String> validateToMap(T obj) {
        Map<String, String> map = new HashMap<>();
        Set<ConstraintViolation<T>> set = validator.validate(obj, Default.class);
        if (set != null && !set.isEmpty()) {
            for (ConstraintViolation<T> cv : set) {
                map.put(cv.getPropertyPath().toString(), cv.getMessage());
            }
        }
        return map;
    }

}
