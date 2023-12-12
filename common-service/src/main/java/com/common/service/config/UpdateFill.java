package com.common.service.config;

import org.mapstruct.Mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
@Mapping(target = "id", ignore = true)
@Mapping(target = "updatedDt", expression = "java(java.time.LocalDateTime.now())")
@Mapping(target = "updatedBy", expression = "java(com.example.common.context.ThreadLocalContextAccessor.getUserId())")
public @interface UpdateFill {
}
