/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.util;

/**
 * 日志处理工具类
 *
 * @since 2024/07/01
 */
public class CleanXSSUtils {
    /**
     * 外部参数要打印日志需做处理
     *
     * @param message
     * @return
     */
    public static String replaceCRLF(String message) {
        if (message == null) {
            return "";
        }
        return message.replace('\n', '_').replace('\r', '_').replace('\t', '_');
    }
}
