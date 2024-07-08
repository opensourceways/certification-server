/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.filter;

import com.github.benmanes.caffeine.cache.Cache;
import com.huawei.it.euler.model.entity.EulerUser;
import com.huawei.it.euler.service.UserService;
import com.huawei.it.euler.util.EncryptUtils;
import com.huawei.it.euler.util.FilterUtils;
import com.huawei.it.euler.util.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * Jwt拦截器
 *
 * @since 2024/06/29
 */
@Slf4j
public class JwtTokenFilter extends BasicAuthenticationFilter {
    @Value("${url.whitelist}")
    private String urlWhitelist;

    @Resource
    private UserService userService;

    @Autowired
    private EncryptUtils encryptUtils;

    @Resource
    private Cache<String, Object> caffeineCache;

    public JwtTokenFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        String[] jwtWhiteList = urlWhitelist.split(",");
        String currentURL = FilterUtils.getRequestUrl(request);
        for (String exclusionURL : jwtWhiteList) {
            if (currentURL.replaceAll("/certification/", "/")
                    .matches(exclusionURL.replaceAll("\\*", "\\.\\*"))) {
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
        // 没有则没有登录，有则续期
        Object obj = caffeineCache.getIfPresent(cookieUuid);
        if (Objects.isNull(obj)) {
            chain.doFilter(request, response);
            return;
        } else {
            caffeineCache.put(cookieUuid, uuid);
        }
        // 获取用户权限等信息
        EulerUser user = userService.findByUuid(uuid);
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(uuid, null, userService.getUserAuthorities(user.getId()));
        SecurityContextHolder.getContext().setAuthentication(token);
        chain.doFilter(request, response);
    }
}
