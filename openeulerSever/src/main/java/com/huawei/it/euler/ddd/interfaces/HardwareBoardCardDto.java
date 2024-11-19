/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.interfaces;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

/**
 * @author zhaoyan
 * @since 2024-11-18
 */
@Tag(name = "兼容列表板卡对象", description = "兼容列表板卡对象")
@Data
public class HardwareBoardCardDto {

    @Schema(description = "板卡id")
    private Integer id;

    @Schema(description = "CPU架构")
    private String architecture;

    @Schema(description = "操作系统版本")
    private String os;

    @Schema(description = "驱动名称")
    private String driverName;

    @Schema(description = "驱动版本")
    private String version;

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
}
