/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.sms;

import org.apache.commons.lang3.StringUtils;

/**
 * 短信工具类
 *
 * @author zhaoyan
 * @since 2024-12-20
 */
public class SmsUtil {
    /**
     * 短信变量最大长度：20
     */
    private static final int MAX_PHONE_VAR_LENGTH = 20;
    /**
     * 短信变量字符串格式：[\"123\",\"456\"]
     * 短信变量组装：开始字符串
     */
    private static final String BEGIN_PHONE_PARAMETERS = "[\"";
    /**
     * 短信变量组装：结束字符串
     */
    private static final String END_PHONE_PARAMETERS = "\"]";
    /**
     * 短信变量组装：变量分割
     */
    private static final String SEPARATE_PHONE_PARAMETERS = "\",\"";
    /**
     * 短信变量组装：实际变量超长，将一部分替换为字符串"···"
     */
    private static final String EXTRA_LENGTH_PLACEHOLDER = "···";
    /**
     * 短信变量组装：实际变量超长，从17位开始到最后替换为"···",保证最终长度不超过最大长度
     */
    private static final int EXTRA_LENGTH_START = 16;

    /**
     * 短信变量组装
     * @param array 变量数组
     * @return 组装字符串
     */
    public static String assembleContent(String[] array){
        int length = array.length;
        String[] formatArr = new String[length];
        for (int i = 0; i < length; i++) {
            formatArr[i] = overlay(array[i]);
        }
        return BEGIN_PHONE_PARAMETERS + String.join(SEPARATE_PHONE_PARAMETERS, formatArr) + END_PHONE_PARAMETERS;
    }

    /**
     * 短信变量参数超长截取
     * @param str 短信变量
     * @return 合规变量
     */
    public static String overlay(String str) {
        int length = str.length();
        if (length > MAX_PHONE_VAR_LENGTH) {
            return StringUtils.overlay(str, EXTRA_LENGTH_PLACEHOLDER, EXTRA_LENGTH_START, length - 1);
        }
        return str;
    }
}
