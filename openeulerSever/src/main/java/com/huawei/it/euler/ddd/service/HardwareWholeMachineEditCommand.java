/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

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
@Data
public class HardwareWholeMachineEditCommand {

    /**
     * 主键id
     */
    @NotNull(message = "主键id不能为空")
    private Integer id;

    /**
     * 硬件厂商-中文
     */
    @NotNull(message = "整机厂商中文名称不能为空")
    private String hardwareFactoryZy;

    /**
     * 硬件厂商-英文
     */
    @NotNull(message = "整机厂商英文名称不能为空")
    private String hardwareFactoryEn;

    /**
     * 硬件型号
     */
    @NotNull(message = "整机型号不能为空")
    private String hardwareModel;

    /**
     * 操作系统版本
     */
    @NotNull(message = "适配的操作系统版本不能为空")
    private String osVersion;

    /**
     * CPU架构
     */
    @NotNull(message = "架构不能为空")
    private String architecture;

    /**
     * 认证日期
     */
    @NotNull(message = "认证日期不能为空")
    private String date;

    /**
     * 友情链接
     */
    private String friendlyLink;

    /**
     * 服务器产品信息链接
     */
    @NotNull(message = "服务器产品链接不能为空")
    private String productInformation;

    /**
     * 认证日期
     */
    @NotNull(message = "认证日期不能为空")
    private String certificationTime;

    /**
     * 适配commit-id
     */
    private String commitID;

    /**
     * 主板型号
     */
    @NotNull(message = "主板型号不能为空")
    private String mainboardModel;

    /**
     * bios/UEFI版本
     */
    @NotNull(message = "bios/UEFI版本不能为空")
    private String biosUefi;

    /**
     * CPU型号
     */
    @NotNull(message = "CPU型号不能为空")
    private String cpu;

    /**
     * 内存条配置信息
     */
    @NotNull(message = "内存条配置信息不能为空")
    private String ram;

    /**
     * 端口类型
     */
    private String portsBusTypes;

    /**
     * 视频适配器
     */
    private String videoAdapter;

    /**
     * 整机总线适配器
     */
    private String hostBusAdapter;

    /**
     * 硬盘驱动
     */
    private String hardDiskDrive;

    /**
     * 密级
     */
    @NotNull(message = "密级不能为空")
    private String securityLevel;

    @Valid
    @NotNull(message = "板卡信息不能为空")
    private List<HardwareBoardCardEditCommand> boardCardEditCommandList;
}
