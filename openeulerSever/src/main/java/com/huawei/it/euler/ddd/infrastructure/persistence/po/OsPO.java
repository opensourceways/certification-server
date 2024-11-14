/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 操作系统 PO 对象
 *
 * @author zhaoyan
 * @since 2024-11-14
 */
@Data
@TableName("os_t")
public class OsPO {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 系统名称
     */
    @TableField(value = "os_name")
    private String osName;

    /**
     * 系统版本
     */
    @TableField(value = "os_version")
    private String osVersion;

    /**
     * 对应欧拉版本
     */
    @TableField(value = "related_os_version")
    private String relatedOsVersion;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date LastUpdatedTime;

    /**
     * 最后更新人
     */
    @TableField(value = "update_by")
    private String LastUpdatedBy;
}
