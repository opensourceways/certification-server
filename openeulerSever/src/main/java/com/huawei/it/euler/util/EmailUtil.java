/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 邮件工具类
 *
 * @author zhaoyan
 * @since 2024-12-19
 */
public class EmailUtil {

    private final static String NULL_DISPLAY = "--";
    private final static String PLACEHOLDER_LEFT = "${";
    private final static String PLACEHOLDER_RIGHT = "}";

    public static String fillDataIntoTemplate(String templatePath, Map<String, String> dataMap) {
        try {
            ClassPathResource resource = new ClassPathResource(templatePath);
            String contentAsString = resource.getContentAsString(StandardCharsets.UTF_8);
            for (Map.Entry<String, String> stringStringEntry : dataMap.entrySet()) {
                String value = stringStringEntry.getValue();
                value = StringUtils.isEmpty(value) ? NULL_DISPLAY : value;
                contentAsString = contentAsString.replace(PLACEHOLDER_LEFT + stringStringEntry.getKey() + PLACEHOLDER_RIGHT, value);
            }
            return contentAsString;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}