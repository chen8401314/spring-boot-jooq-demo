package com.huagui.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Set;

/**
 * Created by 10302 on 2017/5/24.
 */
@Deprecated
public class ResourceCacheDao {

    @Autowired
    RedisCache redisCache;


    private static final Logger logger = LoggerFactory.getLogger(ResourceCacheDao.class);


    private final String keyPrefix = "ROLE_RESOURCE:";

    public void saveResource(String username, Map<String, Set<String>> resources) {
        if (resources != null && resources.size() == 0) {
            logger.error("resources not has data");
            return;
        }
        redisCache.setObj(getKey(username), resources, 120 * 60 * 24 + 5);
    }

    public Map<String, Set<String>> getResource(String username) {
        if (username == null || username.trim().equals("")) {
            logger.error("username is null");
            return null;
        }
        return (Map<String, Set<String>>) redisCache.getObj(getKey(username));
    }

    public void delete(String username) {
        if (username == null || username == "") {
            logger.error("username is null");
            return;
        }
        redisCache.del(getKey(username));
    }

    public void update(String username, Map<String, Set<String>> resources) {
        saveResource(username, resources);
    }

    private String getKey(String username) {
        return keyPrefix + username;
    }

}
