/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.interfaces.request;

import lombok.Data;

@Data
public class UserRoleQueryRequest {

    private String queryUuid;

    private Integer roleId;
}
