/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.enumeration;

import lombok.Getter;

import java.util.Objects;

/**
 * 协议枚举类
 *
 * @since 2024/06/29
 */
@Getter
public enum ProtocolEnum {
    PRIVACY_POLICY(1, "隐私政策"),
    TECHNICAL_EVALUATION_AGREEMENT(2, "技术测评协议"),
    COMPATIBILITY_LIST_USAGE_STATEMENT(3, "兼容性清单使用声明");

    private final Integer protocolType; // 协议类型

    private final String protocolName; // 协议名称

    ProtocolEnum(Integer protocolType, String protocolName) {
        this.protocolType = protocolType;
        this.protocolName = protocolName;
    }

    /**
     * 根据协议类型返回协议名称
     *
     * @param protocolType 协议类型
     * @return 协议名称
     */
    public static String getProtocolNameByType(Integer protocolType) {
        for (ProtocolEnum protocolEnum : ProtocolEnum.values()) {
            if (Objects.equals(protocolEnum.getProtocolType(), protocolType)) {
                return protocolEnum.getProtocolName();
            }
        }
        return null;
    }
}
