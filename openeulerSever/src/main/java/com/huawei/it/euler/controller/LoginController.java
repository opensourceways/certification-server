/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.controller;


import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.common.JwtUtils;
import com.huawei.it.euler.model.entity.EulerUser;
import com.huawei.it.euler.model.vo.EulerUserVo;
import com.huawei.it.euler.service.UserService;
import com.huawei.it.euler.util.EncryptUtils;
import com.huawei.it.euler.util.LogUtils;
import com.huawei.it.euler.util.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * LoginController
 *
 * @since 2024/07/05
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class LoginController {
    @Value("${oauth.cookie.path}")
    private String cookiePath;

    @Value("${idass.clientId}")
    private String clientId;

    @Value("${idaas.authCodeUrl}")
    private String authCodeUrl;

    @Value("${idaas.accessTokenUrl}")
    private String accessTokenUrl;

    @Value("${idaas.userInfoUrl}")
    private String userInfoUrl;

    @Value("${idaas.redirectUrl}")
    private String redirectUrl;

    @Value("${idaas.clientSecret}")
    private String clientSecret;

    @Value("${idaas.frontCallbackUrl}")
    private String frontCallbackUrl;

    @Value("${idaas.logoutUrl}")
    private String logoutUrl;

    @Value("${idaas.frontUrl}")
    private String frontUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EncryptUtils encryptUtils;

    @Autowired
    private Cache<String, Object> caffeineCache;

    @Autowired
    private LogUtils logUtils;

    /**
     * idaas sso自定义登录
     *
     * @return JsonResponse
     */
    @GetMapping("/login")
    public JsonResponse<String> login() throws GeneralSecurityException {
        String state = UUID.randomUUID().toString();
        String loginUrl = authCodeUrl + "?response_type=code" + "&scope=base.profile" + "&state="
                + state + "&client_id=" + clientId + "&redirect_uri=" + redirectUrl;
        return JsonResponse.success(loginUrl);
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
        cleanCookie(request, response);
        String cookieUuid = UserUtils.getCookieUuid(request);
        String userUuid = encryptUtils.aesDecrypt(cookieUuid);
        logUtils.insertAuditLog(request, userUuid, "login", "login out", "user login out");
        return JsonResponse.success(logoutUrl);
    }

    /**
     * 退出登录回调
     *
     * @param response response
     */
    @GetMapping("/logoutCallback")
    public void logout(HttpServletResponse response) throws IOException {
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
        JSONObject jsonObject = getAccessToken(code);
        JSONObject userInfoJson = getUserInfo(jsonObject.getString("access_token"));
        String uuid = userInfoJson.getString("uuid");
        updateUserDb(userInfoJson, uuid);
        writeCookie(response, uuid);
        logUtils.insertAuditLog(request, uuid, "login", "login in", "user login in");
        String token = jwtUtils.generateToken(uuid);
        response.sendRedirect(frontCallbackUrl + "?token=" + token);
    }

    private void updateUserDb(JSONObject userInfoJson, String uuid) {
        EulerUser existUser = userService.findByUuid(uuid);
        String email = userInfoJson.getString("email");
        if (existUser == null) {
            EulerUser user = new EulerUser();
            user.setUuid(uuid);
            String telephone = userInfoJson.getString("telephoneNumber");
            if (!StringUtils.isEmpty(telephone)) {
                telephone = telephone.substring(4);
            }
            String name = userInfoJson.getString("cn");
            user.setUsername(name.replaceAll(" ", ""));
            user.setTelephone(encryptUtils.aesEncrypt(telephone));
            user.setMail(encryptUtils.aesEncrypt(email));
            userService.insertUser(user);
        } else {
            String telephone = userInfoJson.getString("telephoneNumber");
            if (!StringUtils.isEmpty(telephone)) {
                if (telephone.startsWith("0086")) {
                    telephone = telephone.substring(4).trim();
                }
                if (telephone.startsWith("+86")) {
                    telephone = telephone.substring(3).trim();
                }
            }
            String existUserTel = existUser.getTelephone();
            existUserTel = encryptUtils.isEncrypted(existUserTel)
                    ? encryptUtils.aesDecrypt(existUserTel) : existUserTel;
            if (existUser.hasChange(telephone, existUserTel)) {
                existUser.setInfo(encryptUtils.aesEncrypt(telephone));
            }
            EulerUserVo userVo = new EulerUserVo();
            userVo.getVo(existUser);
            userService.updateUser(userVo);
        }
    }

    private JSONObject getAccessToken(String code) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("grant_type", "authorization_code");
        map.put("client_id", clientId);
        map.put("client_secret", clientSecret);
        map.put("redirect_uri", redirectUrl);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(map);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(accessTokenUrl, httpEntity, String.class);
        String body = responseEntity.getBody();
        return JSONObject.parseObject(body);
    }

    private JSONObject getUserInfo(String accessToken) {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", accessToken);
        map.put("client_id", clientId);
        map.put("scope", "base.profile");
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(map);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(accessTokenUrl, httpEntity, String.class);
        String body = responseEntity.getBody();
        return JSONObject.parseObject(body);
    }

    private void writeCookie(HttpServletResponse response, String uuid) {
        String enUuid = encryptUtils.aesEncrypt(uuid);
        addCookie(response, "uuid", enUuid);
        addCookie(response, "openeuler", "in");
        // 加入缓存
        caffeineCache.put(enUuid, uuid);
    }

    private void addCookie(HttpServletResponse response, String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath(cookiePath);
        response.addCookie(cookie);
    }

    private void cleanCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return;
        }
        for (Cookie cookie : cookies) {
            if (cookie == null) {
                continue;
            }
            cookie = new Cookie(cookie.getName(), cookie.getValue());
            cookie.setPath(cookiePath);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            if (Objects.equals(cookie.getName(), "uuid")) {
                // 删除缓存
                caffeineCache.invalidate(cookie.getValue());
            }
        }
    }
}
