/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.filter;

import com.github.benmanes.caffeine.cache.Cache;
import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.util.FilterUtils;
import com.huawei.it.euler.util.SessionManagement;
import com.huawei.it.euler.util.UserUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

/**
 * CsrfFilter
 *
 * @since 2024/06/28
 */
@Slf4j
@Component
public class CsrfFilter extends OncePerRequestFilter {
    @Value("${oauth.cookie.path}")
    private String cookiePath;

    @Resource
    private Cache<String, Object> caffeineCache;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        String token;
        try {
            token = SessionManagement.generateTokenToHex();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("XSRF-TOKEN Generation failed.");
        }
        Cookie cookie = new Cookie("XSRF-TOKEN", token);
        cookie.setPath(cookiePath);
        cookie.setSecure(true);
        cookie.setHttpOnly(false);
        response.addCookie(cookie);
        response.addHeader("XSRF-TOKEN", token);
        // 加入缓存
        String cookieUuid = UserUtils.getCookieUuid(request);
        String tokenKey = cookieUuid + "_xsrf-token";
        List<String> tokenOld = Objects.isNull(caffeineCache.getIfPresent(tokenKey))
                ? new ArrayList<>() : (List) caffeineCache.getIfPresent(tokenKey);
        tokenOld.add(token);
        if (tokenOld.size() > 10) {
            tokenOld.remove(0);
        }
        caffeineCache.put(tokenKey, tokenOld);
        String xsrfToken = request.getHeader("X-XSRF-TOKEN");
        if (!Arrays.asList("GET", "HEAD", "OPTIONS", "TRACE").contains(request.getMethod())
                && !tokenOld.contains(xsrfToken)) {
            log.info("The X-XSRF-TOKEN request header is verification fails, user : {}", cookieUuid);
            FilterUtils.writeErrorResp(response, "The X-XSRF-TOKEN request header is verification fails",
                    JsonResponse.FORBIDDEN_STATUS);
            return;
        }
        chain.doFilter(request, response);
    }
}
