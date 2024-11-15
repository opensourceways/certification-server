/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.ddd.domain.account.UserInfo;
import com.huawei.it.euler.ddd.domain.permission.UserRole;
import com.huawei.it.euler.ddd.domain.permission.UserRoleRepository;
import com.huawei.it.euler.ddd.service.permission.UserRoleFactory;
import com.huawei.it.euler.ddd.service.permission.cqe.UserRoleCommand;
import com.huawei.it.euler.ddd.service.permission.cqe.UserRoleQuery;
import com.huawei.it.euler.ddd.service.permission.impl.UserRoleApplicationServiceImpl;
import com.huawei.it.euler.exception.BusinessException;
import com.huawei.it.euler.mapper.InnovationCenterMapper;
import com.huawei.it.euler.model.entity.InnovationCenter;
import com.huawei.it.euler.util.LogUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * 用户角色application测试类
 *
 * @author zhaoyan
 * @since 2024-11-14
 */
@ExtendWith(MockitoExtension.class)
public class UserRoleApplicationServiceTest {
    private static final String USER_UUID = "1";

    private static final int BUSI_ID = 1;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private UserRoleRepository userRoleRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private UserRoleFactory userRoleFactory;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AccountService accountService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private InnovationCenterMapper innovationCenterMapper;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private LogUtils logUtils;

    @InjectMocks
    private UserRoleApplicationServiceImpl userRoleApplicationService;

    @Test
    @DisplayName("授权成功")
    public void testAuthorizeSuccess() {
        UserRoleCommand command = getCommand(null);
        UserRoleQuery query = getQuery();
        UserRole userRole = getUserRole();
        UserInfo userInfo = getUser();
        InnovationCenter center = getIC();

        Mockito.when(userRoleFactory.toQuery(command)).thenReturn(query);
        Mockito.when(userRoleRepository.findList(query)).thenReturn(null);
        Mockito.when(userRoleFactory.toUserRole(command)).thenReturn(userRole);
        Mockito.when(userRoleRepository.add(userRole)).thenReturn(userRole);
        Mockito.doNothing().when(logUtils).insertAuditLog(any(), anyString(), anyString(), anyString(), anyString());
        Mockito.when(accountService.getUserInfo(USER_UUID)).thenReturn(userInfo);
        Mockito.when(innovationCenterMapper.findById(BUSI_ID)).thenReturn(center);

        UserRole authorize = userRoleApplicationService.authorize(any(), command);

        Assertions.assertEquals(userInfo.getUserName(), authorize.getUserName());
        Assertions.assertNull(authorize.getScopeName());
    }

    @Test
    @DisplayName("授权失败")
    public void testAuthorizeFailed() {
        UserRoleCommand command = getCommand(null);
        UserRoleQuery query = getQuery();
        UserRole userRole = getUserRole();

        List<UserRole> userRoleList = new ArrayList<>();
        userRoleList.add(userRole);

        Mockito.when(userRoleFactory.toQuery(command)).thenReturn(query);
        Mockito.when(userRoleRepository.findList(query)).thenReturn(userRoleList);

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> userRoleApplicationService.authorize(any(), command));

        Assertions.assertEquals("用户已配置此权限！", businessException.getMessage());
    }

    @Test
    @DisplayName("授权更新成功")
    public void testReAuthorizeSuccess() {
        UserRoleCommand command = getCommand(BUSI_ID);
        UserRoleQuery query = getQuery();
        UserRole userRole = getUserRole();
        UserInfo userInfo = getUser();
        InnovationCenter center = getIC();

        Mockito.when(userRoleRepository.findById(command.getId())).thenReturn(userRole);
        Mockito.when(userRoleFactory.toQuery(command)).thenReturn(query);
        Mockito.when(userRoleRepository.findList(query)).thenReturn(null);
        Mockito.when(userRoleFactory.toUserRole(command)).thenReturn(userRole);
        Mockito.when(userRoleRepository.update(userRole)).thenReturn(userRole);
        Mockito.doNothing().when(logUtils).insertAuditLog(null, "1", "permission", "authorize", null);
        Mockito.when(accountService.getUserInfo(null)).thenReturn(userInfo);
        Mockito.when(innovationCenterMapper.findById(BUSI_ID)).thenReturn(center);

        UserRole authorize = userRoleApplicationService.authorize(any(), command);

        Assertions.assertNull(authorize.getScopeName());
    }

    @Test
    @DisplayName("授权更新失败")
    public void testReAuthorizeFailed() {
        UserRoleCommand command = getCommand(null);
        UserRoleQuery query = getQuery();
        UserRole userRole = getUserRole();

        List<UserRole> userRoleList = new ArrayList<>();
        userRoleList.add(userRole);

        Mockito.when(userRoleRepository.findById(command.getId())).thenReturn(userRole);
        Mockito.when(userRoleFactory.toQuery(command)).thenReturn(query);
        Mockito.when(userRoleRepository.findList(query)).thenReturn(userRoleList);

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> userRoleApplicationService.authorize(any(), command));

        Assertions.assertEquals("用户已配置此权限！", businessException.getMessage());
    }

    @Test
    @DisplayName("取消授权成功")
    public void testUndoAuthorizeSuccess() {
        UserRoleCommand command = getCommand(BUSI_ID);
        UserRoleQuery query = getQuery();
        UserRole userRole = getUserRole();

        Mockito.when(userRoleRepository.findById(command.getId())).thenReturn(userRole);
        Mockito.when(userRoleFactory.toQuery(command)).thenReturn(query);
        Mockito.doNothing().when(userRoleRepository).delete(BUSI_ID);
        Mockito.doNothing().when(logUtils).insertAuditLog(any(), anyString(), anyString(), anyString(), anyString());

        userRoleApplicationService.undoAuthorize(any(), command);

        Assertions.assertEquals(BUSI_ID, userRole.getRoleId());
    }

    @Test
    @DisplayName("分页查询成功")
    public void testPageSuccess() {
        UserRoleQuery query = getQuery();
        UserRole userRole = getUserRole();
        List<UserRole> userRoleList = new ArrayList<>();
        userRoleList.add(userRole);
        Page<UserRole> userRolePage = new Page<>(1, 10);
        userRolePage.setRecords(userRoleList);

        Mockito.when(userRoleRepository.page(query)).thenReturn(userRolePage);

        Page<UserRole> rolePage = userRoleApplicationService.page(query);

        Assertions.assertEquals(userRolePage, rolePage);
    }

    @Test
    @DisplayName("查询成功")
    public void testFindByIdSuccess() {
        UserRole userRole = getUserRole();
        UserInfo userInfo = getUser();
        InnovationCenter center = getIC();

        Mockito.when(userRoleRepository.findById(BUSI_ID)).thenReturn(userRole);
        Mockito.when(accountService.getUserInfo(USER_UUID)).thenReturn(userInfo);
        Mockito.when(innovationCenterMapper.findById(BUSI_ID)).thenReturn(center);

        UserRole byId = userRoleApplicationService.findById(BUSI_ID);

        Assertions.assertEquals(userRole, byId);
    }

    private UserRoleCommand getCommand(Integer id) {
        UserRoleCommand command = new UserRoleCommand();
        command.setRoleId(BUSI_ID);
        command.setDataScope(0);
        command.setUuid(USER_UUID);
        command.setId(id);
        command.setLastUpdatedBy(USER_UUID);
        command.setLastUpdatedTime(new Date());
        return command;
    }

    private UserRoleQuery getQuery() {
        UserRoleQuery query = new UserRoleQuery();
        query.setRoleId(BUSI_ID);
        query.setDataScope(0);
        query.setUuid(USER_UUID);
        query.setCurrent(1);
        query.setSize(10);
        return query;
    }

    private UserRole getUserRole() {
        UserRole userRole = new UserRole();
        userRole.setRoleId(BUSI_ID);
        userRole.setDataScope(0);
        userRole.setUuid(USER_UUID);
        return userRole;
    }

    private UserInfo getUser() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUuid(USER_UUID);
        userInfo.setUserName("test");
        return userInfo;
    }

    private InnovationCenter getIC() {
        InnovationCenter innovationCenter = new InnovationCenter();
        innovationCenter.setId(BUSI_ID);
        innovationCenter.setName("测评中心");
        return innovationCenter;
    }
}
