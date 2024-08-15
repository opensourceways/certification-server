/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.filter;

import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.huawei.it.euler.config.CookieConfig;
import com.huawei.it.euler.config.usercenter.TokenConfig;
import com.huawei.it.euler.model.entity.EulerUser;
import com.huawei.it.euler.service.UserService;
import com.huawei.it.euler.util.EncryptUtils;
import com.huawei.it.euler.util.FilterUtils;
import com.huawei.it.euler.util.UserUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Jwt拦截器
 *
 * @since 2024/06/29
 */
@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenFilter.class);
    @Value("${url.whitelist}")
    private String urlWhitelist;

    @Resource
    private UserService userService;

    @Autowired
    private EncryptUtils encryptUtils;

    @Resource
    private Cache<String, Object> caffeineCache;

    @Autowired
    private CookieConfig cookieConfig;

    @Autowired
    private TokenConfig tokenConfig;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        LOGGER.debug("url : {}", request.getRequestURL());
        String[] jwtWhiteList = urlWhitelist.split(",");
        String currentURL = FilterUtils.getRequestUrl(request);
        for (String exclusionURL : jwtWhiteList) {
            if (currentURL.replaceAll("/certification/", "/")
                    .matches(exclusionURL.replaceAll("\\*", "\\.\\*"))
                    || ("/").equals(currentURL.replaceAll("/certification/", "/"))) {
                request.setAttribute("isAllowed", true);
                chain.doFilter(request, response);
                return;
            }
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            request.setAttribute("isAllowed", false);
            request.setAttribute("skipProtocolFilter", true);
            chain.doFilter(request, response);
            return;
        }
        String cookieUuid = UserUtils.getCookieUuid(request);
        if (StringUtils.isEmpty(cookieUuid)) {
            request.setAttribute("isAllowed", false);
            request.setAttribute("skipProtocolFilter", true);
            chain.doFilter(request, response);
            return;
        }
        request.setAttribute("isAllowed", false);
        request.setAttribute("skipProtocolFilter", false);
        String uuid = encryptUtils.aesDecrypt(cookieUuid);
        // 没有则代表未登录或者登录过期或者被用户中心主动退出，有则续期
        Object obj = caffeineCache.getIfPresent(cookieUuid);
        if (Objects.isNull(obj)) {
            cookieConfig.cleanCookie(request,response);
            chain.doFilter(request, response);
            return;
        } else {
            caffeineCache.put(cookieUuid, uuid);
            tokenConfig.refreshToken(request);
        }
        // 获取用户权限等信息
        EulerUser user = userService.findByUuid(uuid);
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(uuid, null, userService.getUserAuthorities(user.getId()));
        SecurityContextHolder.getContext().setAuthentication(token);
        chain.doFilter(request, response);
    }
}
