/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.interfaces.request;

import lombok.Data;

import java.util.Date;

@Data
public class UserRoleDTO {
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 角色值
     */
    private Integer role;

    /**
     * 角色名称
     */
    private String roleName;

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
    private Date LastUpdatedTime;

    /**
     * 最后更新人
     */
    private Integer LastUpdatedBy;
}
