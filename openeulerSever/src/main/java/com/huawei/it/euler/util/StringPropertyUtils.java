/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.util;

import org.apache.commons.lang3.StringUtils;

/**
 * StringPropertyUtils
 *
 * @since 2024/07/02
 */
public class StringPropertyUtils {
    /**
     * 手机脱敏
     *
     * @param phone 手机号
     * @return 脱敏手机号
     */
    public static String reducePhoneSensitivity(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return phone;
        }
        return StringUtils.overlay(phone, "****", 3, 7);
    }

    /**
     * 邮箱脱敏
     *
     * @param email 邮箱
     * @return 脱敏邮箱
     */
    public static String reduceEmailSensitivity(String email) {
        if (StringUtils.isEmpty(email) || !email.contains("@")) {
            return email;
        }
        int atIndex = email.lastIndexOf('@');
        return StringUtils.overlay(email, "****", 1, atIndex);
    }
}
