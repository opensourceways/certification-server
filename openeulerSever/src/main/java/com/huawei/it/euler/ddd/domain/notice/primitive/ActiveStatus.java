/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.notice.primitive;

import lombok.Getter;

/**
 * 系统公告状态
 *
 * @author zhaoyan
 * @since 2024-12-24
 */
@Getter
public enum ActiveStatus {
    ACTIVE("y", "有效"),
    INACTIVE("n", "无效");

    private final String value;

    private final String desc;

    ActiveStatus(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
