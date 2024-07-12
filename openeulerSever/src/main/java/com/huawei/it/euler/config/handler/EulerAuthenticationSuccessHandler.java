/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.config.handler;

import com.alibaba.fastjson.JSONObject;
import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.common.JwtUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 登录成功处理
 *
 * @since 2024/06/28
 */
@Component
public class EulerAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // 生成jwt，并放置到请求头中
        String jwt = jwtUtils.generateToken(authentication.getName());
        response.setContentType("application/json;charset=utf-8");
        response.setHeader(jwtUtils.getHeader(), jwt);
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            JsonResponse resp = JsonResponse.success();
            outputStream.write(JSONObject.toJSONString(resp).getBytes("UTF-8"));
            outputStream.flush();
        }
    }
}
