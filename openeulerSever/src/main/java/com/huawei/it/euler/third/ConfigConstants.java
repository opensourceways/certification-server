/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.third;

import lombok.extern.slf4j.Slf4j;

/**
 * 配置类
 *
 * @since 2024/07/04
 */
@Slf4j
public class ConfigConstants {
    public static String IAM_END_POINT = "";

    public static String SDK_VERSION = "";

    public static String SSO_TOKEN_KEY = "sso";

    public static String REGISTER_INFO = "";

    public static String PUBLIC_KEY_URL = "";

    public static void initSDKVersion() {
        SDK_VERSION = SdkConfigParameters.getSdkParameterByName("version");
    }

    public static void initSdk(String publicKeyUrl, String account, String iamEndPoint) {
        IAM_END_POINT = iamEndPoint;
        REGISTER_INFO = account;
        initSDKVersion();
        PUBLIC_KEY_URL = publicKeyUrl;
        PublicKey.getJwtPublicKey();
    }
}
