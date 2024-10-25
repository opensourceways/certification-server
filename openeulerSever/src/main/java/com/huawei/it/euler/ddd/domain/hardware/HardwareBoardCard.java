/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.Date;

@Data
public class HardwareBoardCard {

    private Integer id;

    /**
     * 板卡四元组vendor id
     */
    private String vendorID;

    /**
     * 板卡四元组device id
     */
    private String deviceID;

    /**
     * 板卡四元组sv id
     */
    private String svID;

    /**
     * 板卡四元组ss id
     */
    private String ssID;

    /**
     * CPU架构
     */
    private String architecture;

    /**
     * 操作系统版本
     */
    private String os;

    /**
     * 驱动名称
     */
    private String driverName;

    /**
     * 驱动版本
     */
    private String version;

    /**
     * 驱动大小
     */
    private String driverSize;

    /**
     * 驱动的sha256
     */
    private String sha256;

    /**
     * 驱动下载链接；内核驱动，填写inbox
     */
    private String downloadLink;

    /**
     * 板卡类型
     */
    private String type;

    /**
     * 认证日期
     */
    private String date;

    /**
     * 芯片厂商
     */
    private String chipVendor;

    /**
     * 芯片型号
     */
    private String chipModel;

    /**
     * 板卡型号
     */
    private String boardModel;

    /**
     * 编码
     */
    private String item;

    /**
     * 关联整机数量
     */
    private int refCount;

    /**
     * 状态
     */
    private String status;

    /**
     * 申请人uuid
     */
    private String userUuid;

    /**
     * 申请时间
     */
    private Date applyTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    public HardwareBoardCard create() {
        this.setStatus(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        this.setApplyTime(new Date());
        return this;
    }

    public HardwareBoardCard delete() {
        this.setStatus(HardwareValueEnum.NODE_DELETE.getValue());
        this.setUpdateTime(new Date());
        return this;
    }

    public HardwareBoardCard apply() {
        this.setStatus(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());
        this.setUpdateTime(new Date());
        return this;
    }

    public HardwareBoardCard pass() {
        this.setStatus(HardwareValueEnum.NODE_PASS.getValue());
        this.setUpdateTime(new Date());
        return this;
    }

    public HardwareBoardCard reject() {
        this.setStatus(HardwareValueEnum.NODE_REJECT.getValue());
        this.setUpdateTime(new Date());
        return this;
    }

    public HardwareBoardCard close() {
        this.setStatus(HardwareValueEnum.NODE_CLOSE.getValue());
        this.setUpdateTime(new Date());
        return this;
    }

    public String toSimpleJsonString() {
        JSONObject simple = new JSONObject();
        simple.put("os", this.getOs());
        simple.put("architecture", this.getArchitecture());
        simple.put("boardModel", this.getBoardModel());
        simple.put("chipModel", this.getChipModel());
        return simple.toJSONString();
    }
}
