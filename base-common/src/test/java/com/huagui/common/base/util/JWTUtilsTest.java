package com.huagui.common.base.util;

import com.huagui.common.base.context.CommonException;
import com.huagui.common.base.context.UserToken;
import mockit.Tested;
import mockit.integration.junit5.JMockitExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(JMockitExtension.class)
class JWTUtilsTest {


    @Tested
    JWTUtils jwtUtils;

    private String tokenStr;

    @BeforeEach
    void setUp() {
        Map<String, Object> map = new HashMap<>();
        map.put(UserToken.VERSION, 1);
        Map<String, Object> props = new HashMap<>();
        props.put("hobby", "jogging");
        map.put(UserToken.PROPS, props);

        tokenStr = JWTUtils.createToken("b86c88b4-f63d-488f-b529-ce17e61778fg", "bradford", map, ZonedDateTime.now().plusYears(20));
    }

    @AfterEach
    void tearDown() {
    }


    @org.junit.jupiter.api.Test
    void parseToken() {

    }

    @org.junit.jupiter.api.Test
    void validateToken() {
        Boolean valid = JWTUtils.validateToken(tokenStr);
        assertTrue(valid);
        Map<String, Object> map = new HashMap<>();
        String token = JWTUtils.createToken("b86c88b4-f63d-488f-b529-ce17e61778fg", "bradford", map, ZonedDateTime.now().minusDays(20));
        assertThrows(CommonException.class, () -> JWTUtils.validateToken(token));
    }

    @org.junit.jupiter.api.Test
    void extractToken() {
        UserToken user = JWTUtils.extractToken(tokenStr);
        assertEquals("bradford", user.getName());
        assertEquals("jogging", user.getProperties().get("hobby"));

        Map<String, Object> map = new HashMap<>();
        String token = JWTUtils.createToken("", "bradford", map, ZonedDateTime.now().plusDays(20));

        CommonException e = assertThrows(CommonException.class, () -> JWTUtils.extractToken(token));

        "id is empty".equals(e.getMessage());

        String expiredToken = JWTUtils.createToken("b86c88b4-f63d-488f-b529-ce17e61778fg", "bradford", map, ZonedDateTime.now().minusDays(20));
        assertThrows(CommonException.class, () -> JWTUtils.validateToken(expiredToken));
    }

    @Test
    void extractToken1() {
        CommonException ex = assertThrows(CommonException.class, () -> JWTUtils.extractToken(tokenStr + "error"));
    }

}