/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 硬件-板卡 筛选VO对象
 *
 * @author zhaoyan
 * @since 2024-09-30
 */
@Tag(name = "整机查询对象", description = "用于存储查询命令字段数据")
@Data
public class HardwareWholeMachineSelectVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "id集合")
    private List<String> idList;

    @Schema(description = "硬件厂商")
    private String hardwareFactory;

    @Schema(description = "硬件型号")
    private String hardwareModel;

    @Schema(description = "操作系统版本")
    private String osVersion;

    @Schema(description = "CPU架构")
    private String architecture;

    @Schema(description = "CPU型号")
    private String cpu;

    @Schema(description = "认证日期")
    private String date;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "状态集合")
    private List<String> statusList;

    @Schema(description = "申请人uuid")
    private String userUuid;

    @Schema(description = "申请时间")
    private Date applyTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "请求页码")
    private int current;

    @Schema(description = "请求页数量")
    private int size;
}
