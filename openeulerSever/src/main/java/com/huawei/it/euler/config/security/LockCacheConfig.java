package com.huawei.it.euler.config.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class LockCacheConfig {
    private Cache<String, Lock> lockCache;

    public LockCacheConfig() {
        this.lockCache = Caffeine.newBuilder()
                .expireAfterAccess(5, TimeUnit.SECONDS)
                .build();
    }

    public Lock getLock(String key) {
        return lockCache.get(key, k -> new ReentrantLock());
    }

    public void releaseLock(String key) {
        Lock lock = lockCache.getIfPresent(key);
        if (lock != null) {
            lock.unlock();
            lockCache.invalidate(key);
        }
    }

    public void acquireLock(String key) {
        Lock lock = getLock(key);
        lock.lock();
    }
}
