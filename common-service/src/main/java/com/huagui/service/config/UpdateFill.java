package com.huagui.service.config;

import org.mapstruct.Mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
@Mapping(target = "id", ignore = true)
@Mapping(target = "updatedDt", expression = "java(java.time.LocalDateTime.now())")
@Mapping(target = "updatedBy", expression = "java(com.huagui.common.base.context.ThreadLocalContextAccessor.getUserID())")
public @interface UpdateFill {
}
