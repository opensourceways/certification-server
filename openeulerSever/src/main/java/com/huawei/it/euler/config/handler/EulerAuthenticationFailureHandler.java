/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.config.handler;

import com.huawei.it.euler.common.JsonResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录失败处理器
 *
 * @since 2024/06/28
 */
@Component
public class EulerAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            JsonResponse resp = new JsonResponse(JsonResponse.FAILED_STATUS, "账号或密码错误");
            outputStream.write(JSONObject.toJSONString(resp).getBytes("UTF-8"));
            outputStream.flush();
        }
    }
}
