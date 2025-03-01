package com.arius.ecommerce.config;

import com.arius.ecommerce.dto.ProductDTO;
import com.arius.ecommerce.dto.response.BasePagination;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

@Component
public class CustomRedisCacheManager extends RedisCacheManager {

    public CustomRedisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        super(
                RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory),
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
        );
    }

    @Override
    protected Cache decorateCache(Cache cache) {
        if (cache instanceof RedisCache redisCache) {
            return new RedisCache(redisCache.getName(), redisCache.getNativeCache(), redisCache.getCacheConfiguration()) {
                @Override
                public ValueWrapper get(Object key) {
                    ValueWrapper valueWrapper = super.get(key);
                    if (valueWrapper != null && valueWrapper.get() instanceof LinkedHashMap) {
                        ObjectMapper mapper = new ObjectMapper();
                        return new SimpleValueWrapper(mapper.convertValue(valueWrapper.get(),
                                new com.fasterxml.jackson.core.type.TypeReference<BasePagination<ProductDTO>>() {}));
                    }
                    return valueWrapper;
                }
            };
        }
        return cache;
    }
}