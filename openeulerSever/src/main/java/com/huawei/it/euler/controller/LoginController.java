/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.common.JwtUtils;
import com.huawei.it.euler.model.entity.EulerUser;
import com.huawei.it.euler.model.vo.EulerUserVo;
import com.huawei.it.euler.service.UserService;
import com.huawei.it.euler.util.EncryptUtils;
import com.huawei.it.euler.util.LogUtils;
import com.huawei.it.euler.util.SessionManagement;
import com.huawei.it.euler.util.UserUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    @Value("${eulerlogin.clientId}")
    private String clientId;

    @Value("${eulerlogin.authCodeUrl}")
    private String authCodeUrl;

    @Value("${eulerlogin.accessTokenUrl}")
    private String accessTokenUrl;

    @Value("${eulerlogin.userInfoUrl}")
    private String userInfoUrl;

    @Value("${eulerlogin.redirectUrl}")
    private String redirectUrl;

    @Value("${eulerlogin.clientSecret}")
    private String clientSecret;

    @Value("${eulerlogin.frontCallbackUrl}")
    private String frontCallbackUrl;

    @Value("${eulerlogin.logoutUrl}")
    private String logoutUrl;

    @Value("${eulerlogin.frontUrl}")
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
        String state = SessionManagement.genSessionIdToHex();
        String loginUrl = authCodeUrl + "?response_type=code" + "&scope=openid profile email phone address offline_access" + "&state="
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
        cleanCookie(request, response);
        String cookieUuid = UserUtils.getCookieUuid(request);
        String userUuid = encryptUtils.aesDecrypt(cookieUuid);
        logUtils.insertAuditLog(request, userUuid, "login", "login out", "user login out");
        String logOut = logoutUrl + "?client_id=" + clientId + "&redirect_uri=" + frontUrl;
        return JsonResponse.success(logOut);
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
        JSONObject jsonObject = getAccessToken(code);
        JSONObject userInfoJson = getUserInfo(jsonObject.getString("access_token"));
        String uuid = userInfoJson.getString("sub");
        updateUserDb(userInfoJson, uuid);
        writeCookie(response, uuid);
        logUtils.insertAuditLog(request, uuid, "login", "login in", "user login in");
//        String token = jwtUtils.generateToken(uuid);
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
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("redirect_uri", redirectUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(formData, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(accessTokenUrl, httpEntity, String.class);
        String body = responseEntity.getBody();
        return JSONObject.parseObject(body);
    }

    private JSONObject getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        // 如果需要，还可以设置其他头，比如 Content-Type
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                userInfoUrl,
                org.springframework.http.HttpMethod.GET,
                entity,
                String.class
        );
        String body = responseEntity.getBody();
        return JSON.parseObject(body);
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
