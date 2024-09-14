/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.huawei.it.euler.model.entity.Attachments;
import com.huawei.it.euler.model.entity.Software;


/**
 * 用户service
 *
 * @since 2024/06/29
 */
public interface UserService extends UserDetailsService {


    /**
     * 根据用户id获取用户权限信息
     *
     * @param userId 用户id
     * @return 用户权限
     */
    String getUserAuthorityInfo(Integer userId);


    List<Integer> getUserRolesByUUID(Integer uuid);

    /**
     * 根据用户id获取用户权限信息
     *
     * @param userId userId
     * @return 用户权限信息列表
     */
    List<GrantedAuthority> getUserAuthorities(Integer userId);

    /**
     * 查询时获取用户的数据权限
     *
     * @param uuid 用户uuid
     * @return 用户的数据权限
     */
    String getUserAllDateScope(Integer uuid);
    Map<Integer, List<Integer>> getUserAllRole(Integer uuid);
    /**
     * 查询用户对应角色是否有的操作权限
     * 
     * @param userUuid 用户uuid
     * @param software 角色id
     * @return 用户的数据权限
     */
    boolean isUserDataScopeByRole(Integer userUuid, Software software);

    /**
     * 查询用户是否有流程的权限
     * 
     * @param userUuid 用户uuid
     * @param software 流程信息
     * @return 结果
     */
    boolean isUserPermission(Integer userUuid, Software software);

    boolean isAttachmentPermission(String userUuid, Attachments attachment);
}
