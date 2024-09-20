/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.software;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("software_display_t")
public class SoftwareDisplayPO {

    @TableId(type = IdType.AUTO)
    private int id;

    private String uuid;

    @TableField(value = "software_id")
    private int softwareId;

    @TableField(value = "product_name")
    private String productName;

}
