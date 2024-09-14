/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.account;

import lombok.Data;

/**
 * 角色信息
 *
 * @since 2024/09/14
 */
@Data
public class Role {
    /**
     * 角色id
     */
    private int id;

    /**
     * 角色值
     */
    private String role;

    /**
     * 角色名称
     */
    private String roleName;

}
