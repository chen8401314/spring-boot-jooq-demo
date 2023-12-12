package com.common.service.config;

import org.mapstruct.Mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
@Mapping(target = "id", expression = "java(com.common.service.util.IdWorker.getIdStr())")
@Mapping(target = "createdDt", expression = "java(java.time.LocalDateTime.now())")
@Mapping(target = "createdBy", expression = "java(com.example.common.context.ThreadLocalContextAccessor.getUserId())")
public @interface InsertFill {
}
