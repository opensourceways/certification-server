/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.permission;

import java.util.Date;

/**
 * 用户角色 实体对象
 *
 * @author zhaoyan
 * @since 2024-11-07
 */
public class UserRole {
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 角色值
     */
    private Integer roleId;

    /**
     * 数据范围
     */
    private Integer dataScope;

    /**
     * 用户uuid
     */
    private String uuid;

    /**
     * 更新时间
     */
    private Date lastUpdatedTime;

    /**
     * 最后更新人
     */
    private Integer lastUpdatedBy;
}
