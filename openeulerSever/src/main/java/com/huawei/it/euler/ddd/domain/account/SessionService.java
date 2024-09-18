/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.account;

import com.huawei.it.euler.ddd.infrastructure.cache.CustomizeCacheService;
import com.huawei.it.euler.util.AesGcmEncryptionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private static final String SESSIONPREFIX = "session-";
    private static final String REFRESHPREFIX = "refresh-";

    @Value("${eulerlogin.sessionExpiredTime}")
    private int defaultExpireTime;

    @Autowired
    private AesGcmEncryptionService aesGcmEncryptionService;

    @Autowired
    private CustomizeCacheService customizeCacheService;

    public boolean isAuth(String sessionId) {
        String sessionKey = getSessionKey(sessionId);
        String value = customizeCacheService.get(sessionKey);
        return !StringUtils.isEmpty(value);
    }

    public String create(){
        return new Session().create();
    }

    public boolean isNeedRefresh(String sessionId){
        String refreshKey = getRefreshKey(sessionId);
        String value = customizeCacheService.get(refreshKey);
        if (StringUtils.isEmpty(value)){
            return true;
        }
        long tokenRefreshTime = Long.parseLong(value);
        long currentTimeMillis = System.currentTimeMillis();
        return currentTimeMillis > tokenRefreshTime;
    }

    public void save(String sessionId, String uuid) throws Exception {
        save(sessionId, uuid, defaultExpireTime);
    }

    public void save(String sessionId, String uuid, int tokenExpiresIn) throws Exception {
        String sessionKey = getSessionKey(sessionId);
        String refreshKey = getRefreshKey(sessionId);
        String encryptedUuid = aesGcmEncryptionService.encrypt(uuid);
        long expiredTimeMillis = System.currentTimeMillis() + tokenExpiresIn / 2 * 1000L;
        System.out.println("save session ==》 sessionId=" + sessionId + "; uuid = " + uuid + "; tokenExpiresIn = " + tokenExpiresIn);
        customizeCacheService.put(encryptedUuid, sessionId, tokenExpiresIn);
        customizeCacheService.put(refreshKey, String.valueOf(expiredTimeMillis), tokenExpiresIn);
        customizeCacheService.put(sessionKey, encryptedUuid, tokenExpiresIn);
    }

    public String clear(String sessionId) {
        try {
            String uuid = getUuid(sessionId);
            String encryptedUuid = aesGcmEncryptionService.encrypt(uuid);
            customizeCacheService.remove(encryptedUuid);
            String refreshKey = getRefreshKey(sessionId);
            customizeCacheService.remove(refreshKey);
            String sessionKey = getSessionKey(sessionId);
            customizeCacheService.remove(sessionKey);
            return uuid;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void clearByUuid(String uuid){
        try {
            String sessionId = getSessionId(uuid);
            clear(sessionId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getUuid(String sessionId) throws Exception {
        String sessionKey = getSessionKey(sessionId);
        String encryptedUuid = customizeCacheService.get(sessionKey);
        if (StringUtils.isEmpty(encryptedUuid)){
            System.out.println("get uuid by sessionId failed ==》 sessionId=" + sessionId);
        }
        return aesGcmEncryptionService.decrypt(encryptedUuid);
    }

    public String getSessionId(String uuid) throws Exception {
        String encryptedUuid = aesGcmEncryptionService.encrypt(uuid);
        return customizeCacheService.get(encryptedUuid);
    }

    private String getSessionKey(String sessionId) {
        return SESSIONPREFIX + sessionId;
    }
    private String getRefreshKey(String sessionId) {
        return REFRESHPREFIX + sessionId;
    }
}
