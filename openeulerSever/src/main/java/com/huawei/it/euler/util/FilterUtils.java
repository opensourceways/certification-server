/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * FilterUtil
 *
 * @since 2024/06/29
 */
public class FilterUtils {
    /**
     * url归一化处理
     *
     * @param request request
     * @return requestURI
     */
    public static String getRequestUrl(HttpServletRequest request) {
        return request.getPathInfo() == null
                ? request.getServletContext().getContextPath() + request.getServletPath()
                : request.getServletContext().getContextPath() + request.getServletPath() + request.getPathInfo();
    }

    /**
     * 返回错误信息
     *
     * @param resp response
     * @param msg 错误信息
     * @param code 错误码
     * @throws IOException IOException
     */
    public static void writeErrorResp(HttpServletResponse resp, String msg, Integer code) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=utf-8");
        resp.setStatus(HttpStatus.OK.value());
        ServletOutputStream outputStream = resp.getOutputStream();
        JSONObject res = new JSONObject();
        res.put("code", code);
        res.put("message", msg);
        res.put("result", new JSONObject());
        outputStream.print(res.toString());
    }
}
