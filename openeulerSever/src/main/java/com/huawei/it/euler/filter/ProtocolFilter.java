/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.filter;

import com.huawei.it.euler.mapper.ProtocolMapper;
import com.huawei.it.euler.model.entity.Protocol;
import com.huawei.it.euler.model.enumeration.ProtocolEnum;
import com.huawei.it.euler.service.impl.SoftwareServiceImpl;
import com.huawei.it.euler.util.EncryptUtils;
import com.huawei.it.euler.util.FilterUtils;
import com.huawei.it.euler.util.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * 协议拦截器，校验是否签署隐私协议
 *
 * @since 2024/06/29
 */
@Slf4j
public class ProtocolFilter extends BasicAuthenticationFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolFilter.class);

    @Value("${protocol.url.whitelist}")
    private String protocolWhitelist;

    @Autowired
    private EncryptUtils encryptUtils;

    @Autowired
    private ProtocolMapper protocolMapper;

    @Autowired
    private SoftwareServiceImpl softwareService;

    public ProtocolFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        // 校验协议
        int checkProtocol = checkProtocol(request);
        if (Objects.equals(HttpStatus.UNAUTHORIZED.value(), checkProtocol)) {
            FilterUtils.writeErrorResp(response, "not login", checkProtocol);
            return;
        }
        if (!Objects.equals(HttpStatus.OK.value(), checkProtocol)) {
            FilterUtils.writeErrorResp(response, "You haven't signed the privacy protocol yet", checkProtocol);
            return;
        }
        chain.doFilter(request, response);
    }

    private int checkProtocol(HttpServletRequest httpRequest) {
        boolean isAllowed = (boolean) httpRequest.getAttribute("isAllowed");
        if (isAllowed) {
            return HttpStatus.OK.value();
        }
        boolean skipProtocolFilter = (boolean) httpRequest.getAttribute("skipProtocolFilter");
        if (skipProtocolFilter) {
            return HttpStatus.UNAUTHORIZED.value();
        }
        LOGGER.info("check privacy protocol start");
        // 白名单接口不校验隐私政策
        boolean isVerifyProtocol = false;
        String currentURL = FilterUtils.getRequestUrl(httpRequest);
        for (String exclusionURL : protocolWhitelist.split(",")) {
            if (currentURL.replaceAll("/certification/", "/")
                    .matches(exclusionURL.replaceAll("\\*", "\\.\\*"))) {
                isVerifyProtocol = true;
                break;
            }
        }
        String cookieUuid = UserUtils.getCookieUuid(httpRequest);
        String userUuid = encryptUtils.aesDecrypt(cookieUuid);
        // 华为审核用户不需要校验协议
        List<String> roles = softwareService.getRoles(userUuid);
        if (!SoftwareServiceImpl.PARTNER_ROLE.containsAll(roles)) {
            isVerifyProtocol = true;
        }
        if (!isVerifyProtocol) {
            Protocol protocol = protocolMapper.selectProtocolByType(ProtocolEnum.PRIVACY_POLICY.getProtocolType(), userUuid);
            isVerifyProtocol = !Objects.isNull(protocol);
        }
        if (!isVerifyProtocol) {
            LOGGER.error("check privacy protocol failed");
            return HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
        LOGGER.info("check privacy protocol end");
        return HttpStatus.OK.value();
    }
}
