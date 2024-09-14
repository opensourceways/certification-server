/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.controller;

import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.ddd.domain.account.ProtocolService;
import com.huawei.it.euler.ddd.domain.account.Role;
import com.huawei.it.euler.ddd.domain.account.UserInfo;
import com.huawei.it.euler.ddd.service.AccountService;
import com.huawei.it.euler.exception.NoLoginException;
import com.huawei.it.euler.model.constant.StringConstant;
import com.huawei.it.euler.model.enumeration.ProtocolEnum;
import com.huawei.it.euler.model.vo.CompanyVo;
import com.huawei.it.euler.model.vo.EulerUserVo;
import com.huawei.it.euler.model.vo.UserInfoVo;
import com.huawei.it.euler.service.UserService;
import com.huawei.it.euler.service.impl.CompanyServiceImpl;
import com.huawei.it.euler.util.EncryptUtils;
import com.huawei.it.euler.util.LogUtils;
import com.huawei.it.euler.util.StringPropertyUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    private CompanyServiceImpl companyService;

    @Autowired
    private EncryptUtils encryptUtils;

    @Autowired
    private LogUtils logUtils;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ProtocolService protocolService;

    /**
     * 通过uuid查询个人信息
     *
     * @param uuid uuid
     * @return JsonResponse
     */
    @GetMapping("/users/user")
    @PreAuthorize("hasRole('china_region')")
    public JsonResponse<EulerUserVo> findUserByUserUuid(
            @RequestParam("uuid") @NotBlank(message = "用户uuid不能为空") String uuid) {
        UserInfo loginUser = accountService.getUserInfo(uuid);
        EulerUserVo userVo = new EulerUserVo();
        if (loginUser != null) {
            String telPhone = companyService.reduceSensitivity(loginUser.getPhone(), StringConstant.TLE_PHONE);
            userVo.setTelephone(telPhone);
            String mail = companyService.reduceSensitivity(loginUser.getEmail(), StringConstant.MAIL);
            userVo.setMail(mail);
            userVo.setUsername(loginUser.getUserName());
            BeanUtils.copyProperties(loginUser, userVo);
            List<Role> userRoleList = accountService.getUserRoleList(uuid);
            List<String> userRoles = userRoleList.stream().map(Role::getRole).toList();
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
    @PreAuthorize("hasAnyRole('user', 'china_region', 'sig_group',  'euler_ic', 'program_review','report_review','certificate_issuance', 'openatom_intel', 'flag_store', 'admin', 'OSV_user')")
    public JsonResponse<UserInfoVo> getUserInfo(HttpServletRequest request) throws NoLoginException {
        UserInfo loginUser = accountService.getLoginUser(request);
        UserInfoVo vo = new UserInfoVo();
        if (loginUser != null) {
            BeanUtils.copyProperties(loginUser, vo);
            String telephone = loginUser.getPhone();
            telephone = encryptUtils.isEncrypted(telephone)
                    ? StringPropertyUtils.reducePhoneSensitivity(encryptUtils.aesDecrypt(telephone))
                    : StringPropertyUtils.reducePhoneSensitivity(telephone);
            String mail = loginUser.getEmail();
            mail = encryptUtils.isEncrypted(mail)
                    ? StringPropertyUtils.reduceEmailSensitivity(encryptUtils.aesDecrypt(mail))
                    : StringPropertyUtils.reduceEmailSensitivity(mail);
            vo.setTelephone(telephone);
            vo.setMail(mail);
            vo.setUsername(loginUser.getUserName());
            List<Role> roleList = accountService.getUserRoleList(loginUser.getUuid());
            vo.setRoles(roleList.stream().map(Role::getRole).collect(Collectors.toList()));
            vo.setRoleNames(roleList.stream().map(Role::getRoleName).collect(Collectors.toList()));
            vo.setRole(userService.getUserAllRole(Integer.valueOf(loginUser.getUuid())));
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
    public JsonResponse<Boolean> isNeedCompanyAuthentication(HttpServletRequest request) throws NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        CompanyVo companyVo = companyService.findCompanyByUserUuid(uuid);
        boolean isNeedCompanyAuthentication = companyVo != null && companyVo.getStatus() == 1;
        return JsonResponse.success(isNeedCompanyAuthentication);
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
    public JsonResponse<String> signPrivacyAgreement(HttpServletRequest request) throws NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        logUtils.insertAuditLog(request, uuid, "privacy agreement", "sign", "sign privacy agreement");
        return protocolService.signAgreement(ProtocolEnum.PRIVACY_POLICY.getProtocolType(), uuid);
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
    public JsonResponse<String> cancelPrivacyAgreement(HttpServletRequest request) throws NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        logUtils.insertAuditLog(request, uuid, "privacy agreement", "cancel", "cancel privacy agreement");
        return protocolService.cancelAgreement(ProtocolEnum.PRIVACY_POLICY.getProtocolType(), uuid);
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
    public JsonResponse<String> signTechnicalAgreement(HttpServletRequest request) throws NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        logUtils.insertAuditLog(request, uuid, "technical agreement", "sign", "sign technical agreement");
        return protocolService.signAgreement(ProtocolEnum.TECHNICAL_EVALUATION_AGREEMENT.getProtocolType(), uuid);
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
    public JsonResponse<String> cancelTechnicalAgreement(HttpServletRequest request) throws NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        logUtils.insertAuditLog(request, uuid, "technical agreement", "cancel", "cancel technical agreement");
        return protocolService.cancelAgreement(ProtocolEnum.TECHNICAL_EVALUATION_AGREEMENT.getProtocolType(), uuid);
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
    public JsonResponse<String> signCompatibilityAgreement(HttpServletRequest request) throws NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        logUtils.insertAuditLog(request, uuid, "compatibility agreement", "sign", "sign compatibility agreement");
        return protocolService.signAgreement(ProtocolEnum.COMPATIBILITY_LIST_USAGE_STATEMENT.getProtocolType(), uuid);
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
    public JsonResponse<String> cancelCompatibilityAgreement(HttpServletRequest request) throws NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        logUtils.insertAuditLog(request, uuid, "compatibility agreement", "cancel", "cancel compatibility agreement");
        return protocolService.cancelAgreement(ProtocolEnum.COMPATIBILITY_LIST_USAGE_STATEMENT.getProtocolType(), uuid);
    }
}