/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 硬件-枚举
 *
 * @author zhaoyan
 * @since 2024-10-08
 */
@Slf4j
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
    RESULT_CLOSE("3", "关闭"),
    RESULT_DELETE("4", "删除"),
    RESULT_OFFLINE("5", "下架"),
    /**
     * 硬件数据审批节点枚举
     */
    NODE_WAIT_APPLY("1", "数据提交"),
    NODE_WAIT_APPROVE("2", "数据审核"),
    NODE_PASS("3", "已通过"),
    NODE_TEMP("0", "临时数据"),
    NODE_DELETE("-1", "已删除"),
    NODE_CLOSE("-2", "已关闭"),
    NODE_REJECT("-3", "已驳回"),
    NODE_OFFLINE("-4", "已下架");

    private final String value;

    private final String text;

    HardwareValueEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }

    private static final Map<String, HardwareValueEnum> VALUE_MAP = new HashMap<>();

    static {
        for (HardwareValueEnum valueEnum : values()) {
            VALUE_MAP.put(valueEnum.getValue(), valueEnum);
        }
    }
    public static String getTextByValue(String value){
        HardwareValueEnum valueEnum = VALUE_MAP.get(value);
        if (valueEnum == null) {
            log.error("Invalid value: {}", value);
            return value;
        }
        return valueEnum.getText();
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
        activeStatusList.add(NODE_REJECT.value);
        return activeStatusList;
    }

    /**
     * 有效板卡数据节点状态
     *
     * @return 有效数据节点状态集合
     */
    public static List<String> activeBoardCardStatusList() {
        List<String> activeStatusList = new ArrayList<>();
        activeStatusList.add(NODE_TEMP.value);
        activeStatusList.add(NODE_WAIT_APPLY.value);
        activeStatusList.add(NODE_WAIT_APPROVE.value);
        activeStatusList.add(NODE_PASS.value);
        activeStatusList.add(NODE_REJECT.value);
        return activeStatusList;
    }
}
