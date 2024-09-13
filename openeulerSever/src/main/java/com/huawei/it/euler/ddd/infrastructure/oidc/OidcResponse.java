package com.huawei.it.euler.ddd.infrastructure.oidc;

import lombok.Data;

import java.util.List;

@Data
public class OidcResponse {
    private int code;
    private String msg;
    private String data;
    private List<String> cookieList;
}
