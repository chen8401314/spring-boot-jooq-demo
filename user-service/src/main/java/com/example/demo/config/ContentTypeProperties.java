package com.example.demo.config;

import com.google.common.collect.Maps;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

/**
* @Title: 上传文件类型contentType配置
* @Description:
* @Author: chenx
* @Date: 2023/7/21
*/
@Data
@Configuration
@PropertySource("classpath:contentType.properties")
@ConfigurationProperties
public class ContentTypeProperties {

    public final Map<String, String> data = Maps.newHashMap();
}
