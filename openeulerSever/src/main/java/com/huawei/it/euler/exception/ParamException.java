/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.exception;

/**
 * 参数异常
 *
 * @since 2024/06/29
 */
public class ParamException extends RuntimeException{
    private static final long serialVersionUID = 1377969936371913080L;

    public ParamException(String message) {
        super(message);
    }
}
