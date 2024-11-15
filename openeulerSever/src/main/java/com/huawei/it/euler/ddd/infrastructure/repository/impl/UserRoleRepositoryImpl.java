/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.ddd.domain.permission.UserRole;
import com.huawei.it.euler.ddd.domain.permission.UserRoleRepository;
import com.huawei.it.euler.ddd.infrastructure.repository.builder.UserRoleBuilder;
import com.huawei.it.euler.ddd.infrastructure.persistence.mapper.UserRoleMapper;
import com.huawei.it.euler.ddd.infrastructure.persistence.po.UserRolePO;
import com.huawei.it.euler.ddd.service.permission.cqe.UserRoleQuery;
import com.huawei.it.euler.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户角色 持久化接口实现类
 *
 * @author zhaoyan
 * @since 2024-11-07
 */
@Repository
public class UserRoleRepositoryImpl implements UserRoleRepository {

    @Autowired
    private UserRoleMapper mapper;

    @Autowired
    private UserRoleBuilder builder;

    @Override
    public UserRole add(UserRole userRole) {
        UserRolePO userRolePO = builder.fromUserRole(userRole);
        mapper.insert(userRolePO);
        return builder.toUserRole(userRolePO);
    }

    public void clearDataScope(Integer id) {
        UpdateWrapper<UserRolePO> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.set("data_scope", null);
        mapper.update(updateWrapper);
    }

    @Override
    public UserRole update(UserRole userRole) {
        UserRolePO userRolePO = builder.fromUserRole(userRole);
        mapper.updateById(userRolePO);
        if (userRolePO.getDataScope() == null){
            clearDataScope(userRolePO.getId());
        }
        return builder.toUserRole(userRolePO);
    }

    @Override
    public void delete(Integer id) {
        UserRolePO userRolePO = mapper.selectById(id);
        if (userRolePO == null) {
            throw new BusinessException("用户权限不存在！");
        }
        mapper.deleteById(id);
    }

    @Override
    public UserRole findById(Integer id) {
        UserRolePO userRolePO = mapper.selectById(id);
        if (userRolePO == null){
            throw new BusinessException("用户权限不存在！");
        }
        return builder.toUserRole(userRolePO);
    }

    private QueryWrapper<UserRolePO> createQueryWrapper(UserRoleQuery query) {
        QueryWrapper<UserRolePO> queryWrapper = new QueryWrapper<>();
        if (query.getId() != null) {
            queryWrapper.eq("id", query.getId());
        }
        if (query.getRoleId() != null) {
            queryWrapper.eq("role_id", query.getRoleId());
        }
        if (query.getRoleIdList() != null && !query.getRoleIdList().isEmpty()) {
            queryWrapper.in("role_id", query.getRoleIdList());
        }
        if (query.getDataScope() != null) {
            queryWrapper.eq("data_scope", query.getDataScope());
        }
        if (!StringUtils.isEmpty(query.getUuid())) {
            queryWrapper.eq("uuid", query.getUuid());
        }
        if (query.getUuidList() != null && !query.getUuidList().isEmpty()) {
            queryWrapper.in("uuid", query.getUuidList());
        }
        return queryWrapper;
    }

    @Override
    public List<UserRole> findList(UserRoleQuery query) {
        QueryWrapper<UserRolePO> queryWrapper = createQueryWrapper(query);
        List<UserRolePO> userRolePOS = mapper.selectList(queryWrapper);
        return builder.toUserRoleList(userRolePOS);
    }

    @Override
    public Page<UserRole> page(UserRoleQuery query) {
        QueryWrapper<UserRolePO> queryWrapper = createQueryWrapper(query);
        Page<UserRolePO> userRolePOPage = new Page<>(query.getCurrent(), query.getSize());
        Page<UserRolePO> selectPage = mapper.selectPage(userRolePOPage, queryWrapper);
        return builder.toUserRolePage(selectPage);
    }
}
