/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.notice.primitive;

import lombok.Getter;

/**
 * 发送方式枚举
 *
 * @author zhaoyan
 * @since 2024-12-19
 */
@Getter
public enum SendType {
    PHONE("phone", "手机"),
    EMAIL("email", "邮箱"),
    KAFKA("kafka", "kafka消息中心");

    private final String value;

    private final String desc;

    SendType(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
