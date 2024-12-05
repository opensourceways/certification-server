/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import com.huawei.it.euler.util.ObjectUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Tag(name = "新增板卡命令", description = "用于存储新增命令字段数据")
@Data
public class HardwareBoardCardAddCommand {

    @Schema(name = "vendorID", description = "板卡四元组vendor id")
//    @NotNull(message = "板卡四元组vendorID不能为空")
    @Size(max = 50, message = "板卡四元组vendor id长度不能超过50")
    private String vendorID;

    @Schema(name = "deviceID", description = "板卡四元组device id")
//    @NotNull(message = "板卡四元组deviceID不能为空")
    @Size(max = 50, message = "板卡四元组device id长度不能超过50")
    private String deviceID;

    @Schema(name = "svID", description = "板卡四元组sv id")
//    @NotNull(message = "板卡四元组svID不能为空")
    @Size(max = 50, message = "板卡四元组sv id长度不能超过50")
    private String svID;

    @Schema(name = "ssID", description = "板卡四元组ss id")
//    @NotNull(message = "板卡四元组ssID不能为空")
    @Size(max = 50, message = "板卡四元组ss id长度不能超过50")
    private String ssID;

    @Schema(name = "architecture", description = "CPU架构")
//    @NotNull(message = "架构不能为空")
    @Size(max = 10, message = "CPU架构长度不能超过10")
    private String architecture;

    @Schema(name = "os", description = "适配的操作系统版本")
//    @NotNull(message = "适配的操作系统版本不能为空")
    @Size(max = 50, message = "适配的操作系统版本长度不能超过50")
    private String os;

    @Schema(name = "driverName", description = "驱动名称")
//    @NotNull(message = "驱动名称不能为空")
    @Size(max = 50, message = "驱动名称长度不能超过50")
    private String driverName;

    @Schema(name = "version", description = "驱动版本")
//    @NotNull(message = "驱动版本不能为空")
    @Size(max = 20, message = "驱动版本长度不能超过20")
    private String version;

    @Schema(name = "driverSize", description = "驱动大小")
//    @NotNull(message = "驱动大小不能为空")
    @Size(max = 20, message = "驱动大小长度不能超过20")
    private String driverSize;

    @Schema(name = "sha256", description = "驱动的sha256")
//    @NotNull(message = "驱动的sha256不能为空")
    @Size(max = 64, message = "驱动的sha256长度不能超过64")
    private String sha256;

    @Schema(name = "downloadLink", description = "驱动下载链接；内核驱动，填写inbox")
//    @NotNull(message = "驱动下载链接不能为空")
    @Size(max = 255, message = "驱动下载链接长度不能超过255")
    private String downloadLink;

    @Schema(name = "type", description = "板卡类型")
//    @NotNull(message = "板卡类型不能为空")
    @Size(max = 10, message = "板卡类型长度不能超过10")
    private String type;

    @Schema(name = "date", description = "认证日期")
//    @NotNull(message = "认证日期不能为空")
    @Size(max = 10, message = "认证日期长度不能超过10")
    private String date;

    @Schema(name = "chipVendor", description = "芯片厂商")
//    @NotNull(message = "芯片厂商不能为空")
    @Size(max = 10, message = "芯片厂商长度不能超过10")
    private String chipVendor;

    @Schema(name = "chipModel", description = "芯片型号")
//    @NotNull(message = "芯片型号不能为空")
    @Size(max = 50, message = "芯片型号长度不能超过50")
    private String chipModel;

    @Schema(name = "boardModel", description = "板卡型号")
//    @NotNull(message = "板卡型号不能为空")
    @Size(max = 50, message = "板卡型号长度不能超过50")
    private String boardModel;

    @Schema(name = "item", description = "编码")
    @Size(max = 50, message = "编码长度不能超过50")
    private String item;

    @Schema(name = "securityLevel", description = "密级")
//    @NotNull(message = "密级不能为空")
    @Size(max = 10, message = "密级长度不能超过10")
    private String securityLevel;

    public boolean canCreated(){
        return !ObjectUtil.checkFieldAllNull(this);
    }

}
