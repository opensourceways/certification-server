/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
public class HardwareWholeMachineAddCommand {

    @Schema(description = "硬件厂商-中文")
    // @NotNull(message = "整机厂商中文名称不能为空")
    private String hardwareFactoryZy;

    @Schema(description = "硬件厂商-英文")
    // @NotNull(message = "整机厂商英文名称不能为空")
    private String hardwareFactoryEn;

    @Schema(description = "硬件型号")
    // @NotNull(message = "整机型号不能为空")
    private String hardwareModel;

    @Schema(description = "操作系统版本")
    // @NotNull(message = "适配的操作系统版本不能为空")
    private String osVersion;

    @Schema(description = "CPU架构")
    // @NotNull(message = "架构不能为空")
    private String architecture;

    @Schema(description = "认证日期")
    // @NotNull(message = "认证日期不能为空")
    private String date;

    @Schema(description = "友情链接")
    private String friendlyLink;

    @Schema(description = "服务器产品信息链接")
    // @NotNull(message = "服务器产品链接不能为空")
    private String productInformation;

    @Schema(description = "认证日期")
    // @NotNull(message = "认证日期不能为空")
    private String certificationTime;

    @Schema(description = "适配commit-id")
    private String commitID;

    @Schema(description = "主板型号")
    // @NotNull(message = "主板型号不能为空")
    private String mainboardModel;

    @Schema(description = "bios/UEFI版本")
    // @NotNull(message = "bios/UEFI版本不能为空")
    private String biosUefi;

    @Schema(description = "CPU型号")
    // @NotNull(message = "CPU型号不能为空")
    private String cpu;

    @Schema(description = "内存条配置信息")
    // @NotNull(message = "内存条配置信息不能为空")
    private String ram;

    @Schema(description = "端口类型")
    private String portsBusTypes;

    @Schema(description = "视频适配器")
    private String videoAdapter;

    @Schema(description = "整机总线适配器")
    private String hostBusAdapter;

    @Schema(description = "硬盘驱动")
    private String hardDiskDrive;

    @Schema(description = "密级")
    // @NotNull(message = "密级不能为空")
    private String securityLevel;

    @Valid
    // @NotNull(message = "板卡信息不能为空")
    private List<HardwareBoardCardAddCommand> boardCardAddCommandList;
}
