/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 缓存
 *
 * @since 2024/07/01
 */
@EnableCaching
@Configuration
public class CacheConfig {
    @Bean
    public Cache<String, Object> caffeineCache() {
        return Caffeine.newBuilder()
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterWrite(30, TimeUnit.MINUTES)
                // 初始的缓存空间大小
                .initialCapacity(100)
                // 缓存的最大条数
                .maximumSize(1000).build();
    }
    @Bean
    public Cache<String, String> customizeCache() {
        return Caffeine.newBuilder()
                .expireAfter(new Expiry<String, String>() {
                    @Override
                    public long expireAfterCreate(@NotNull String key, @NotNull String value, long currentTime) {
                        // Set expiration time for each key
                        int splitIndex = value.lastIndexOf("-");
                        long time = Long.parseLong(value.substring(splitIndex + 1));
                        return TimeUnit.SECONDS.toNanos(time);
                    }

                    @Override
                    public long expireAfterUpdate(@NotNull String key, @NotNull String value, long currentTime, long currentDuration) {
                        return currentDuration; // Keep the same expiration
                    }

                    @Override
                    public long expireAfterRead(@NotNull String key, @NotNull String value, long currentTime, long currentDuration) {
                        return currentDuration; // Keep the same expiration
                    }
                })
                .build();
    }

    @Bean
    public Cache<String, Object> persistentCache() {
        return Caffeine.newBuilder()
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterWrite(Long.MAX_VALUE, TimeUnit.MINUTES)
                // 初始的缓存空间大小
                .initialCapacity(100)
                // 缓存的最大条数
                .maximumSize(1000).build();
    }

}
