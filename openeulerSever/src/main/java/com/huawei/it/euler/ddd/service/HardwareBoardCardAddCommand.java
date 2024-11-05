/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Tag(name = "新增板卡命令", description = "用于存储新增命令字段数据")
@Data
public class HardwareBoardCardAddCommand {

    @Schema(name = "vendorID", description = "板卡四元组vendor id")
    @NotNull(message = "板卡四元组vendorID不能为空")
    private String vendorID;

    @Schema(name = "deviceID", description = "板卡四元组device id")
    @NotNull(message = "板卡四元组deviceID不能为空")
    private String deviceID;

    @Schema(name = "svID", description = "板卡四元组sv id")
    @NotNull(message = "板卡四元组svID不能为空")
    private String svID;

    @Schema(name = "ssID", description = "板卡四元组ss id")
    @NotNull(message = "板卡四元组ssID不能为空")
    private String ssID;

    @Schema(name = "architecture", description = "CPU架构")
    @NotNull(message = "架构不能为空")
    private String architecture;

    @Schema(name = "os", description = "适配的操作系统版本")
    @NotNull(message = "适配的操作系统版本不能为空")
    private String os;

    @Schema(name = "driverName", description = "驱动名称")
    @NotNull(message = "驱动名称不能为空")
    private String driverName;

    @Schema(name = "version", description = "驱动版本")
    @NotNull(message = "驱动版本不能为空")
    private String version;

    @Schema(name = "driverSize", description = "驱动大小")
    @NotNull(message = "驱动大小不能为空")
    private String driverSize;

    @Schema(name = "sha256", description = "驱动的sha256")
    @NotNull(message = "驱动的sha256不能为空")
    private String sha256;

    @Schema(name = "downloadLink", description = "驱动下载链接；内核驱动，填写inbox")
    @NotNull(message = "驱动下载链接不能为空")
    private String downloadLink;

    @Schema(name = "type", description = "板卡类型")
    @NotNull(message = "板卡类型不能为空")
    private String type;

    @Schema(name = "date", description = "认证日期")
    @NotNull(message = "认证日期不能为空")
    private String date;

    @Schema(name = "chipVendor", description = "芯片厂商")
    @NotNull(message = "芯片厂商不能为空")
    private String chipVendor;

    @Schema(name = "chipModel", description = "芯片型号")
    @NotNull(message = "芯片型号不能为空")
    private String chipModel;

    @Schema(name = "boardModel", description = "板卡型号")
    @NotNull(message = "板卡型号不能为空")
    private String boardModel;

    @Schema(name = "item", description = "编码")
    private String item;

    @Schema(name = "securityLevel", description = "密级")
    @NotNull(message = "密级不能为空")
    private String securityLevel;

}
