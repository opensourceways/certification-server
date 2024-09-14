/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.oidc;

import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huawei.it.euler.ddd.domain.account.UserInfo;
import com.huawei.it.euler.ddd.domain.account.UserInfoFactory;
import com.huawei.it.euler.ddd.infrastructure.cache.CustomizeCacheService;
import com.huawei.it.euler.exception.NoLoginException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OidcAuthService {

    @Autowired
    private OidcClient oidcClient;

    @Autowired
    private UserInfoFactory userInfoFactory;

    @Autowired
    private CustomizeCacheService customizeCacheService;

    public JSONObject isLogin(OidcCookie oidcCookie) throws NoLoginException {
        OidcResponse refreshResult = oidcClient.refreshSession(oidcCookie);
        if (refreshResult == null){
            throw new NoLoginException();
        }

        if (HttpStatus.HTTP_OK != refreshResult.getCode()){
            throw new NoLoginException();
        }

        JSONObject dataJSONObject = JSON.parseObject(refreshResult.getData());
        if (dataJSONObject == null) {
            throw new NoLoginException();
        }
        dataJSONObject.put("cookieList",refreshResult.getCookieList());
        return dataJSONObject;
    }

    public UserInfo getUserInfoByCode(String code){
        String accessToken = oidcClient.getAccessToken(code);
        OidcResponse userInfoRes = oidcClient.getUserInfoByAccessToken(accessToken);
        return userInfoFactory.createByAccessTokenUser(userInfoRes.getData());
    }

    public UserInfo getUserInfo(String uuid) {
        String managerToken;
        String tokenStr = customizeCacheService.get("managerToken");
        if (StringUtils.isEmpty(tokenStr)) {
            String tokenResponse = oidcClient.getManagerToken();
            log.info("get manager token");
            log.info(tokenResponse);
            JSONObject tokenObj = JSONObject.parseObject(tokenResponse);
            managerToken = tokenObj.getString("token");
            long expireTimeSec = tokenObj.getLong("token_expire");
            customizeCacheService.put("managerToken", managerToken, expireTimeSec);
        } else {
            managerToken = tokenStr;
        }
        OidcResponse userInfoRes = oidcClient.getUserInfoByManagerToken(uuid, managerToken);
        log.info("get manager token by user");
        log.info(JSONObject.toJSONString(userInfoRes));
        if (HttpStatus.HTTP_OK == userInfoRes.getCode()){
            UserInfo byManagerTokenUser = userInfoFactory.createByManagerTokenUser(userInfoRes.getData());
            byManagerTokenUser.setUuid(uuid);
            return byManagerTokenUser;
        }
       return null;
    }

    public String getLoginUrl(){
        return oidcClient.getLoginUrl();
    }

    public String getLogoutUrl(){
        return oidcClient.getLogoutUrl();
    }

    public String redirectToIndex(){
        return oidcClient.getFrontRedirectUrl();
    }

    /**
     * parse the data for api called by euler user center
     * @param jwtStr jwt data
     * @return required data
     */
    public String verifyJwt(String jwtStr){
        return oidcClient.verifyJwt(jwtStr);
    }

}
