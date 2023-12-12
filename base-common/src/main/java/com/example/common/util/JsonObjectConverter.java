package com.example.common.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Created by Bradford on 2017/4/23.
 */
@Slf4j
public abstract class JsonObjectConverter {


    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        // 返回时间类型
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        objectMapper.registerModules(javaTimeModule);
    }

    private JsonObjectConverter() {
    }

    public static String objectToJson(Object object) throws JsonProcessingException {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("unable to serialize the object {}", object.toString());
            throw e;
        }
    }

    public static <T> T jsonToObject(String data, Class<T> tClass) throws IOException {
        return jsonToObject(data.getBytes(StandardCharsets.UTF_8), tClass);
    }

    public static <T> T jsonToObject(byte[] dataBytes, Class<T> tClass) throws IOException {
        try {
            return objectMapper.readValue(dataBytes, tClass);
        } catch (IOException e) {
            log.error("unable to deserialize the String {} to {}", new String(dataBytes), tClass);
            throw e;
        }
    }

    public static <T> T jsonToObject(Map<String, Object> data, Class<T> tClass) {
        return objectMapper.convertValue(data, tClass);
    }

    public static <T> T jsonToObject(String data, TypeReference<T> typeReference) throws IOException {
        return jsonToObject(data.getBytes(StandardCharsets.UTF_8), typeReference);
    }

    public static <T> T jsonToObject(byte[] dataBytes, TypeReference<T> typeReference) throws IOException {
        try {
            return objectMapper.readValue(dataBytes, typeReference);
        } catch (IOException e) {
            log.error("unable to deserialize the String {} to {}", new String(dataBytes), typeReference.getType());
            throw e;
        }
    }

    public static JsonNode objectToJsonNode(Object object) {
        return objectMapper.valueToTree(object);
    }

    public static <T> T jsonNodeToObject(TreeNode node, Class<T> tClass) throws JsonProcessingException {
        try {
            return objectMapper.treeToValue(node, tClass);
        } catch (JsonProcessingException e) {
            log.error("unable to convert node {} to {}", node.toString(), tClass);
            throw e;
        }
    }


    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
