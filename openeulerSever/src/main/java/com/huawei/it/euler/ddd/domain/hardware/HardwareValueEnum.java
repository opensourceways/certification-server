/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 硬件-枚举
 *
 * @author zhaoyan
 * @since 2024-10-08
 */
@Getter
public enum HardwareValueEnum {
    /**
     * 硬件数据类型枚举
     */
    TYPE_WHOLE_MACHINE("wholeMachine", "整机"),
    TYPE_BOARD_CARD("boardCard", "板卡"),
    /**
     * 硬件数据审批结果美剧
     */
    RESULT_PASS("1", "通过"),
    RESULT_REJECT("2", "驳回"),
    /**
     * 硬件数据审批节点枚举
     */
    NODE_WAIT_APPLY("1", "数据提交"),
    NODE_WAIT_APPROVE("2", "数据审核"),
    NODE_PASS("3", "已通过"),
    NODE_REJECT("-1", "已驳回");
    private final String value;

    private final String text;

    HardwareValueEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 有效数据节点状态
     *
     * @return 有效数据节点状态集合
     */
    public static List<String> activeStatusList() {
        List<String> activeStatusList = new ArrayList<>();
        activeStatusList.add(NODE_WAIT_APPLY.value);
        activeStatusList.add(NODE_WAIT_APPROVE.value);
        activeStatusList.add(NODE_PASS.value);
        return activeStatusList;
    }
}
