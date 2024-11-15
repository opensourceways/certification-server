/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.permission;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.Date;

/**
 * 用户角色 实体对象
 *
 * @author zhaoyan
 * @since 2024-11-07
 */
@Data
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
     * 数据范围名称
     */
    private String scopeName;

    /**
     * 用户uuid
     */
    private String uuid;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 更新时间
     */
    private Date lastUpdatedTime;

    /**
     * 最后更新人
     */
    private String lastUpdatedBy;

    public String toSimpleJsonString() {
        JSONObject simple = new JSONObject();
        simple.put("id", this.getId());
        simple.put("user", this.getUuid());
        simple.put("role", this.getRoleId());
        simple.put("scope", this.getDataScope());
        return simple.toJSONString();
    }
}
