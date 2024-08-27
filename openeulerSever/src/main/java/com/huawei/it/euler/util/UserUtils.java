/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * UserUtils
 *
 * @since 2024/06/29
 */
public class UserUtils {

    private static final Logger log = LoggerFactory.getLogger(UserUtils.class);

    /**
     * 请求获取uuid
     *
     * @param request request
     * @return uuid
     */
    public static String getCookieUuid(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("uuid".equals(cookie.getName())) {
                    log.debug("Found uuid cookie: {}", cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        log.debug("uuid cookie not found");
        return null;
    }
}
