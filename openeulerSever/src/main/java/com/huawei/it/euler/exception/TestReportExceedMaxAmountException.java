/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.exception;

/**
 * 测试报告超过最大个数限制异常
 *
 * @since 2024/07/01
 */
public class TestReportExceedMaxAmountException extends Exception{
    private static final long serialVersionUID = -6852414824137843767L;

    public TestReportExceedMaxAmountException(String message) {
        super(message);
    }
}
