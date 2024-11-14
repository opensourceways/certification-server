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
 * 产品类型 PO对象
 *
 * @author zhaoyan
 * @since 2024-11-14
 */
@Data
@TableName("product_t")
public class ProductPO {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 产品类型
     */
    @TableField(value = "product_type")
    private String productType;

    /**
     * 产品类型子类型
     */
    @TableField(value = "product_children_type")
    private String productChildrenType;

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
