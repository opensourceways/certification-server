/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.third;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static java.util.Objects.isNull;

/**
 * 获取HEDS的JwtToken相关，用于调用模块第三方接口
 *
 * @since 2024/07/04
 */
@Slf4j
public class JwtTokenClient extends JwtTokenParent {
    public JwtTokenClient(String iamEndpoint, String account, String secret, String enterprise, String project) {
        super(iamEndpoint, account, secret, enterprise, project);
    }

    public String getJwtToken() {
        try {
            String token = null;
            Data<GeneralBody<AuthenticationRequest>> data = generalBodyData();
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> entity = new HttpEntity<>(data, headers);
            ResponseEntity<AuthenticationResponse> responseEntity =
                    restTemplate.postForEntity(iamEndpoint + "/iam/auth/token", entity, AuthenticationResponse.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                token = Objects.requireNonNull(responseEntity.getBody()).getAccessToken();
            }
            if (!isNull(token)) {
                return token;
            }
            throw new RuntimeException("get service account jwt token error: servlet. Status:"
                    + responseEntity.getStatusCodeValue());
        } catch (Exception exception) {
            throw new RuntimeException("get service account jwt token error: servlet. Exception:"
                    + exception.getMessage());
        }
    }

    private Data<GeneralBody<AuthenticationRequest>> generalBodyData() {
        GeneralBody<AuthenticationRequest> generalBody =
                GeneralBody.<AuthenticationRequest>builder()
                        .type("token")
                        .attributes(AuthenticationRequest.create(account, secret, project, enterprise))
                        .build();
        return Data.<GeneralBody<AuthenticationRequest>>builder()
                .data(generalBody)
                .build();
    }
}
