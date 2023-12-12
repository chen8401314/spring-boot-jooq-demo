package com.common.service.util.tree;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ChildrenKey {

    /** 字段数据类型默认为 List */
    DataType dataType() default DataType.LIST;
}
