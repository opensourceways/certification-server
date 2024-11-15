/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.permission.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.ddd.domain.account.UserInfo;
import com.huawei.it.euler.ddd.domain.permission.UserRole;
import com.huawei.it.euler.ddd.domain.permission.UserRoleRepository;
import com.huawei.it.euler.ddd.service.AccountService;
import com.huawei.it.euler.ddd.service.permission.*;
import com.huawei.it.euler.ddd.service.permission.cqe.UserRoleCommand;
import com.huawei.it.euler.ddd.service.permission.cqe.UserRoleQuery;
import com.huawei.it.euler.exception.BusinessException;
import com.huawei.it.euler.mapper.InnovationCenterMapper;
import com.huawei.it.euler.model.entity.InnovationCenter;
import com.huawei.it.euler.util.LogUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户角色application实现类
 *
 * @author zhaoyan
 * @since 2024-11-12
 */
@Slf4j
@Service
public class UserRoleApplicationServiceImpl implements UserRoleApplicationService {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRoleFactory userRoleFactory;

    @Autowired
    private AccountService accountService;

    @Autowired
    private InnovationCenterMapper innovationCenterMapper;

    @Autowired
    private LogUtils logUtils;

    @Override
    public UserRole authorize(HttpServletRequest request, UserRoleCommand command) {
        UserRoleQuery query = userRoleFactory.toQuery(command);
        List<UserRole> queryList = userRoleRepository.findList(query);
        if (queryList != null && !queryList.isEmpty()) {
            throw new BusinessException("用户已配置此权限！");
        }
        UserRole userRole = userRoleFactory.toUserRole(command);
        userRole = userRoleRepository.add(userRole);
        logUtils.insertAuditLog(request, command.getLastUpdatedBy(), "permission", "authorize", userRole.toSimpleJsonString());
        return fillData(userRole);
    }

    @Override
    public UserRole reauthorize(HttpServletRequest request, UserRoleCommand command) {
        UserRole userRole = userRoleRepository.findById(command.getId());

        UserRoleQuery query = userRoleFactory.toQuery(command);
        query.setUuid(userRole.getUuid());
        List<UserRole> queryList = userRoleRepository.findList(query);
        if (queryList != null && !queryList.isEmpty()) {
            throw new BusinessException("用户已配置此权限！");
        }

        UserRole update = userRoleFactory.toUserRole(command);
        userRoleRepository.update(update);
        logUtils.insertAuditLog(request, command.getLastUpdatedBy(), "permission", "reauthorize", userRole.toSimpleJsonString());
        return fillData(update);
    }

    @Override
    public void undoAuthorize(HttpServletRequest request, UserRoleCommand command) {
        UserRole userRole = userRoleRepository.findById(command.getId());
        userRoleRepository.delete(command.getId());
        logUtils.insertAuditLog(request, command.getLastUpdatedBy(), "permission", "undoAuthorize", userRole.toSimpleJsonString());
    }

    @Override
    public Page<UserRole> page(UserRoleQuery query) {
        Page<UserRole> userRolePage = userRoleRepository.page(query);
        userRolePage.setRecords(userRolePage.getRecords().stream().map(this::fillData).toList());
        return userRolePage;
    }

    @Override
    public UserRole findById(Integer id) {
        UserRole byId = userRoleRepository.findById(id);
        return fillData(byId);
    }

    private UserRole fillData(UserRole userRole) {
        UserInfo userInfo = accountService.getUserInfo(userRole.getUuid());
        userRole.setUserName(userInfo.getUserName());

        if (userRole.getDataScope() != null && userRole.getDataScope() > 0) {
            InnovationCenter innovationCenter = innovationCenterMapper.findById(userRole.getDataScope());
            userRole.setScopeName(innovationCenter.getName());
        }
        return userRole;
    }
}
