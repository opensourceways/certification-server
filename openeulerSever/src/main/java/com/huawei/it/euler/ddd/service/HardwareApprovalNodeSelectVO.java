/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.io.Serializable;

/**
 * 硬件审批操作记录表
 *
 * @author zhaoyan
 * @since 2024-10-09
 */
@Tag(name = "审批记录查询命令", description = "用于存储查询命令字段数据")
@Data
public class HardwareApprovalNodeSelectVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 硬件设备id
     */
    private Integer hardwareId;

    /**
     * 硬件设备类型:wholeMachine-整机；boardCard-板卡
     */
    private String hardwareType;

    /**
     * 操作节点：HardwareValueEnum-NODE*
     */
    private String handlerNode;

    /**
     * 操作节点名称：数据提交；数据审核；
     */
    private String handlerNodeName;

}
