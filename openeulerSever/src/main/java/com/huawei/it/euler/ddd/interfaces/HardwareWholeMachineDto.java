/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.interfaces;

import com.huawei.it.euler.ddd.domain.hardware.HardwareCompatibilityConfiguration;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.util.List;

/**
 * @author zhaoyan
 * @since 2024-11-18
 */
@Tag(name = "兼容列表整机对象", description = "兼容列表整机对象")
@Data
public class HardwareWholeMachineDto {

    @Schema(description = "整机id")
    private Integer id;

    @Schema(description = "整机厂商-中文")
    private String hardwareFactoryZy;

    @Schema(description = "硬件厂商-英文")
    private String hardwareFactoryEn;

    @Schema(description = "硬件型号")
    private String hardwareModel;

    @Schema(description = "操作系统版本")
    private String osVersion;

    @Schema(description = "CPU架构")
    private String architecture;

    @Schema(description = "认证日期")
    private String date;

    @Schema(description = "友情链接")
    private String friendlyLink;

    @Schema(description = "板卡信息集合")
    private List<HardwareBoardCardDto> boardCards;

    @Schema(description = "兼容性配置")
    private HardwareCompatibilityConfiguration compatibilityConfiguration;
}
