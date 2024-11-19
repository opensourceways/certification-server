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
@Tag(name = "兼容列表整机列表对象", description = "兼容列表整机列表对象")
@Data
public class HardwareWholeMachineListDto {
    @Schema(description = "整机id")
    private Integer id;

    @Schema(description = "整机厂商-中文")
    private String hardwareFactoryZy;

    @Schema(description = "硬件厂商-英文")
    private String hardwareFactoryEn;

    @Schema(description = "硬件型号")
    private String hardwareModel;

    @Schema(description = "操作系统")
    private String osVersion;

    @Schema(description = "架构")
    private String architecture;

    @Schema(description = "CPU")
    private String cpu;

    @Schema(description = "认证日期")
    private String date;

    @Schema(description = "友情链接")
    private String friendlyLink;
}
