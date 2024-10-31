/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * 硬件-整机 PO对象
 *
 * @author zhaoyan
 * @since 2024-09-30
 */
@Getter
@Setter
@TableName("hardware_whole_machine_t")
public class HardwareWholeMachinePO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 硬件厂商-中文
     */
    @TableField("factory_zy")
    private String factoryZy;

    /**
     * 硬件厂商-英文
     */
    @TableField("factory_en")
    private String factoryEn;

    /**
     * 硬件型号
     */
    @TableField("model")
    private String hardwareModel;

    /**
     * 操作系统版本
     */
    @TableField("os_version")
    private String osVersion;

    /**
     * CPU架构
     */
    @TableField("architecture")
    private String architecture;

    /**
     * 认证日期
     */
    @TableField("date")
    private String date;

    /**
     * 友情链接
     */
    @TableField("friendly_link")
    private String friendlyLink;

    /**
     * 板卡信息id集合
     */
    @TableField("board_card_ids")
    private String boardCardIds;

    /**
     * 服务器产品信息链接
     */
    @TableField("product_information")
    private String productInformation;

    /**
     * 认证日期
     */
    @TableField("certification_time")
    private String certificationTime;

    /**
     * 适配commit-id
     */
    @TableField("commit_id")
    private String commitID;

    /**
     * 主板型号
     */
    @TableField("mainboard_model")
    private String mainboardModel;

    /**
     * bios/UEFI版本
     */
    @TableField("bios_uefi")
    private String biosUefi;

    /**
     * CPU型号
     */
    @TableField("cpu")
    private String cpu;

    /**
     * 内存条配置信息
     */
    @TableField("ram")
    private String ram;

    /**
     * 端口类型
     */
    @TableField("ports_bus_types")
    private String portsBusTypes;

    /**
     * 视频适配器
     */
    @TableField("video_adapter")
    private String videoAdapter;

    /**
     * 整机总线适配器
     */
    @TableField("host_bus_adapter")
    private String hostBusAdapter;

    /**
     * 硬盘驱动
     */
    @TableField("hard_disk_drive")
    private String hardDiskDrive;

    /**
     * 密级
     */
    @TableField("security_level")
    private String securityLevel;

    /**
     * 申请人uuid
     */
    @TableField("user_uuid")
    private String userUuid;

    /**
     * 状态
     */
    @TableField("status")
    private String status;

    /**
     * 申请时间
     */
    @TableField("apply_time")
    private Date applyTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;
}
