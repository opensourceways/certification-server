/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.permission;

import lombok.Data;

import java.util.Date;

/**
 * 角色 实体对象
 *
 * @author zhaoyan
 * @since 2024-11-07
 */
@Data
public class Role {
    private Integer id;

    private String role;

    private String roleName;

    private Date lastUpdatedTime;
}
