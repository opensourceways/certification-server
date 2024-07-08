/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.filter;

import com.huawei.it.euler.util.FilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * SecurityFilter
 *
 * @since 2024/06/29
 */
@Slf4j
public class SecurityFilter extends BasicAuthenticationFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityFilter.class);

    @Value("${origin.value}")
    private String originCheck;

    @Value("${referer.value}")
    private String refererCheck;

    @Value("${url.whitelist}")
    private String refererUrlWhitelist;

    @Value("${url.whitelist}")
    private String originUrlWhitelist;

    public SecurityFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


    }

    private int checkReferer(HttpServletRequest httpRequest, String shortUri) {
        LOGGER.info("checkReferer start");
        String referer = httpRequest.getHeader("Referer");
        // get请求和白名单不校验referer
        boolean isVerifyReferer = "GET".equals(httpRequest.getMethod());
        String[] refererWhitelist = refererUrlWhitelist.split(",");
        String currentURL = FilterUtils.getRequestUrl(httpRequest);
        for (String exclusionURL : refererWhitelist) {
            if (currentURL.matches(exclusionURL.replaceAll("\\*", "\\.\\*"))) {
                isVerifyReferer = true;
                break;
            }
        }
        boolean isRefererInclude = true;
        if (!isVerifyReferer) {
            if (StringUtils.isEmpty(referer)) {
                isRefererInclude = false;
            } else {
                isRefererInclude = refererCheck.equals(referer);
            }
        }
        if (!isRefererInclude) {
            LOGGER.info("The Referer request header is not found or the verification fails: {}", shortUri);
            return HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
        LOGGER.info("checkReferer end");
        return HttpStatus.OK.value();
    }

    private int checkOrigin(HttpServletRequest httpRequest, String shortUri) {
        // CORS校验
        LOGGER.info("checkOrigin start");
        String origin = httpRequest.getHeader("Origin");
        // get请求和白名单不校验origin
        boolean isVerifyOrigin = "GET".equals(httpRequest.getMethod());
        String[] originWhitelist = originUrlWhitelist.split(",");
        String currentURL = FilterUtils.getRequestUrl(httpRequest);
        for (String exclusionURL : originWhitelist) {
            if (currentURL.matches(exclusionURL.replaceAll("\\*", "\\.\\*"))) {
                isVerifyOrigin = true;
                break;
            }
        }
        if (isVerifyOrigin) {
            LOGGER.info("checkOrigin end");
            return HttpStatus.OK.value();
        }
        if (!StringUtils.isEmpty(origin)) {
            boolean isIncludeOrigin = originCheck.equals(origin);
            if (!isIncludeOrigin) {
                LOGGER.info("The Origin is not found or the verification fails: {}", shortUri);
                return HttpStatus.INTERNAL_SERVER_ERROR.value();
            }
        } else {
            LOGGER.info("The Origin is not found or the verification fails: {}", shortUri);
            return HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
        LOGGER.info("checkOrigin end");
        return HttpStatus.OK.value();
    }
}
