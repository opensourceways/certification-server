package com.huawei.it.euler.ddd.infrastructure.cache;

import com.github.benmanes.caffeine.cache.Cache;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class CustomizeCacheService {

    @Resource
    private Cache<String, String> customizeCache;

    public void put(String key, String value, long expireTime) {
        value = value + "-" + expireTime;
        customizeCache.put(key, value);
    }

    public String get(String key) {
        String ifPresent = customizeCache.getIfPresent(key);
        if (StringUtils.isEmpty(ifPresent)) {
            return ifPresent;
        }
        int splitIndex = ifPresent.lastIndexOf("-");
        return ifPresent.substring(0, splitIndex);
    }

    public void remove(String key){
        customizeCache.invalidate(key);
    }

}
