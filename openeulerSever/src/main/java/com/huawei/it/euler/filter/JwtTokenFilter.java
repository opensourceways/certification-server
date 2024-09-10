/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.filter;

import com.huawei.it.euler.config.CookieConfig;
import com.huawei.it.euler.ddd.domain.account.WhiteListService;
import com.huawei.it.euler.ddd.service.AccountService;
import com.huawei.it.euler.util.RequestUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

/**
 * Jwt拦截器
 *
 * @since 2024/06/29
 */
@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenFilter.class);

    @Autowired
    private CookieConfig cookieConfig;

    @Autowired
    private WhiteListService whiteListService;

    @Autowired
    private AccountService accountService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        LOGGER.debug("url : {}", request.getRequestURL());
        LOGGER.info("url : {}", request.getRequestURL());
        String shortUri = RequestUtils.getShortUri(request);
        if (whiteListService.isWriteUrl(shortUri)) {
            chain.doFilter(request, response);
            return;
        }

        boolean isLogin = accountService.isLogin(request, response);
        if (!isLogin) {
            cookieConfig.cleanCookie(request,response);
            chain.doFilter(request, response);
            return;
        }

        accountService.refreshLogin(request);

        accountService.setAuthentication(request);

        chain.doFilter(request, response);
    }
}
