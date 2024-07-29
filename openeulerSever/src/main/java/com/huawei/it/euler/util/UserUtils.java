/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * UserUtils
 *
 * @since 2024/06/29
 */
public class UserUtils {
    /**
     * 请求获取uuid
     *
     * @param request request
     * @return uuid
     */
    public static String getCookieUuid(HttpServletRequest request) {
       Map<String, String> cookieMap = new HashMap<>();
       Cookie[] cookies = request.getCookies();
       if (cookies != null) {
           for (Cookie cookie : cookies) {
               cookieMap.put(cookie.getName(), cookie.getValue());
           }
           String uuid = null;
           if (!CollectionUtils.isEmpty(cookieMap)) {
               uuid = cookieMap.get("uuid");
           }
           return uuid;
       }

        return null;
    }
}
