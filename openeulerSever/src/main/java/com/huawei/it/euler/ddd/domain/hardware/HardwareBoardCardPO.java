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
 * 硬件-板卡 PO对象
 *
 * @author zhaoyan
 * @since 2024-09-30
 */
@Getter
@Setter
@TableName("hardware_board_card_t")
public class HardwareBoardCardPO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 板卡四元组vendor id
     */
    @TableField("vendor_id")
    private String vendorID;

    /**
     * 板卡四元组device id
     */
    @TableField("device_id")
    private String deviceID;

    /**
     * 板卡四元组sv id
     */
    @TableField("sv_id")
    private String svID;

    /**
     * 板卡四元组ss id
     */
    @TableField("ss_id")
    private String ssID;

    /**
     * CPU架构
     */
    @TableField("architecture")
    private String architecture;

    /**
     * 操作系统版本
     */
    @TableField("os")
    private String os;

    /**
     * 驱动名称
     */
    @TableField("driver_name")
    private String driverName;

    /**
     * 驱动版本
     */
    @TableField("version")
    private String version;

    /**
     * 驱动大小
     */
    @TableField("driver_size")
    private String driverSize;

    /**
     * 驱动的sha256
     */
    @TableField("sha256")
    private String sha256;

    /**
     * 驱动下载链接；内核驱动，填写inbox
     */
    @TableField("download_link")
    private String downloadLink;

    /**
     * 板卡类型
     */
    @TableField("type")
    private String type;

    /**
     * 认证日期
     */
    @TableField("date")
    private String date;

    /**
     * 芯片厂商
     */
    @TableField("chip_vendor")
    private String chipVendor;

    /**
     * 芯片型号
     */
    @TableField("chip_model")
    private String chipModel;

    /**
     * 板卡型号
     */
    @TableField("board_model")
    private String boardModel;

    /**
     * 编码
     */
    @TableField("item")
    private String item;

    /**
     * 关联整机数量
     */
    @TableField("ref_count")
    private int refCount;

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
