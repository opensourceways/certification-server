/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.enumeration;

import lombok.Getter;

/**
 * 处理结果枚举类
 *
 * @since 2024/06/29
 */
@Getter
public enum HandlerResultEnum {
    // pending
    PENDING(0, "待处理"),
    // accept
    ACCEPT(1, "通过"),
    // reject
    REJECT(2, "驳回"),
    // transfer
    TRANSFER(3, "转审"),
    // withdraw
    WITHDRAW(4, "撤销"),
    // void
    VOID(5, "作废");

    private final Integer id; // id

    private final String name; // 中心名称

    HandlerResultEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public static String findById(Integer id) {
        for (HandlerResultEnum handlerResultEnum : HandlerResultEnum.values()) {
            if (handlerResultEnum.getId().equals(id)) {
                return handlerResultEnum.getName();
            }
        }
        return null;
    }

    public static boolean isValidId(Integer id) {
        for (HandlerResultEnum handlerResultEnum : HandlerResultEnum.values()) {
            if (handlerResultEnum.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
