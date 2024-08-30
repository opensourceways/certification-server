/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.config.CookieConfig;
import com.huawei.it.euler.config.usercenter.TokenConfig;
import com.huawei.it.euler.model.entity.EulerUser;
import com.huawei.it.euler.model.vo.EulerUserVo;
import com.huawei.it.euler.service.UserService;
import com.huawei.it.euler.util.EncryptUtils;
import com.huawei.it.euler.util.LogUtils;
import com.huawei.it.euler.util.SessionManagement;
import com.huawei.it.euler.util.UserUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * LoginController
 *
 * @since 2024/07/05
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class LoginController {

    @Value("${eulerlogin.clientId}")
    private String clientId;

    @Value("${eulerlogin.authCodeUrl}")
    private String authCodeUrl;

    @Value("${eulerlogin.redirectUrl}")
    private String redirectUrl;

    @Value("${eulerlogin.frontCallbackUrl}")
    private String frontCallbackUrl;

    @Value("${eulerlogin.logoutUrl}")
    private String logoutUrl;

    @Value("${eulerlogin.frontUrl}")
    private String frontUrl;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenConfig tokenConfig;

    @Autowired
    private EncryptUtils encryptUtils;

    @Autowired
    private CookieConfig cookieConfig;

    @Autowired
    private LogUtils logUtils;

    /**
     * idaas sso自定义登录
     *
     * @return JsonResponse
     */
    @GetMapping("/login")
    public JsonResponse<String> login() throws GeneralSecurityException {
        String state = SessionManagement.genSessionIdToHex();
        String loginUrl =
            authCodeUrl + "?response_type=code" + "&scope=openid profile email phone address offline_access" + "&state="
                + state + "&client_id=" + clientId + "&redirect_uri=" + redirectUrl;
        return new JsonResponse<>(loginUrl);
    }

    /**
     * idaas sso自定义登录
     *
     * @param request request
     * @param response response
     * @return JsonResponse
     */
    @GetMapping("/logout")
    public JsonResponse<String> logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("user logout");
        cookieConfig.cleanCookie(request, response);
        String cookieUuid = UserUtils.getCookieUuid(request);
        String userUuid = encryptUtils.aesDecrypt(cookieUuid);
        logUtils.insertAuditLog(request, userUuid, "login", "login out", "user login out");
        String logOut = logoutUrl + "?client_id=" + clientId + "&redirect_uri=" + frontUrl;
        return JsonResponse.success(logOut);
    }

    /**
     * user center will call that when other application logout to clear current application session
     *
     * @param request request
     * @param response responses
     * @return JsonResponse
     */
    @GetMapping("/logoutForCenter")
    public JsonResponse<String> logoutForCenter(HttpServletRequest request, HttpServletResponse response) {
        log.info("user center logout");
        String authorization = request.getHeader("Authorization");
        log.debug("logout for api request data authorization:{}", authorization);
        String uuid = tokenConfig.verifyJwt(authorization);
        log.debug("logout for api request data uuid:{}", uuid);
        if (StringUtils.isEmpty(uuid)) {
            return JsonResponse.failed("jwt parse error!");
        }
        String userUuid = encryptUtils.aesDecrypt(uuid);
        logUtils.insertAuditLog(request, userUuid, "login", "login out", "user center login out");
        return JsonResponse.success();
    }

    /**
     * 退出登录回调
     *
     * @param response response
     */
    @GetMapping("/logoutCallback")
    public void logoutCallback(HttpServletResponse response) throws IOException {
        response.sendRedirect(frontUrl);
    }

    /**
     * idaas sso登录回调
     *
     * @param request request
     * @param response response
     */
    @GetMapping("/callback")
    public void callBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getParameter("code");
        JSONObject jsonObject = tokenConfig.getAccessToken(code);
        JSONObject userInfoJson = tokenConfig.getUserInfo(jsonObject.getString("access_token"));
        String uuid = userInfoJson.getString("sub");
        updateUserDb(userInfoJson, uuid);
        cookieConfig.writeCookie(response, uuid);
        tokenConfig.refreshToken(request);
        logUtils.insertAuditLog(request, uuid, "login", "login in", "user login in");
        response.sendRedirect(frontCallbackUrl);
    }

    private void updateUserDb(JSONObject userInfoJson, String uuid) {
        EulerUser existUser = userService.findByUuid(uuid);
        String email = userInfoJson.getString("email");
        if (existUser == null) {
            EulerUser user = new EulerUser();
            user.setUuid(uuid);
            String telephone = userInfoJson.getString("phone_number");
            String name = userInfoJson.getString("username");
            user.setUsername(name.replaceAll(" ", ""));
            user.setTelephone(encryptUtils.aesEncrypt(telephone));
            user.setMail(encryptUtils.aesEncrypt(email));
            userService.insertUser(user);
        } else {
            String telephone = userInfoJson.getString("phone_number");
            if (!StringUtils.isEmpty(telephone)) {
                if (telephone.startsWith("0086")) {
                    telephone = telephone.substring(4).trim();
                }
                if (telephone.startsWith("+86")) {
                    telephone = telephone.substring(3).trim();
                }
            }
            String existUserTel = existUser.getTelephone();
            existUserTel =
                encryptUtils.isEncrypted(existUserTel) ? encryptUtils.aesDecrypt(existUserTel) : existUserTel;
            if (existUser.hasChange(telephone, existUserTel)) {
                existUser.setInfo(encryptUtils.aesEncrypt(telephone));
            }
            EulerUserVo userVo = new EulerUserVo();
            userVo.getVo(existUser);
            userService.updateUser(userVo);
        }
    }
}
