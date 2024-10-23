/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import com.huawei.it.euler.config.security.LockCacheConfig;
import com.huawei.it.euler.ddd.domain.account.Role;
import com.huawei.it.euler.ddd.service.populater.UserRoleVOPopulater;
import com.huawei.it.euler.exception.ParamException;
import com.huawei.it.euler.model.enumeration.RoleEnum;
import com.huawei.it.euler.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.ddd.domain.account.UserRole;
import com.huawei.it.euler.ddd.domain.account.UserRoleVO;
import com.huawei.it.euler.ddd.domain.account.converter.UserRoleToVOConverter;
import com.huawei.it.euler.ddd.domain.account.query.UserRoleQuery;
import com.huawei.it.euler.ddd.domain.account.repository.UserRoleRepository;
import com.huawei.it.euler.model.vo.PageResult;

@Service
public class UserRoleService {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRoleVOPopulater userRoleVOPopulater;

    @Autowired
    private LockCacheConfig lockCacheConfig;

    @Autowired
    private UserService userService;

    public PageResult<UserRoleVO> getAccountRoleListByQuery(String uuid, UserRoleQuery query, int curPage, int pageSize) {
        query.setUuid(uuid);
        Page<UserRole> page = userRoleRepository.getUserRolePage(query, curPage, pageSize);
        PageResult<UserRoleVO> userRoleVOPage = UserRoleToVOConverter.INSTANCE.convert(page);
        userRoleVOPopulater.populate(userRoleVOPage.getList());
        return UserRoleToVOConverter.INSTANCE.convert(page);
    }

    public String createUserRole(String uuid,UserRole userRole) {
        checkParams(uuid,userRole);
        String lockKey = userRole.getUuid() + "-" + userRole.getRoleId();
        lockCacheConfig.acquireLock(lockKey);
        String id = userRoleRepository.createUserRole(userRole);
        lockCacheConfig.releaseLock(lockKey);
        return id;
    }

    public String modifyUserRole(String uuid,UserRole userRole) {
        checkParams(uuid,userRole);
        String lockKey = String.valueOf(userRole.getId());
        lockCacheConfig.acquireLock(lockKey);
        String id = userRoleRepository.modifyUserRole(userRole);
        lockCacheConfig.releaseLock(lockKey);
        return id;
    }

    public void deleteUserRole(String uuid,Integer userRoleId) {
        userRoleRepository.deleteUserRole(userRoleId);
    }

    private void checkParams(String uuid,UserRole userRole) {
        if(!RoleEnum.isManageRole(userRole.getRoleId())) {
            throw new ParamException("非法的角色");
        }
        if (!userService.hasDateScopePermission(uuid, RoleEnum.ADMIN.getRoleId(), userRole.getDataScope())) {
            throw new ParamException("非法的数据范围");
        }
        userRole.setLastUpdatedBy(Integer.valueOf(uuid));
    }
}