/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.config.handler;

import com.huawei.it.euler.common.JsonResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 匿名用户访问无权限资源处理器
 *
 *
 * @since 2024/06/28
 */
@Component
public class EulerAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            JsonResponse resp =
                    new JsonResponse(JsonResponse.NOT_LOGIN_STATUS, JsonResponse.NOT_LOGIN_MESSAGE, false);
            outputStream.write(JSONObject.toJSONString(resp).getBytes("UTF-8"));
            outputStream.flush();
        }
    }
}
