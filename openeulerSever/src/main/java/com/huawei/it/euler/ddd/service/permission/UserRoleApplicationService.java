/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.permission;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.ddd.domain.permission.UserRole;
import com.huawei.it.euler.ddd.service.permission.cqe.UserRoleCommand;
import com.huawei.it.euler.ddd.service.permission.cqe.UserRoleQuery;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户角色application接口
 *
 * @author zhaoyan
 * @since 2024-11-12
 */
public interface UserRoleApplicationService {
    public UserRole authorize(HttpServletRequest request, UserRoleCommand command, String uuid);

    public UserRole reauthorize(HttpServletRequest request, UserRoleCommand command, String uuid);

    public void undoAuthorize(HttpServletRequest request, Integer id, String uuid);

    public Page<UserRole> page(UserRoleQuery query);

    public UserRole findById(Integer id);
}
