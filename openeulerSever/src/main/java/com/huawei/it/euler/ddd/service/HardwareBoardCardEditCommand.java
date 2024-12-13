/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Tag(name = "编辑板卡命令", description = "用于存储编辑命令字段数据")
@Data
public class HardwareBoardCardEditCommand {

    @Schema(description = "主键id")
    private Integer id = 0;

    @Schema(description = "板卡四元组vendor id")
    @Size(max = 50, message = "板卡四元组vendor id长度不能超过50")
    private String vendorID;

    @Schema(description = "板卡四元组device id")
    @Size(max = 50, message = "板卡四元组device id长度不能超过50")
    private String deviceID;

    @Schema(description = "板卡四元组sv id")
    @Size(max = 50, message = "板卡四元组sv id长度不能超过50")
    private String svID;

    @Schema(description = "板卡四元组ss id")
    @Size(max = 50, message = "板卡四元组ss id长度不能超过50")
    private String ssID;

    @Schema(description = "CPU架构")
    @Size(max = 10, message = "CPU架构长度不能超过10")
    private String architecture;

    @Schema(description = "适配的操作系统版本")
    @Size(max = 50, message = "适配的操作系统版本长度不能超过50")
    private String os;

    @Schema(description = "驱动名称")
    @Size(max = 50, message = "驱动名称长度不能超过50")
    private String driverName;

    @Schema(description = "驱动版本")
    @Size(max = 50, message = "驱动版本长度不能超过50")
    private String version;

    @Schema(description = "驱动大小")
    @Size(max = 20, message = "驱动大小长度不能超过20")
    private String driverSize;

    @Schema(description = "驱动的sha256")
    @Size(max = 260, message = "驱动的sha256长度不能超过260")
    private String sha256;

    @Schema(description = "驱动下载链接；内核驱动，填写inbox")
    @Size(max = 255, message = "驱动下载链接长度不能超过255")
    private String downloadLink;

    @Schema(description = "板卡类型")
    @Size(max = 20, message = "板卡类型长度不能超过20")
    private String type;

    @Schema(description = "认证日期")
    @Size(max = 10, message = "认证日期长度不能超过10")
    private String date;

    @Schema(description = "芯片厂商")
    @Size(max = 100, message = "芯片厂商长度不能超过100")
    private String chipVendor;

    @Schema(description = "芯片型号")
    @Size(max = 50, message = "芯片型号长度不能超过50")
    private String chipModel;

    @Schema(description = "板卡型号")
    @Size(max = 50, message = "板卡型号长度不能超过50")
    private String boardModel;

    @Schema(description = "编码")
    @Size(max = 50, message = "编码长度不能超过50")
    private String item;

    @Schema(description = "密级")
    @Size(max = 10, message = "密级长度不能超过10")
    private String securityLevel;

}
