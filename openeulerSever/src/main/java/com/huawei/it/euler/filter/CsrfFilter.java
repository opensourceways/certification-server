/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.filter;

import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.config.CookieConfig;
import com.huawei.it.euler.ddd.domain.account.WhiteListService;
import com.huawei.it.euler.ddd.domain.account.XsrfService;
import com.huawei.it.euler.ddd.service.AccountService;
import com.huawei.it.euler.exception.NoLoginException;
import com.huawei.it.euler.util.FilterUtils;
import com.huawei.it.euler.util.RequestUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

/**
 * CsrfFilter
 *
 * @since 2024/06/28
 */
@Slf4j
@Component
public class CsrfFilter extends OncePerRequestFilter {

    @Autowired
    private WhiteListService whiteListService;

    @Autowired
    private XsrfService xsrfService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CookieConfig cookieConfig;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        String loginUuid = "0";
        try {
            loginUuid = accountService.getLoginUuid(request);
        } catch (NoLoginException e) {
            log.error(e.getMessage());
        }

        String token = xsrfService.refreshToken(loginUuid);
        cookieConfig.writeXsrfInCookie(response, xsrfService.getResponseHeaderKey(), token);
        response.addHeader(xsrfService.getResponseHeaderKey(), token);

        boolean filterMethod = !Arrays.asList("GET", "HEAD", "OPTIONS", "TRACE").contains(request.getMethod());
        String shortUri = RequestUtils.getShortUri(request);
        boolean writeUrl = whiteListService.isWriteUrl(shortUri);
        if (filterMethod && !writeUrl) {
            String xsrfToken = request.getHeader(xsrfService.getRequestHeaderKey());
            boolean isActive = xsrfService.isTokenActive(loginUuid, xsrfToken);
            if (!isActive) {
                log.info("The X-XSRF-TOKEN request header is verification fails, user : {}", loginUuid);
                FilterUtils.writeErrorResp(response, "The X-XSRF-TOKEN request header is verification fails",
                        JsonResponse.FORBIDDEN_STATUS);
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
