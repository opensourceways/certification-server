package com.huawei.it.euler.ddd.infrastructure.oidc;

import lombok.Data;

@Data
public class OidcResponse {
    private int code;
    private String msg;
    private String data;
}
