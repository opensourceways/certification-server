/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.repository.builder;

import com.huawei.it.euler.ddd.domain.permission.Role;
import com.huawei.it.euler.ddd.infrastructure.persistence.po.RolePO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 角色构造器
 *
 * @author zhaoyan
 * @since 2024-11-12
 */
@Component
public class RoleBuilder {
    public Role toRole(RolePO rolePO) {
        Role role = new Role();
        BeanUtils.copyProperties(rolePO, role);
        return role;
    }

    public List<Role> toRoleList(List<RolePO> rolePOList){
        return rolePOList.stream().map(this::toRole).toList();
    }
}
