/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */
package com.huawei.it.euler.ddd.infrastructure.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huawei.it.euler.ddd.domain.account.UserRole;
import com.huawei.it.euler.ddd.domain.account.query.UserRoleQuery;
import com.huawei.it.euler.ddd.domain.account.repository.UserRoleRepository;
import com.huawei.it.euler.ddd.infrastructure.mapper.UserRoleMapper;

import java.util.Date;

@Service
public class UserRoleDBRepository extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleRepository {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public Page<UserRole> getUserRolePage(UserRoleQuery query, int curPage, int pageSize) {
        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        Page<UserRole> poPage = new Page<>(curPage, pageSize);
        return userRoleMapper.selectPage(poPage, queryWrapper);
    }

    @Override
    public String createUserRole(UserRole userRole) {
        userRole.setLastUpdatedTime(new Date());
        userRoleMapper.insert(userRole);
        return String.valueOf(userRole.getId());
    }

    @Override
    public String modifyUserRole(UserRole userRole) {
        userRole.setLastUpdatedTime(new Date());
        userRoleMapper.updateById(userRole);
        return String.valueOf(userRole.getId());
    }

    @Override
    public void deleteUserRole(Integer userRoleId) {
        userRoleMapper.deleteById(userRoleId);
    }

    @Override
    public UserRole getUserRoleById(Integer id) {
        return userRoleMapper.selectById(id);
    } 
}