/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */
package com.huawei.it.euler.ddd.domain.account.query;

import lombok.Data;

@Data
public class UserRoleQuery {

    private String queryUuid;

    private Integer roleId;

    private String uuid;
}
