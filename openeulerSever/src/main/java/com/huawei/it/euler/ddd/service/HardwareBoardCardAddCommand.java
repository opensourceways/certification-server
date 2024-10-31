/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HardwareBoardCardAddCommand {

    /**
     * 板卡四元组vendor id
     */
    @NotNull(message = "板卡四元组vendorID不能为空")
    private String vendorID;

    /**
     * 板卡四元组device id
     */
    @NotNull(message = "板卡四元组deviceID不能为空")
    private String deviceID;

    /**
     * 板卡四元组sv id
     */
    @NotNull(message = "板卡四元组svID不能为空")
    private String svID;

    /**
     * 板卡四元组ss id
     */
    @NotNull(message = "板卡四元组ssID不能为空")
    private String ssID;

    /**
     * CPU架构
     */
    @NotNull(message = "架构不能为空")
    private String architecture;

    /**
     * 适配的操作系统版本
     */
    @NotNull(message = "适配的操作系统版本不能为空")
    private String os;

    /**
     * 驱动名称
     */
    @NotNull(message = "驱动名称不能为空")
    private String driverName;

    /**
     * 驱动版本
     */
    @NotNull(message = "驱动版本不能为空")
    private String version;

    /**
     * 驱动大小
     */
    @NotNull(message = "驱动大小不能为空")
    private String driverSize;

    /**
     * 驱动的sha256
     */
    @NotNull(message = "驱动的sha256不能为空")
    private String sha256;

    /**
     * 驱动下载链接；内核驱动，填写inbox
     */
    @NotNull(message = "驱动下载链接不能为空")
    private String downloadLink;

    /**
     * 板卡类型
     */
    @NotNull(message = "板卡类型不能为空")
    private String type;

    /**
     * 认证日期
     */
    @NotNull(message = "认证日期不能为空")
    private String date;

    /**
     * 芯片厂商
     */
    @NotNull(message = "芯片厂商不能为空")
    private String chipVendor;

    /**
     * 芯片型号
     */
    @NotNull(message = "芯片型号不能为空")
    private String chipModel;

    /**
     * 板卡型号
     */
    @NotNull(message = "板卡型号不能为空")
    private String boardModel;

    /**
     * 编码
     */
    private String item;

    /**
     * 密级
     */
    @NotNull(message = "密级不能为空")
    private String securityLevel;

}
