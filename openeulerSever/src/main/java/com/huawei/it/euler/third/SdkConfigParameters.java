/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.third;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 配置参数类
 *
 * @since 2024/07/04
 */
@Slf4j
public class SdkConfigParameters {
    private static final Logger logger = LoggerFactory.getLogger(SdkConfigParameters.class);

    private static final ResourceBundle SDK_RESOURCE_BUNDLE = load(ConfigConstants.SSO_TOKEN_KEY);

    public static String getSdkParameterByName(String paraName) {
        return SDK_RESOURCE_BUNDLE.getString(paraName);
    }

    public static ResourceBundle load(String fileName) {
        ResourceBundle resourceBundle = null;
        try {
            resourceBundle = ResourceBundle.getBundle(fileName);
        } catch (Exception exception) {
            logger.error(String.format(Locale.ENGLISH, "load file %s failed", fileName), exception);
        }
        return resourceBundle;
    }
}
