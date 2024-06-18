/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.exception;

/**
 * 参数异常
 *
 * @since 2024/06/29
 */
public class UrlRetryException extends RuntimeException{

    public UrlRetryException(String message) {
        super(message);
    }
}
