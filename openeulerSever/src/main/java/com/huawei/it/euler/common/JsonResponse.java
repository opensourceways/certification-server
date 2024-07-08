/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.common;

import lombok.Data;

/**
 * 统一响应体
 *
 * @since 2024/06/28
 */
@Data
public class JsonResponse<T> {
    /**
     * 数据获取成功状态码
     */
    public static final Integer SUCCESS_STATUS = 200;

    /**
     * 数据获取成功提示信息
     */
    public static final String SUCCESS_MESSAGE = "success";

    /**
     * 业务异常状态码
     */
    public static final Integer FAILED_STATUS = 400;

    /**
     * 数据获取失败提示信息
     */
    public static final String FAILED_MESSAGE = "failed";

    /**
     * 用户未登录状态码
     */
    public static final Integer NOT_LOGIN_STATUS = 401;

    /**
     * 用户未登录提示信息
     */
    public static final String NOT_LOGIN_MESSAGE = "用户未登录";

    /**
     * 用户未授权状态码
     */
    public static final Integer FORBIDDEN_STATUS = 403;

    /**
     * 用户未授权提示信息
     */
    public static final String FORBIDDEN_MESSAGE = "用户没有访问权限";

    /**
     * 非法提交
     */
    public static final String ILLEGAL_SUBMIT = "非法提交";

    private Boolean status;

    private Integer code;

    private String message;

    private T result;

    public JsonResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public JsonResponse(Integer code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    /**
     * 快速定义成功方法，无返回值
     *
     * @return 统一响应体
     */
    public static JsonResponse<String> success() {
        return new JsonResponse<>(SUCCESS_STATUS, SUCCESS_MESSAGE, null);
    }

    public static <T> JsonResponse<T> success(T result) {
        return new JsonResponse<>(SUCCESS_STATUS, SUCCESS_MESSAGE, result);
    }

    /**
     * 快速定义失败方法，有返回提示信息
     *
     * @param errorMessage 错误消息提示
     * @return 统一响应体
     */
    public static <T> JsonResponse<T> failed(String errorMessage) {
        return new JsonResponse<>(FAILED_STATUS, errorMessage, null);
    }

    /**
     * 快速定义失败方法，有返回值
     *
     * @param result 错误返回值
     * @return 统一响应体
     */
    public static <T> JsonResponse<T> failedResult(T result) {
        return new JsonResponse<>(FAILED_STATUS, FAILED_MESSAGE, result);
    }
}
