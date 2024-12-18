/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.enumeration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;

/**
 * 审批节点枚举类
 *
 * @since 2024/06/29
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum NodeEnum {
    // 申请评测
    APPLY(1, "认证申请"),
    // 方案审核
    PROGRAM_REVIEW(2, "方案审核"),
    // 测试阶段
    TESTING_PHASE(3, "测试阶段"),
    // 报告初审
    REPORT_REVIEW(4, "报告初审"),
    // 报告复审
    REPORT_RE_REVIEW(5, "报告复审"),
    // 证书初审
    CERTIFICATE_REVIEW(6, "证书初审"),
    // 证书确认
    CERTIFICATE_CONFIRMATION(7, "证书确认"),
    // 证书签发
    CERTIFICATE_ISSUANCE(8, "证书签发"),
    // 完成
    FINISHED(9, "已完成"),;

    private final Integer id; // id

    private final String name; // 中心名称

    NodeEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeEnum.class);

    private static final Map<Integer, NodeEnum> NODE_ENUM_MAP = new HashMap<>();

    static {
        for (NodeEnum nodeEnum : values()) {
            NODE_ENUM_MAP.put(nodeEnum.getId(), nodeEnum);
        }
    }

    public static String findById(int id) {
        NodeEnum nodeEnum = NODE_ENUM_MAP.get(id);
        if (nodeEnum == null) {
            LOGGER.error("Invalid Status id: {}", id);
            return String.valueOf(id);
        }
        return nodeEnum.getName();
    }


    public static String findByName(String name) {
        for (NodeEnum nodeEnum : values()) {
            if (nodeEnum.getName().equals(name)) {
                return String.valueOf(nodeEnum.getId());
            }
        }
        LOGGER.error("Invalid Status name: {}", name);
        return name;
    }

    public static List<NodeEnum> getAllNodes() {
        return Arrays.asList(values());
    }
}
