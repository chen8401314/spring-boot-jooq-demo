package com.huagui.service.config;

import com.huagui.common.base.CommonBeanConfig;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.resource.ClientResources;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import java.time.Duration;

/**
 * Created by bradford
 * Usage:
 * Direct RedisTemplate
 *
 * @Bean public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory, GenericJackson2JsonRedisSerializer jsonRedisSerializer) {
 * RedisTemplate<String, Object> template = new RedisTemplate<>();
 * template.setConnectionFactory(redisConnectionFactory);
 * template.setKeySerializer(StringRedisSerializer.UTF_8);
 * template.setValueSerializer(jsonRedisSerializer);
 * template.setHashKeySerializer(StringRedisSerializer.UTF_8);
 * template.setHashValueSerializer(jsonRedisSerializer);
 * template.afterPropertiesSet();
 * return template;
 * }
 * <p>
 * if you want to use some a RedisTemplate for a specific type say UserToken, use below code
 * @Bean public RedisTemplate<String, UserToken> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
 * ExtendedJackson2JsonSerializer jackson2JsonSerializer = new ExtendedJackson2JsonSerializer(UserToken.class);
 * jackson2JsonSerializer.setObjectMapper(JsonObjectConverter.getObjectMapper());
 * RedisTemplate<String, UserToken> template = new RedisTemplate<>();
 * template.setConnectionFactory(redisConnectionFactory);
 * template.setKeySerializer(StringRedisSerializer.UTF_8);
 * template.setValueSerializer(jackson2JsonSerializer);
 * template.setHashKeySerializer(StringRedisSerializer.UTF_8);
 * template.setHashValueSerializer(jackson2JsonSerializer);
 * template.afterPropertiesSet();
 * return template;
 * }
 * if you want use a String-String RedisTemplate, just
 * @Autowired StringRedisTemplate template;
 * <p>
 * Reactive RedisTemplate:
 * Below will gives ReactiveRedisTemplate<String, Object>
 * @Bean public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory, RedisSerializationContext serializationContext) {
 * return new ReactiveRedisTemplate(connectionFactory, serializationContext);
 * }
 * <p>
 * below template will create a RedisTemplate<String, UserToken>
 * @Bean public RedisTemplate<String, UserToken> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
 * ExtendedJackson2JsonSerializer jackson2JsonSerializer = new ExtendedJackson2JsonSerializer(UserToken.class);
 * jackson2JsonSerializer.setObjectMapper(JsonObjectConverter.getObjectMapper());
 * RedisTemplate<String, UserToken> template = new RedisTemplate<>();
 * template.setConnectionFactory(redisConnectionFactory);
 * template.setKeySerializer(StringRedisSerializer.UTF_8);
 * template.setValueSerializer(jackson2JsonSerializer);
 * template.setHashKeySerializer(StringRedisSerializer.UTF_8);
 * template.setHashValueSerializer(jackson2JsonSerializer);
 * template.afterPropertiesSet();
 * return template;
 * }
 * <p>
 * Gives String-String redis Template in reactive mode
 * @Bean public ReactiveStringRedisTemplate reactiveStringRedisTemplate(LettuceConnectionFactory redisConnectionFactory) {
 * return new ReactiveStringRedisTemplate(redisConnectionfactory);
 * }
 */
@Configuration
@Import(CommonBeanConfig.class)
@Slf4j
@EnableCaching
//@EnableRedisRepositories
public class RedisConfig {

    @Value("${spring.redis.host:localhost}") //docker.for.mac.host.internal
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

//    @Value("${spring.redis.database}")
//    private String redisDatabase;
//
//    @Value("${spring.redis.url}")
//    private String redisURL;

    @Value("${spring.redis.ioThreads:4}")
    private int ioThreads;

    @Value("${spring.redis.computationThreads:4}")
    private int computationThreads;

    @Value("${spring.redis.password:}") //docker.for.mac.host.internal
    private String password;

    @Bean
    ClientResources clientResources() {
        return ClientResources.builder().ioThreadPoolSize(ioThreads).computationThreadPoolSize(computationThreads).build();
    }

    @Bean
    LettuceClientConfiguration lettucePoolConfig(ClientResources resources, ClientOptions clientOptions) {
        return LettuceClientConfiguration.builder()
                .clientOptions(clientOptions)
                .clientResources(resources)
                .commandTimeout(Duration.ofMillis(5000))
                .build();
    }

    @Bean
    @Profile({"dev", "debug"})
    public ClientOptions clientOptions() {
        return ClientOptions.create();
    }

    @Bean
    @Profile({"dev", "debug"})
    public LettuceConnectionFactory redisConnectionFactory(LettuceClientConfiguration lettucePoolConfig) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHost, redisPort);
        if (!StringUtils.isEmpty(password)) {
            redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
        }
        return new LettuceConnectionFactory(redisStandaloneConfiguration, lettucePoolConfig);
    }

    @Bean
    @Profile("test")
    ClusterClientOptions clusterClientOptions() {
        // 支持自适应集群拓扑刷新和动态刷新源
        ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                .enableAllAdaptiveRefreshTriggers()
                // 开启自适应刷新
                .enableAdaptiveRefreshTrigger(ClusterTopologyRefreshOptions.RefreshTrigger.MOVED_REDIRECT, ClusterTopologyRefreshOptions.RefreshTrigger.PERSISTENT_RECONNECTS)
                .adaptiveRefreshTriggersTimeout(Duration.ofSeconds(10))
                // 开启定时刷新
                .enablePeriodicRefresh(Duration.ofSeconds(5))
                .build();
        return ClusterClientOptions.builder().autoReconnect(true).maxRedirects(1).topologyRefreshOptions(clusterTopologyRefreshOptions).build();
    }

    @Bean
    @Profile("test")
    ClusterConfigurationProperties clusterProperties() {
        return new ClusterConfigurationProperties();
    }

    @Bean
    @Profile("test")
    public LettuceConnectionFactory redisClusterConnectionFactory(LettuceClientConfiguration lettucePoolConfig) {
        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(clusterProperties().getNodes());
        if (!StringUtils.isEmpty(password)) {
            clusterConfiguration.setPassword(RedisPassword.of(password));
        }
        return new LettuceConnectionFactory(clusterConfiguration, lettucePoolConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(StringRedisSerializer.UTF_8);
        template.setHashKeySerializer(StringRedisSerializer.UTF_8);
        template.afterPropertiesSet();
        return template;
    }


    @Bean
    public RedisCache redisCache() {
        return new RedisCache();
    }


    @Bean
    public ResourceCacheDao resourceCacheDao() {
        return new ResourceCacheDao();
    }

}

