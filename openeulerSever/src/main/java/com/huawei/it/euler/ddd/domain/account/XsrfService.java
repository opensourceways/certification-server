/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.account;

import com.github.benmanes.caffeine.cache.Cache;
import com.huawei.it.euler.util.SessionManagement;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Service
public class XsrfService {
    private static final String RESPONSEHEADERKEY = "XSRF-TOKEN";

    private static final String REQUESTHEADERKEY = "X-XSRF-TOKEN";

    private static final String SAVEPREFIX = "xsrf-token-";

    @Resource
    private Cache<String, Object> persistentCache;

    public String getResponseHeaderKey() {
        return RESPONSEHEADERKEY;
    }

    public String getRequestHeaderKey() {
        return REQUESTHEADERKEY;
    }

    private String createToken() {
        String token;
        try {
            token = SessionManagement.generateTokenToHex();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("XSRF-TOKEN Generation failed.");
        }
        return token;
    }

    public boolean isTokenActive(String uuid, String token) {
        String tokenKey = getSaveKey(uuid);
        Object ifPresent = persistentCache.getIfPresent(tokenKey);
        if (ifPresent == null){
            return false;
        }
        List<String> tokenList = (List<String>) ifPresent;
        if (tokenList.isEmpty()){
            return false;
        }
        return tokenList.contains(token);
    }

    private String getSaveKey(String uuid) {
        return SAVEPREFIX + uuid;
    }

    public String refreshToken(String uuid) {
        String tokenKey = getSaveKey(uuid);
        Object ifPresent = persistentCache.getIfPresent(tokenKey);
        List<String> tokenList;
        if (ifPresent == null){
            tokenList = new ArrayList<>();
        } else {
            tokenList = (List<String>) ifPresent;
        }
        String token = createToken();
        tokenList.add(token);
        if (tokenList.size() > 20){
            tokenList.remove(0);
        }
        persistentCache.put(tokenKey,tokenList);
        return token;
    }
}
