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
@Tag(name = "编辑整机命令", description = "用于存储编辑命令字段数据")
@Data
public class HardwareWholeMachineEditCommand {

    @Schema(description = "主键id")
    private Integer id;

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

    @Schema(description = "服务器产品信息链接")
    @Size(max = 255, message = "服务器产品信息链接长度不能超过255")
    private String productInformation;

    @Schema(description = "认证日期")
    @Size(max = 10, message = "认证日期长度不能超过10")
    private String certificationTime;

    @Schema(description = "适配commit-id")
    @Size(max = 255, message = "适配commit-id长度不能超过255")
    private String commitID;

    @Schema(description = "主板型号")
    @Size(max = 50, message = "主板型号长度不能超过50")
    private String mainboardModel;

    @Schema(description = "bios/UEFI版本")
    @Size(max = 50, message = "bios/UEFI版本长度不能超过50")
    private String biosUefi;

    @Schema(description = "CPU型号")
    @Size(max = 50, message = "CPU型号长度不能超过50")
    private String cpu;

    @Schema(description = "内存条配置信息")
    @Size(max = 50, message = "内存条配置信息长度不能超过50")
    private String ram;

    @Schema(description = "端口类型")
    @Size(max = 50, message = "端口类型长度不能超过50")
    private String portsBusTypes;

    @Schema(description = "视频适配器")
    @Size(max = 50, message = "视频适配器长度不能超过50")
    private String videoAdapter;

    @Schema(description = "整机总线适配器")
    @Size(max = 50, message = "整机总线适配器长度不能超过50")
    private String hostBusAdapter;

    @Schema(description = "硬盘驱动")
    @Size(max = 50, message = "硬盘驱动长度不能超过50")
    private String hardDiskDrive;

    @Schema(description = "密级")
    // @NotNull(message = "密级不能为空")
    @Size(max = 10, message = "密级长度不能超过10")
    private String securityLevel;

    @Valid
    private List<HardwareBoardCardEditCommand> boardCardEditCommandList;
}
