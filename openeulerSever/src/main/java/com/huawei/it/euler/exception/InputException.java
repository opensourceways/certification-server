/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.exception;

/**
 * InputException
 *
 * @since 2024/07/01
 */
public class InputException extends Exception{
    private static final long serialVersionUID = -6007240920902498448L;

    public InputException() {}

    public InputException(String message) {
        super(message);
    }
}
