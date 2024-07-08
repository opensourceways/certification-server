/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.third;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.Data;

/**
 * AuthenticationRequest
 *
 * @since 2024/07/04
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @JsonProperty("account")
    private String account;

    @JsonProperty("secret")
    private String secret;

    @JsonProperty("project")
    private String project;

    @JsonProperty("enterprise")
    private String enterprise;

    public static AuthenticationRequest create(String account, String secret, String project, String enterprise) {
        return AuthenticationRequest.builder()
                .account(account)
                .secret(secret)
                .project(project)
                .enterprise(enterprise)
                .build();
    }
}
