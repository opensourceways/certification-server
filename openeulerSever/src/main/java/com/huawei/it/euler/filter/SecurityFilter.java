/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.filter;

import com.huawei.it.euler.ddd.domain.account.WhiteListService;
import com.huawei.it.euler.util.FilterUtils;
import com.huawei.it.euler.util.RequestUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

/**
 * SecurityFilter
 *
 * @since 2024/06/29
 */
@Slf4j
@Component
public class SecurityFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityFilter.class);

    @Value("${origin.value}")
    private String originCheck;

    @Value("${referer.value}")
    private String refererCheck;

    @Autowired
    private WhiteListService whiteListService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        LOGGER.debug("url : {}", request.getRequestURL());
        String shortUri = RequestUtils.getShortUri(request);
        // 校验referer
        int checkReferer = checkReferer(request, shortUri);
        if (!Objects.equals(HttpStatus.OK.value(), checkReferer)) {
            FilterUtils.writeErrorResp(response,
                    "The Referer request header is not found or the verification fails", checkReferer);
            return;
        }
        //校验origin
        int checkOrigin = checkOrigin(request, shortUri);
        if (!Objects.equals(HttpStatus.OK.value(), checkOrigin)) {
            FilterUtils.writeErrorResp(response,
                    "The Origin is not found or the verification fails", checkOrigin);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private int checkReferer(HttpServletRequest httpRequest, String shortUri) {
        String referer = httpRequest.getHeader("Referer");
        // get请求和白名单不校验referer
        boolean isVerifyReferer = "GET".equals(httpRequest.getMethod());
        if (!isVerifyReferer){
            isVerifyReferer = whiteListService.isWriteUrl(shortUri);
        }
        boolean isRefererInclude = true;
        if (!isVerifyReferer) {
            if (StringUtils.isEmpty(referer)) {
                isRefererInclude = false;
            } else {
                isRefererInclude = referer.startsWith(refererCheck);
            }
        }
        if (!isRefererInclude) {
            LOGGER.info("The Referer request header is not found or the verification fails: {}", shortUri);
            return HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
        return HttpStatus.OK.value();
    }

    private int checkOrigin(HttpServletRequest httpRequest, String shortUri) {
        // CORS校验
        String origin = httpRequest.getHeader("Origin");
        // get请求和白名单不校验origin
        boolean isVerifyOrigin = "GET".equals(httpRequest.getMethod());
        if (!isVerifyOrigin){
            isVerifyOrigin = whiteListService.isWriteUrl(shortUri);
        }
        if (isVerifyOrigin) {
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
        return HttpStatus.OK.value();
    }
}
