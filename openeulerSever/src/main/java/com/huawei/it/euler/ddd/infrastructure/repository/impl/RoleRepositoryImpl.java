/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huawei.it.euler.ddd.domain.permission.Role;
import com.huawei.it.euler.ddd.domain.permission.RoleRepository;
import com.huawei.it.euler.ddd.infrastructure.repository.builder.RoleBuilder;
import com.huawei.it.euler.ddd.infrastructure.persistence.mapper.RoleMapper;
import com.huawei.it.euler.ddd.infrastructure.persistence.po.RolePO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色接口实现类
 *
 * @author zhaoyan
 * @since 2024-11-07
 */
@Repository
public class RoleRepositoryImpl implements RoleRepository {

    @Autowired
    private RoleMapper mapper;

    @Autowired
    private RoleBuilder builder;

    @Override
    public List<Role> findList() {
        QueryWrapper<RolePO> queryWrapper = new QueryWrapper<>();
        List<RolePO> rolePOS = mapper.selectList(queryWrapper);
        return builder.toRoleList(rolePOS);
    }
}
