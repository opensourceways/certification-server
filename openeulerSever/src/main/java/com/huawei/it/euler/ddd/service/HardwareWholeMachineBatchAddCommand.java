/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 硬件-整机 PO对象
 *
 * @author zhaoyan
 * @since 2024-09-30
 */
@Tag(name = "新增整机命令", description = "用于存储新增命令字段数据")
@Data
public class HardwareWholeMachineBatchAddCommand {

    @Schema(description = "硬件厂商-中文")
    @Size(max = 500, message = "硬件厂商-中文长度不能超过500")
    private String hardwareFactoryZy;

    @Schema(description = "硬件厂商-英文")
    @Size(max = 500, message = "硬件厂商-英文长度不能超过500")
    private String hardwareFactoryEn;

    @Schema(description = "硬件型号")
    @Size(max = 50, message = "硬件型号长度不能超过50")
    private String hardwareModel;

    @Schema(description = "操作系统版本")
    @Size(max = 50, message = "操作系统版本长度不能超过50")
    private String osVersion;

    @Schema(description = "CPU架构")
    @Size(max = 20, message = "CPU架构长度不能超过20")
    private String architecture;

    @Schema(description = "认证日期")
    @Size(max = 10, message = "认证日期长度不能超过10")
    private String date;

    @Schema(description = "友情链接")
    @Size(max = 255, message = "友情链接长度不能超过255")
    private String friendlyLink;

    @Valid
    private HardwareCompatibilityConfigurationAddCommand compatibilityConfiguration;

    @Schema(description = "密级")
    @Size(max = 10, message = "密级长度不能超过10")
    private String securityLevel;

    @Valid
    private List<HardwareBoardCardAddCommand> boardCards;


}
