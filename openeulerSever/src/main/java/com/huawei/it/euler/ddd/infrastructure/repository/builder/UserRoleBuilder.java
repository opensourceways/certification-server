/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.repository.builder;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.ddd.domain.permission.UserRole;
import com.huawei.it.euler.ddd.infrastructure.persistence.po.UserRolePO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户角色构造器
 *
 * @author zhaoyan
 * @since 2024-11-12
 */
@Component
public class UserRoleBuilder {
    public UserRolePO fromUserRole(UserRole userRole) {
        UserRolePO userRolePO = new UserRolePO();
        BeanUtils.copyProperties(userRole, userRolePO);
        return userRolePO;
    }

    public UserRole toUserRole(UserRolePO userRolePO) {
        UserRole userRole = new UserRole();
        BeanUtils.copyProperties(userRolePO, userRole);
        return userRole;
    }

    public List<UserRole> toUserRoleList(List<UserRolePO> userRolePOList) {
        return userRolePOList.stream().map(this::toUserRole).toList();
    }

    public Page<UserRole> toUserRolePage(Page<UserRolePO> userRolePOPage) {
        Page<UserRole> userRolePage = new Page<>();
        BeanUtils.copyProperties(userRolePOPage, userRolePage);
        userRolePage.setRecords(userRolePOPage.getRecords().stream().map(this::toUserRole).toList());
        return userRolePage;
    }
}
