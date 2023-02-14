package com.huagui.common.base.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.huagui.common.base.context.UserToken;
import mockit.integration.junit5.JMockitExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(JMockitExtension.class)
class JsonObjectConverterTest {
    UserToken ut;

    @BeforeEach
    void setUp() {
        ut = UserToken.builder()
                .id("id").name("bradford")
                .version(2)
                .code(1024L)
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void objectToJson() throws Exception {
        String tokenString = JsonObjectConverter.objectToJson(ut);
        UserToken token = JsonObjectConverter.jsonToObject(tokenString, UserToken.class);
        assertEquals(token, ut);
    }

/*    @Test
    void jsonToObject(@Mocked ObjectMapper mapper) throws Exception {
        new Expectations() {
            {
                mapper.readValue(withAny(new byte[]{}), withAny(UserToken.class));
                result = new IOException("error");
            }
        };

        IOException exception = assertThrows(IOException.class, () -> JsonObjectConverter.jsonToObject("", UserToken.class));
        assertEquals("error", exception.getMessage());
    }*/

/*    @Test
    void jsonToObject1(@Mocked ObjectMapper mapper) throws Exception {
        TypeReference reference = new TypeReference<List<String>>() {
        };
        new Expectations() {
            {
                mapper.readValue(withAny(new byte[]{}), withAny(reference));
                result = new IOException("error");
            }
        };

        IOException exception = assertThrows(IOException.class, () -> JsonObjectConverter.jsonToObject("", new TypeReference<List<String>>() {
        }));
        assertEquals("error", exception.getMessage());

    }*/

    @Test
    void jsonToObject2() throws Exception {
        TypeReference<List<String>> reference = new TypeReference<List<String>>() {
        };
        List<String> strList = new ArrayList<>();
        strList.add("a");
        strList.add("b");
        String listStr = JsonObjectConverter.objectToJson(strList);
        List<String> serializedList = JsonObjectConverter.jsonToObject(listStr, reference);
        assertTrue(serializedList.size() == 2);
    }

    @Test
    void jsonToObject3() throws Exception {
        Map<String, Object> userMap = new HashMap<>();
        Map<String, Object> props = new HashMap<>();
        props.put("hobby", "jogging");
        userMap.put("properties", props);
        UserToken deserializedToken = JsonObjectConverter.jsonToObject(userMap, UserToken.class);
        assertEquals("jogging", deserializedToken.getProperties().get("hobby"));
    }

    @Test
    void getObjectMapper() {
        assertNotNull(JsonObjectConverter.getObjectMapper());
    }

/*    @Test
    void testObjectToJson(@Mocked ObjectMapper mapper) throws Exception {
        new Expectations() {
            {
                mapper.writeValueAsString(any);
                result = new ChildJsonException("error");
            }
        };

        ChildJsonException ex = assertThrows(ChildJsonException.class, () -> JsonObjectConverter.objectToJson(ut));
        assertEquals("error", ex.getMessage());
    }*/

    static class ChildJsonException extends JsonProcessingException {
        public ChildJsonException(String msg) {
            super(msg);
        }
    }
}
