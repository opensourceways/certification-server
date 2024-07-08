/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.third;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * AuthenticationResponse
 *
 * @since 2024/07/04
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String code;

    private String message;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("expires_at")
    private String expiresAt;

    @JsonProperty("expires_on")
    private Long expiresOn;

    @JsonProperty("token_type")
    private TokenType tokenType;

    @JsonProperty("access_token")
    private String enterprise;
}
