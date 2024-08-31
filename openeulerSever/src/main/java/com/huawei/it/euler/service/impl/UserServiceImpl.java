/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.service.impl;

import java.util.Date;
import java.util.List;
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

import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.exception.ParamException;
import com.huawei.it.euler.mapper.ProtocolMapper;
import com.huawei.it.euler.mapper.RoleMapper;
import com.huawei.it.euler.mapper.SoftwareMapper;
import com.huawei.it.euler.mapper.UserMapper;
import com.huawei.it.euler.model.entity.Attachments;
import com.huawei.it.euler.model.entity.EulerUser;
import com.huawei.it.euler.model.entity.Protocol;
import com.huawei.it.euler.model.entity.Software;
import com.huawei.it.euler.model.enumeration.ProtocolEnum;
import com.huawei.it.euler.model.enumeration.RoleEnum;
import com.huawei.it.euler.model.vo.EulerUserVo;
import com.huawei.it.euler.model.vo.RoleVo;
import com.huawei.it.euler.service.UserService;
import com.huawei.it.euler.util.EncryptUtils;
import com.huawei.it.euler.util.LogUtils;
import com.huawei.it.euler.util.UserUtils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * UserServiceImpl
 *
 * @since 2024/07/04
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private static final String ACCOUNT_INVAILD = "账号或密码错误";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private SoftwareMapper softwareMapper;

    @Autowired
    private ProtocolMapper protocolMapper;

    @Autowired
    private LogUtils logUtils;

    @Autowired
    private EncryptUtils encryptUtils;

    @Override
    public void insertUser(EulerUser user) {
        userMapper.insertUser(user);
        EulerUser eulerUser = userMapper.findByUuid(user.getUuid());
        // 新注册用户设置默认角色
        roleMapper.insertDefaultRole(eulerUser.getId());
    }

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

    @Override
    public List<String> getUserRoles(Integer userId) {
        return roleMapper.findByUserId(userId);
    }

    public List<Integer> getUserRolesByUUID(Integer uuid) {
        return roleMapper.findByUUID(uuid);
    }

    @Override
    public List<RoleVo> getUserRoleInfo(Integer userId) {
        return roleMapper.findRoleInfoByUserId(userId);
    }

    @Override
    public List<GrantedAuthority> getUserAuthorities(Integer userId) {
        String authorityInfo = getUserAuthorityInfo(userId);
        return AuthorityUtils.commaSeparatedStringToAuthorityList(authorityInfo);
    }

    @Override
    public void updateUser(EulerUserVo userVo) {
        userMapper.updateUser(userVo);
    }

    @Override
    public EulerUser findByUuid(String uuid) {
        return userMapper.findByUuid(uuid);
    }

    @Override
    @Transactional
    public JsonResponse<String> deregisterUser(HttpServletRequest request) {
        String cookieUuid = UserUtils.getCookieUuid(request);
        String userUuid = encryptUtils.aesDecrypt(cookieUuid);
        EulerUser user = userMapper.findByUuid(userUuid);
        if (user == null) {
            throw new ParamException("账号异常，注销失败");
        }
        if (Objects.equals(user.getUseable(), 0)) {
            throw new ParamException("您已注销账号！将在15个工作日内注销个人信息。");
        }
        userMapper.deleteUser(userUuid);
        logUtils.insertAuditLog(request, userUuid, "user info", "deregister", "deregister user info");
        return JsonResponse.success();
    }

    @Override
    public JsonResponse<String> signAgreement(Integer protocolType, String userUuid) {
        Date date = new Date();
        Protocol protocol = new Protocol();
        protocol.setProtocolType(protocolType).setProtocolName(ProtocolEnum.getProtocolNameByType(protocolType))
            .setStatus(1).setCreatedBy(userUuid).setCreatedTime(date).setUpdatedBy(userUuid).setUpdatedTime(date);
        if (Objects.equals(protocolType, ProtocolEnum.PRIVACY_POLICY.getProtocolType())) {
            Protocol signedProtocol = protocolMapper.selectProtocolByType(protocolType, userUuid);
            if (signedProtocol != null) {
                throw new ParamException("无需重复签署");
            }
        }
        int count = protocolMapper.insertUserSignProtocol(protocol);
        if (count == 0) {
            throw new ParamException("签署失败");
        }
        return JsonResponse.success();
    }

    @Override
    public JsonResponse<String> cancelAgreement(Integer protocolType, String userUuid) {
        Protocol signedProtocol = protocolMapper.selectProtocolDesc(protocolType, userUuid);
        if (signedProtocol == null) {
            throw new ParamException("未签署该协议");
        }
        int count = protocolMapper.cancelSignedProtocol(signedProtocol.getId());
        if (count == 0) {
            throw new ParamException("撤销签署失败");
        }
        return JsonResponse.success();
    }

    @Override
    public UserDetails loadUserByUsername(String uuid) throws UsernameNotFoundException {
        EulerUser user = userMapper.findByUuid(uuid);
        UserDetails userDetails;
        if (user != null) {
            userDetails = new User("", "", getUserAuthorities(user.getId()));
        } else {
            throw new UsernameNotFoundException(ACCOUNT_INVAILD);
        }
        return userDetails;
    }

    @Override
    public String getUserAllDateScope(Integer uuid) {
        Set<Integer> dateScope = roleMapper.findRoleByUserId(uuid, null).stream().map(RoleVo::getDataScope)
            .filter(Objects::nonNull).collect(Collectors.toSet());
        if (dateScope.contains(0)) {
            return "ALL";
        } else {
            return String.join(",", dateScope.stream().map(String::valueOf).toArray(String[]::new));
        }
    }

    @Override
    public boolean isUserDataScopeByRole(Integer uuid, Integer role, Integer softwareId) {
        List<Integer> dateScope = roleMapper.findRoleByUserId(uuid, role).stream().map(RoleVo::getDataScope)
            .filter(Objects::nonNull).toList();
        return dateScope.contains(0) || dateScope.contains(softwareId);
    }

    @Override
    public boolean isUserPermission(Integer userUuid, Software software) {
        if (Objects.equals(userUuid, Integer.valueOf(software.getUserUuid()))) {
            return true;
        } else {
            Set<Integer> dateScope = roleMapper.findRoleByUserId(userUuid, null).stream().map(RoleVo::getDataScope)
                .filter(Objects::nonNull).collect(Collectors.toSet());
            if (dateScope.contains(0)) {
                return true;
            } else {
                return dateScope.contains(software.getTestOrgId());
            }
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
}
