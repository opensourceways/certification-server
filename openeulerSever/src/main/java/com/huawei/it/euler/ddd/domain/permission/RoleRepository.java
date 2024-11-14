/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.permission;

import java.util.List;

/**
 * 角色持久化接口
 *
 * @author zhaoyan
 * @since 2024-11-07
 */
public interface RoleRepository {

    public List<Role> findList();
}
