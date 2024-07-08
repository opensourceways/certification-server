/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.filter;

import com.github.benmanes.caffeine.cache.Cache;
import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.util.FilterUtils;
import com.huawei.it.euler.util.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * CsrfFilter
 *
 * @since 2024/06/28
 */
@Slf4j
public class CsrfFilter extends BasicAuthenticationFilter {
    @Value("${oauth.cookie.path}")
    private String cookiePath;

    @Resource
    private Cache<String, Object> caffeineCache;

    public CsrfFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        // todo 随机数生成token
        String token = UUID.randomUUID().toString();
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
            FilterUtils.writeErrorResp(response, "The X-XSRF-TOKEN request header is verification fails",
                    JsonResponse.FORBIDDEN_STATUS);
        }
        chain.doFilter(request, response);
    }
}
