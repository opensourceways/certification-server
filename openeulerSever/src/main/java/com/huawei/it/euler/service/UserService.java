/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.service;

import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.model.entity.EulerUser;
import com.huawei.it.euler.model.vo.EulerUserVo;
import com.huawei.it.euler.model.vo.RoleVo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户service
 *
 * @since 2024/06/29
 */
public interface UserService extends UserDetailsService {
    /**
     * 新增用户
     *
     * @param user uesr
     */
    void insertUser(EulerUser user);

    /**
     * 根据用户id获取用户权限信息
     *
     * @param userId 用户id
     * @return 用户权限
     */
    String getUserAuthorityInfo(Integer userId);

    /**
     * 获取用户角色列表
     *
     * @param userId 用户id
     * @return roles
     */
    List<String> getUserRoles(Integer userId);

    /**
     * 获取用户角色列表
     *
     * @param userId 用户id
     * @return roles
     */
    List<RoleVo> getUserRoleInfo(Integer userId);

    /**
     * 根据用户id获取用户权限信息
     *
     * @param userId userId
     * @return 用户权限信息列表
     */
    List<GrantedAuthority> getUserAuthorities(Integer userId);

    /**
     * 更新个人信息
     *
     * @param userVo 用户
     */
    void updateUser(EulerUserVo userVo);

    /**
     * 根据uuid查询用户
     *
     * @param uuid uuid
     * @return 用户信息
     */
    EulerUser findByUuid(String uuid);

    /**
     * 注销用户信息
     *
     * @param request request
     * @return JsonResponse
     */
    JsonResponse<String> deregisterUser(HttpServletRequest request);

    /**
     * 签署协议
     *
     * @param protocolType 协议类型
     * @param userUuid uuid
     * @return JsonResponse
     */
    JsonResponse<String> signAgreement(Integer protocolType, String userUuid);

    /**
     * 撤销签署协议
     *
     * @param protocolType 协议类型
     * @param userUuid uuid
     * @return JsonResponse
     */
    JsonResponse<String> cancelAgreement(Integer protocolType, String userUuid);
}
