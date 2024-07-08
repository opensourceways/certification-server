/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.config.handler;

import com.alibaba.fastjson.JSONObject;
import com.huawei.it.euler.common.JsonResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 权限拒绝处理器
 *
 * @since 2024/06/28
 */
@Component
public class EulerAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            JsonResponse resp = new JsonResponse(JsonResponse.FORBIDDEN_STATUS, JsonResponse.FORBIDDEN_MESSAGE);
            outputStream.write(JSONObject.toJSONString(resp).getBytes("UTF-8"));
            outputStream.flush();
        }
    }
}
