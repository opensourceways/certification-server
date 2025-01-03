/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import lombok.Data;

import java.io.Serializable;

/**
 * 硬件-整机 兼容性配置
 *
 * @author zhaoyan
 * @since 2024-09-30
 */
@Data
public class HardwareCompatibilityConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 服务器产品信息链接
     */
    private String productInformation;

    /**
     * 认证日期
     */
    private String certificationTime;

    /**
     * 适配commit-id
     */
    private String commitID;

    /**
     * 主板型号
     */
    private String mainboardModel;

    /**
     * bios/UEFI版本
     */
    private String biosUefi;

    /**
     * CPU型号
     */
    private String cpu;

    /**
     * 内存条配置信息
     */
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
     * 硬盘驱动-中文
     */
    private String hardDiskDrive;

    /**
     * 硬盘驱动-英文
     */
    private String hardDiskDriveEn;

}
