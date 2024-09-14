/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.account;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class UserInfoFactory {
    public UserInfo createByAccessTokenUser(String accessTokenUser){
        UserInfo userInfo = new UserInfo();
        JSONObject jsonObject = JSONObject.parseObject(accessTokenUser);
        userInfo.setUuid(jsonObject.getString("sub"));
        userInfo.setNickName(jsonObject.getString("nickname"));
        String userName = jsonObject.getString("username");
        userInfo.setUserName(StringUtils.isEmpty(userName) ? userInfo.getNickName() : userName);
        userInfo.setEmail(jsonObject.getString("email"));
        userInfo.setPhone(jsonObject.getString("phone_number"));
        return userInfo;
    }

    public UserInfo createByManagerTokenUser(String managerTokenUser){
        UserInfo userInfo = new UserInfo();
        JSONObject jsonObject = JSONObject.parseObject(managerTokenUser);
        userInfo.setNickName(jsonObject.getString("nickname"));
        String userName = jsonObject.getString("username");
        userInfo.setUserName(StringUtils.isEmpty(userName) ? userInfo.getNickName() : userName);
        userInfo.setEmail(jsonObject.getString("email"));
        userInfo.setPhone(jsonObject.getString("phone"));
        return userInfo;
    }
}
