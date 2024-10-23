/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */
package com.huawei.it.euler.ddd.domain.account.query;

import lombok.Data;

@Data
public class UserRoleQuery {

    private String uuid;

    private Integer roleId;

    private Integer dataScope;

    private String applicant;
}
