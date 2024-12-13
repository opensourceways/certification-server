/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.exception;

/**
 * 未登录异常
 */
public class NoLoginException extends Exception{

    public NoLoginException() {
    }

    public NoLoginException(String message) {
        super(message);
    }
}
