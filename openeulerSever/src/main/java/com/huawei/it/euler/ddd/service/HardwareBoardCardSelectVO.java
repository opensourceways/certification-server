/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 硬件-板卡 筛选VO对象
 *
 * @author zhaoyan
 * @since 2024-09-30
 */
@Tag(name = "板卡查询对象", description = "用于存储查询命令字段数据")
@Data
public class HardwareBoardCardSelectVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "板卡id")
    private String id;

    @Schema(description = "id集合")
    private List<String> idList;

    @Schema(description = "关键字")
    private String keyword;

    @Schema(description = "CPU架构")
    private String architecture;

    @Schema(description = "操作系统版本")
    private String os;

    @Schema(description = "驱动名称")
    private String driverName;

    @Schema(description = "板卡类型")
    private String type;

    @Schema(description = "认证日期")
    private String date;

    @Schema(description = "芯片厂商")
    private String chipVendor;

    @Schema(description = "芯片型号")
    private String chipModel;

    @Schema(description = "板卡型号")
    private String boardModel;

    @Schema(description = "申请人uuid")
    private String userUuid;

    @Schema(description = "密级")
    private String securityLevel;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "状态集合")
    private List<String> statusList;

    @Schema(description = "申请时间")
    private Date applyTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "请求页码")
    private int current;

    @Schema(description = "请求页数量")
    private int size;

    private String sortType;
}
