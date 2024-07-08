/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.third;

/**
 * 获取HEDS的JwtToken相关，用于调用模块第三方接口
 *
 * @since 2024/07/04
 */
public class JwtTokenParent {
    protected String iamEndpoint;

    protected String account;

    protected String secret;

    protected String enterprise;

    protected String project;

    public JwtTokenParent(String iamEndpoint, String account, String secret, String enterprise, String project) {
        this.iamEndpoint = iamEndpoint;
        this.account = account;
        this.secret = secret;
        this.enterprise = enterprise;
        this.project = project;
        ConfigConstants.initSdk(iamEndpoint + "/iam/auth/jwt/public-key",
                String.join("@", account, project, enterprise, "service_client"), iamEndpoint);
    }
}
