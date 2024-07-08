/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.controller;

import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.model.constant.StringConstant;
import com.huawei.it.euler.model.entity.EulerUser;
import com.huawei.it.euler.model.enumeration.ProtocolEnum;
import com.huawei.it.euler.model.vo.CompanyVo;
import com.huawei.it.euler.model.vo.EulerUserVo;
import com.huawei.it.euler.model.vo.RoleVo;
import com.huawei.it.euler.model.vo.UserInfoVo;
import com.huawei.it.euler.service.RegionService;
import com.huawei.it.euler.service.UserService;
import com.huawei.it.euler.service.impl.CompanyServiceImpl;
import com.huawei.it.euler.util.EncryptUtils;
import com.huawei.it.euler.util.LogUtils;
import com.huawei.it.euler.util.StringPropertyUtils;
import com.huawei.it.euler.util.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * UserController
 *
 * @since 2024/07/05
 */
@Slf4j
@RestController
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private CompanyServiceImpl companyService;

    @Autowired
    private EncryptUtils encryptUtils;

    @Autowired
    private LogUtils logUtils;

    /**
     * 通过uuid查询个人信息
     *
     * @param uuid uuid
     * @return JsonResponse
     */
    @GetMapping("/users/user")
    @PreAuthorize("hasAnyRole('china_region')")
    public JsonResponse<EulerUserVo> findUserByUserUuid(
            @RequestParam("uuid") @NotBlank(message = "用户uuid不能为空") String uuid) {
        EulerUser user = userService.findByUuid(uuid);
        EulerUserVo userVo = new EulerUserVo();
        if (user != null) {
            String telPhone = companyService.reduceSensitivity(user.getTelephone(), StringConstant.TLE_PHONE);
            user.setTelephone(telPhone);
            String mail = companyService.reduceSensitivity(user.getMail(), StringConstant.MAIL);
            user.setMail(mail);
            BeanUtils.copyProperties(user, userVo);
            List<String> userRoles = userService.getUserRoles(user.getId());
            userVo.setRoles(userRoles);
        }
        return JsonResponse.success(userVo);
    }

    /**
     * 查询用户个人信息
     *
     * @param request request
     * @return JsonResponse
     */
    @GetMapping("/user/getUserInfo")
    @PreAuthorize("hasAnyRole('user', 'china_region', 'sig_group', 'euler_ic', 'flag_store', 'admin', 'OSV_user')")
    public JsonResponse<UserInfoVo> getUserInfo(HttpServletRequest request) {
        String cookieUuid = UserUtils.getCookieUuid(request);
        String uuid = encryptUtils.aesDecrypt(cookieUuid);
        if (StringUtils.isEmpty(uuid)) {
            return JsonResponse.failed("请先登录");
        }
        EulerUser user = userService.findByUuid(uuid);
        UserInfoVo vo = new UserInfoVo();
        if (user != null) {
            String telephone = user.getTelephone();
            telephone = encryptUtils.isEncrypted(telephone)
                    ? StringPropertyUtils.reducePhoneSensitivity(encryptUtils.aesDecrypt(telephone))
                    : StringPropertyUtils.reducePhoneSensitivity(telephone);
            String mail = user.getMail();
            mail = encryptUtils.isEncrypted(mail)
                    ? StringPropertyUtils.reduceEmailSensitivity(encryptUtils.aesDecrypt(mail))
                    : StringPropertyUtils.reduceEmailSensitivity(mail);
            user.setTelephone(telephone);
            user.setMail(mail);
            BeanUtils.copyProperties(user, vo);
            List<RoleVo> roleVos = userService.getUserRoleInfo(user.getId());
            vo.setRoles(roleVos.stream().map(RoleVo::getRole).collect(Collectors.toList()));
            vo.setRoleNames(roleVos.stream().map(RoleVo::getRoleName).collect(Collectors.toList()));
        }
        return JsonResponse.success(vo);
    }

    /**
     * 企业是否已实名认证
     *
     * @param request request
     * @return JsonResponse
     */
    @GetMapping("/user/isNeedCompanyAuthentication")
    @PreAuthorize("hasAnyRole('user', 'OSV_user')")
    public JsonResponse<Boolean> isNeedCompanyAuthentication(HttpServletRequest request) {
        String cookieUuid = UserUtils.getCookieUuid(request);
        String uuid = encryptUtils.aesDecrypt(cookieUuid);
        CompanyVo companyVo = companyService.findCompanyByUserUuid(uuid);
        boolean isNeedCompanyAuthentication = companyVo != null && companyVo.getStatus() == 1;
        return JsonResponse.success(isNeedCompanyAuthentication);
    }

    /**
     * 修改个人信息
     *
     * @param userVo userVo
     * @param request request
     * @return JsonResponse
     */
    @PostMapping("/user/modifyUserInfo")
    @PreAuthorize("hasAnyRole('user', 'china_region', 'sig_group', 'euler_ic', 'flag_store', 'admin', 'OSV_user')")
    @Transactional
    public JsonResponse<String> modifyUserInfo(@RequestBody @Validated EulerUserVo userVo, HttpServletRequest request) {
        String cookieUuid = UserUtils.getCookieUuid(request);
        String uuid = encryptUtils.aesDecrypt(cookieUuid);
        if (!Objects.equals(userVo.getUuid(), uuid)) {
            return JsonResponse.failedResult("无法修改该账号信息");
        }
        EulerUser eulerUser = userService.findByUuid(uuid);
        if (eulerUser == null) {
            return JsonResponse.failedResult("未查询到该账号信息");
        }
        String mail = eulerUser.getMail();
        mail = StringPropertyUtils.reduceEmailSensitivity(
                encryptUtils.isEncrypted(mail) ? encryptUtils.aesDecrypt(mail) : mail);
        if (StringUtils.isNoneBlank(mail) && !mail.equals(userVo.getMail())) {
            return JsonResponse.failedResult("不允许修改注册账号");
        }
        // 校验省市区
        if (StringUtils.isNotBlank(userVo.getProvince())) {
            List<String> allProvince = regionService.findAllProvince();
            boolean boolProvince = allProvince.stream().anyMatch(item -> item.equals(userVo.getProvince()));
            if (!boolProvince) {
                return JsonResponse.failedResult("非法的省份");
            }
        }
        if (StringUtils.isNotBlank(userVo.getCity())) {
            List<String> cityByProvince = regionService.findCityByProvince(userVo.getProvince());
            boolean boolCity = cityByProvince.stream().anyMatch(item -> item.equals(userVo.getCity()));
            if (!boolCity) {
                return JsonResponse.failedResult("非法的市区");
            }
        }
        userVo.setMail(encryptUtils.aesEncrypt(userVo.getMail()));
        userService.updateUser(userVo);
        logUtils.insertAuditLog(request, uuid, "user info", "modify", "modify user info");
        return JsonResponse.success();
    }

    /**
     * 注销个人信息
     *
     * @param request request
     * @return JsonResponse
     */
    @DeleteMapping("/user/deregisterUser")
    @PreAuthorize("hasAnyRole('user', 'china_region', 'sig_group', 'euler_ic', 'flag_store', 'admin', 'OSV_user')")
    public JsonResponse<String> deregisterUser(HttpServletRequest request) {
        return userService.deregisterUser(request);
    }

    /**
     * 签署隐私政策
     *
     * @param request request
     * @return JsonResponse
     */
    @PutMapping("/user/signPrivacyAgreement")
    @PreAuthorize("hasAnyRole('user', 'OSV_user')")
    @Transactional
    public JsonResponse<String> signPrivacyAgreement(HttpServletRequest request) {
        String cookieUuid = UserUtils.getCookieUuid(request);
        String uuid = encryptUtils.aesDecrypt(cookieUuid);
        logUtils.insertAuditLog(request, uuid, "privacy agreement", "sign", "sign privacy agreement");
        return userService.signAgreement(ProtocolEnum.PRIVACY_POLICY.getProtocolType(), uuid);
    }

    /**
     * 撤销签署隐私政策
     *
     * @param request request
     * @return JsonResponse
     */
    @PutMapping("/user/cancelPrivacyAgreement")
    @PreAuthorize("hasAnyRole('user', 'OSV_user')")
    @Transactional
    public JsonResponse<String> cancelPrivacyAgreement(HttpServletRequest request) {
        String cookieUuid = UserUtils.getCookieUuid(request);
        String uuid = encryptUtils.aesDecrypt(cookieUuid);
        logUtils.insertAuditLog(request, uuid, "privacy agreement", "cancel", "cancel privacy agreement");
        return userService.cancelAgreement(ProtocolEnum.PRIVACY_POLICY.getProtocolType(), uuid);
    }

    /**
     * 签署技术测评协议
     *
     * @param request request
     * @return JsonResponse
     */
    @PutMapping("/user/signTechnicalAgreement")
    @PreAuthorize("hasAnyRole('user', 'OSV_user')")
    @Transactional
    public JsonResponse<String> signTechnicalAgreement(HttpServletRequest request) {
        String cookieUuid = UserUtils.getCookieUuid(request);
        String uuid = encryptUtils.aesDecrypt(cookieUuid);
        logUtils.insertAuditLog(request, uuid, "technical agreement", "sign", "sign technical agreement");
        return userService.signAgreement(ProtocolEnum.TECHNICAL_EVALUATION_AGREEMENT.getProtocolType(), uuid);
    }

    /**
     * 撤销签署技术测评协议
     *
     * @param request request
     * @return JsonResponse
     */
    @PutMapping("/user/cancelTechnicalAgreement")
    @PreAuthorize("hasAnyRole('user', 'OSV_user')")
    @Transactional
    public JsonResponse<String> cancelTechnicalAgreement(HttpServletRequest request) {
        String cookieUuid = UserUtils.getCookieUuid(request);
        String uuid = encryptUtils.aesDecrypt(cookieUuid);
        logUtils.insertAuditLog(request, uuid, "technical agreement", "cancel", "cancel technical agreement");
        return userService.cancelAgreement(ProtocolEnum.TECHNICAL_EVALUATION_AGREEMENT.getProtocolType(), uuid);
    }

    /**
     * 签署兼容性清单使用声明
     *
     * @param request request
     * @return JsonResponse
     */
    @PutMapping("/user/signCompatibilityAgreement")
    @PreAuthorize("hasAnyRole('user', 'OSV_user')")
    @Transactional
    public JsonResponse<String> signCompatibilityAgreement(HttpServletRequest request) {
        String cookieUuid = UserUtils.getCookieUuid(request);
        String uuid = encryptUtils.aesDecrypt(cookieUuid);
        logUtils.insertAuditLog(request, uuid, "compatibility agreement", "sign", "sign compatibility agreement");
        return userService.signAgreement(ProtocolEnum.COMPATIBILITY_LIST_USAGE_STATEMENT.getProtocolType(), uuid);
    }

    /**
     * 撤销签署兼容性清单使用声明
     *
     * @param request request
     * @return JsonResponse
     */
    @PutMapping("/user/cancelCompatibilityAgreement")
    @PreAuthorize("hasAnyRole('user', 'OSV_user')")
    @Transactional
    public JsonResponse<String> cancelCompatibilityAgreement(HttpServletRequest request) {
        String cookieUuid = UserUtils.getCookieUuid(request);
        String uuid = encryptUtils.aesDecrypt(cookieUuid);
        logUtils.insertAuditLog(request, uuid, "compatibility agreement", "cancel", "cancel compatibility agreement");
        return userService.signAgreement(ProtocolEnum.COMPATIBILITY_LIST_USAGE_STATEMENT.getProtocolType(), uuid);
    }
}