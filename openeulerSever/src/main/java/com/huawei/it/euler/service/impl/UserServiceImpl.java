/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.it.euler.exception.ParamException;
import com.huawei.it.euler.mapper.CompanyMapper;
import com.huawei.it.euler.mapper.RoleMapper;
import com.huawei.it.euler.mapper.SoftwareMapper;
import com.huawei.it.euler.mapper.UserMapper;
import com.huawei.it.euler.model.entity.Attachments;
import com.huawei.it.euler.model.entity.Company;
import com.huawei.it.euler.model.entity.EulerUser;
import com.huawei.it.euler.model.entity.Software;
import com.huawei.it.euler.model.enumeration.RoleEnum;
import com.huawei.it.euler.model.vo.RoleVo;
import com.huawei.it.euler.service.UserService;

/**
 * UserServiceImpl
 *
 * @since 2024/07/04
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final String ACCOUNT_INVALID = "账号或密码错误";

    private static final Integer ALL_PERMISSION = 0;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private SoftwareMapper softwareMapper;

    @Autowired
    private CompanyMapper companyMapper;

    @Override
    public String getUserAuthorityInfo(Integer userId) {
        List<String> roleList = roleMapper.findByUserId(userId);
        String authorities = "";
        if (!roleList.isEmpty()) {
            String roleNames = roleList.stream().map(role -> "ROLE_" + role).collect(Collectors.joining(","));
            authorities = authorities.concat(roleNames);
        }
        return authorities;
    }
    public List<Integer> getUserRolesByUUID(Integer uuid) {
        return roleMapper.findByUUID(uuid);
    }

    @Override
    public List<GrantedAuthority> getUserAuthorities(Integer userId) {
        String authorityInfo = getUserAuthorityInfo(userId);
        return AuthorityUtils.commaSeparatedStringToAuthorityList(authorityInfo);
    }

    @Override
    public UserDetails loadUserByUsername(String uuid) throws UsernameNotFoundException {
        EulerUser user = userMapper.findByUuid(uuid);
        UserDetails userDetails;
        if (user != null) {
            userDetails = new User("", "", getUserAuthorities(user.getId()));
        } else {
            throw new UsernameNotFoundException(ACCOUNT_INVALID);
        }
        return userDetails;
    }

    @Override
    public String getUserAllDateScope(Integer uuid) {
        Set<Integer> dateScope = roleMapper.findRoleByUserId(uuid, null).stream().map(RoleVo::getDataScope)
            .filter(Objects::nonNull).collect(Collectors.toSet());
        if (dateScope.contains(ALL_PERMISSION)) {
            return "ALL";
        } else {
            return String.join(",", dateScope.stream().map(String::valueOf).toArray(String[]::new));
        }
    }

    public Map<Integer, List<Integer>> getUserAllRole(Integer uuid) {
        List<RoleVo> roleList = roleMapper.findRoleByUserId(uuid, null);
        return roleList.stream().collect(Collectors.groupingBy(role -> Integer.parseInt(role.getRole()),
            Collectors.mapping(RoleVo::getDataScope, Collectors.toList())));
    }

    @Override
    public boolean isUserDataScopeByRole(Integer userUuid, Software software) {
        if (RoleEnum.USER.getRoleId().equals(software.getReviewRole())
            && Objects.equals(userUuid, Integer.valueOf(software.getUserUuid()))) {
            return true;
        }
        return hasDateScopePermission(String.valueOf(userUuid), software.getReviewRole(), software.getTestOrgId());
    }

    @Override
    public boolean isUserPermission(Integer userUuid, Software software) {
        if (Objects.equals(userUuid, Integer.valueOf(software.getUserUuid()))) {
            return true;
        } else {
            Company company = companyMapper.findCompanyByUserUuid(String.valueOf(userUuid));
            if (company != null){
                return company.getCompanyCode().equals(software.getCompanyCode());
            }
            return hasDateScopePermission(String.valueOf(userUuid), null, software.getTestOrgId());
        }
    }

    @Override
    public boolean isAttachmentPermission(String userUuid, Attachments attachment) {
        List<Integer> roleList = getUserRolesByUUID(Integer.valueOf(userUuid));
        if (RoleEnum.isUser(roleList) && Objects.equals(attachment.getUuid(), userUuid)) {
            return true;
        }
        switch (attachment.getFileType()) {
            case "logo":
            case "license":
                return roleList.contains(RoleEnum.CHINA_REGION.getRoleId());
            case "testReport":
            case "sign":
            case "certificates":
                Software software = softwareMapper.findById(Integer.valueOf(attachment.getSoftwareId()));
                return isUserPermission(Integer.valueOf(userUuid), software);
            default:
                throw new ParamException("无权限下载");
        }
    }

    @Override
    public boolean hasDateScopePermission(String uuid, Integer roleId, Integer dataScope) {
        Set<Integer> dateScopeSet = roleMapper.findRoleByUserId(Integer.valueOf(uuid), roleId).stream().map(RoleVo::getDataScope)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        return dateScopeSet.contains(ALL_PERMISSION) || dateScopeSet.contains(dataScope);
    }
}
