package com.huawei.it.euler.ddd.domain.account;

import lombok.Data;

@Data
public class Role {
    private int id;

    private String role;

    private String roleName;

    private Integer dataScope;
}
