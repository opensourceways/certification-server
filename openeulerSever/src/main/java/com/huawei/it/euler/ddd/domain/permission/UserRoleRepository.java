/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.permission;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.ddd.service.permission.cqe.UserRoleQuery;

import java.util.List;

/**
 * 用户角色 持久化接口
 *
 * @author zhaoyan
 * @since 2024-11-07
 */
public interface UserRoleRepository {
    public UserRole add(UserRole userRole);

    public UserRole update(UserRole userRole);

    public void delete(Integer id);

    public UserRole findById(Integer id);

    public List<UserRole> findList(UserRoleQuery query);

    public Page<UserRole> page(UserRoleQuery query);
}
