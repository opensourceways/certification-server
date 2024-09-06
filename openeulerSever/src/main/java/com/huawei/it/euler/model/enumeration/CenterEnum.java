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
 * 创新中心枚举类
 *
 * @since 2024/06/29
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CenterEnum {
    // openEuler社区
    USER(1, "openEuler社区"),
    // 江苏鲲鹏&欧拉生态创新中心
    CHINA_REGION(2, "江苏鲲鹏&欧拉生态创新中心"),
    // 四川鲲鹏&欧拉生态创新中心
    SIG_GROUP(4, "四川鲲鹏&欧拉生态创新中心"),
    // 长江鲲鹏&欧拉生态创新中心
    EULER_IC(5, "长江鲲鹏&欧拉生态创新中心"),
    // 湖南欧拉生态创新中心
    FLAG_STORE(6, "湖南欧拉生态创新中心"),
    // 北京欧拉生态创新中心
    ADMIN(7, "北京欧拉生态创新中心"),
    // 开放原子-英特尔联合验证中心
    OSV_USER(8, "开放原子-英特尔联合验证中心");

    private final Integer id; // id

    private final String name; // 中心名称

    CenterEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(CenterEnum.class);

    private static final Map<Integer, CenterEnum> CENTER_ENUM_MAP = new HashMap<>();

    static {
        for (CenterEnum status : values()) {
            CENTER_ENUM_MAP.put(status.getId(), status);
        }
    }

    public static String findById(int id) {
        CenterEnum centerEnum = CENTER_ENUM_MAP.get(id);
        if (centerEnum == null) {
            LOGGER.error("Invalid Status id: {}", id);
            return String.valueOf(id);
        }
        return centerEnum.getName();
    }

    public static Integer findByName(String name) {
        for (CenterEnum centerEnum : values()) {
            if (centerEnum.getName().equals(name)) {
                return centerEnum.getId();
            }
        }
        LOGGER.error("Invalid Status name: {}", name);
        return null;
    }

    public static List<CenterEnum> getAllCenters() {
        return Arrays.asList(values());
    }
}
