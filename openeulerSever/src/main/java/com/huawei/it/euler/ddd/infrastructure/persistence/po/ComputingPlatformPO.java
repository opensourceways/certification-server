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
 * 算力平台 PO 对象
 *
 * @author zhaoyan
 * @since 2024-11-13
 */
@Data
@TableName("computing_platform_t")
public class ComputingPlatformPO {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 算力平台
     */
    @TableField(value = "platform_name")
    private String platformName;

    /**
     * 服务厂家
     */
    @TableField(value = "server_provider")
    private String serverProvider;

    /**
     * 服务器类型
     */
    @TableField(value = "server_type")
    private String serverType;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date lastUpdatedTime;

    /**
     * 最后更新人
     */
    @TableField(value = "update_by")
    private String lastUpdatedBy;
}
