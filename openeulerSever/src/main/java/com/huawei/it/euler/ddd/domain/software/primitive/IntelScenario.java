/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.software.primitive;

import lombok.Getter;

/**
 * 英特尔测评业务
 *
 * @author zhaoyan
 * @since 2024-12-26
 */
@Getter
public enum IntelScenario {
    CPU_VENDOR("英特尔", "英特尔厂商名称"),
    INNOVATION_ID("8", "英特尔测评机构id"),
    SCENARIO_ID("8", "英特尔测评场景id"),
    ROLE_ID("8", "英特尔测评角色id");

    private final String value;

    private final String desc;

    IntelScenario(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 判断是否为英特尔测评业务
     * @param asId 测评场景id
     * @return 判断结果
     */
    public static boolean isIntel(Integer asId){
        return SCENARIO_ID.getValue().equals(String.valueOf(asId));
    }
}
