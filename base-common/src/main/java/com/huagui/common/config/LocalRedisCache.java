package com.huagui.common.config;

import com.google.common.cache.*;
import com.huagui.common.base.util.JsonObjectConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class LocalRedisCache<K, V, M> {

    private static final String INVALID_PREFIX = "INVALID_";

    @Autowired
    LettuceConnectionFactory redisConnectionFactory;

    private LoadingCache<K, V> localCache;

    private Class<K> keyType;
    private Class<V> valueType;
    private Class<M> msgType;

    public LocalRedisCache() {
        keyType = (Class<K>)
                ((ParameterizedType) getClass().getGenericSuperclass())
                        .getActualTypeArguments()[0];
        valueType = (Class<V>)
                ((ParameterizedType) getClass().getGenericSuperclass())
                        .getActualTypeArguments()[1];
        msgType = (Class<M>)
                ((ParameterizedType) getClass().getGenericSuperclass())
                        .getActualTypeArguments()[2];
    }

    public V getValue(K k) {
        try {
            return localCache.get(k);
        } catch (ExecutionException e) {
            log.error("error in fetching local cache", e);
            return null;
        } catch (CacheLoader.InvalidCacheLoadException e1) {
            log.debug("no cache found for {}", k);
            return null;
        }
    }

    public void put(K k, V v) {
        localCache.put(k, v);
    }

    public boolean existsKey(K k) {
        return getValue(k) != null;
    }

    public LoadingCache<K, V> unwrap() {
        return localCache;
    }

    protected int initialCapacity() {
        return 1000;
    }

    protected int maxSize() {
        return 20000;
    }

    // this setting should be less than the redis setting
    protected int expireAfterMinutes() {
        return 60 * 12; // 12 hours
    }

    @PostConstruct
    public void init() {
        ReactiveRedisMessageListenerContainer container = new ReactiveRedisMessageListenerContainer(redisConnectionFactory);
        ChannelMessageHandler messageHandler = new ChannelMessageHandler();

        this.localCache = CacheBuilder.newBuilder()
                .initialCapacity(initialCapacity())
                .maximumSize(maxSize())
                .expireAfterAccess(expireAfterMinutes(), TimeUnit.MINUTES)
                .removalListener((RemovalListener<K, V>) removalNotification -> {
                    K key = removalNotification.getKey();
                    V value = removalNotification.getValue();
                    RemovalCause cause = removalNotification.getCause();
                    log.info("key: {}, value: {}, cause: {}", key, value, cause);
                })
                .build(newCacheLoader());

        for (String channel : getChannels()) {
            container.receive(ChannelTopic.of(channel), ChannelTopic.of(INVALID_PREFIX + channel))
                    .doOnNext(message -> messageHandler.onMessage(message))
                    .subscribe();
        }
    }

    // User can override this method to implement their own cache loader
    protected CacheLoader<K, V> newCacheLoader() {
        return new CacheLoader<K, V>() {
            @Override
            public V load(K s) {
                // Default is empty
                return null;
            }
        };
    }

    protected abstract K buildKey(M message);

    protected abstract V buildValue(M message);

    protected abstract String[] getChannels();

    private class ChannelMessageHandler {
        private String quote = "\"";

        public void onMessage(ReactiveSubscription.Message<String, String> message) {
            String messageStr = message.getMessage();
            if (StringUtils.isEmpty(messageStr)) { //Normally won't happen
                log.info("Received an empty message");
                return;
            }
            String channelName = message.getChannel();
            log.debug("msgBody={}, channel={}", messageStr, channelName);
            // for Primitives and String, we need to enclose the original with quotes
            if (msgType == String.class || msgType.isPrimitive()) {
                messageStr = quote + messageStr + quote;
            }

            M msgObject;
            try {
                msgObject = JsonObjectConverter.jsonToObject(messageStr, msgType);
            } catch (IOException e) {
                log.warn("unable to do the json conversion-{}", messageStr, e);
                return;
            }

            //Handle invalidation
            if (channelName.startsWith(INVALID_PREFIX)) {
                localCache.invalidate(buildKey(msgObject));
            } else {
                localCache.put(buildKey(msgObject), buildValue(msgObject));
            }
        }
    }
}
