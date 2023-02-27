package com.huagui.service.config;

import org.mapstruct.Mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
@Mapping(target = "id", expression = "java(com.huagui.service.util.BaseUtil.getUUID())")
@Mapping(target = "createdDt", expression = "java(java.time.LocalDateTime.now())")
@Mapping(target = "createdBy", expression = "java(com.huagui.common.base.context.ThreadLocalContextAccessor.getUserID())")
public @interface InsertFill {
}
