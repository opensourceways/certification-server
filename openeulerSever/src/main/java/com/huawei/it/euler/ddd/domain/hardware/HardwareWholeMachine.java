/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 硬件-整机 PO对象
 *
 * @author zhaoyan
 * @since 2024-09-30
 */
@Data
public class HardwareWholeMachine implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 硬件厂商
     */
    private String hardwareFactory;

    /**
     * 硬件型号
     */
    private String hardwareModel;

    /**
     * 操作系统版本
     */
    private String osVersion;

    /**
     * CPU架构
     */
    private String architecture;

    /**
     * 认证日期
     */
    private String date;

    /**
     * 友情链接
     */
    private String friendlyLink;

    /**
     * 板卡信息集合
     */
    private List<HardwareBoardCard> boardCardList;

    /**
     * 兼容性配置
     */
    private HardwareCompatibilityConfiguration compatibilityConfiguration;

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

    public HardwareWholeMachine create(){
        this.setStatus(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        this.setApplyTime(new Date());
        return this;
    }

    public HardwareWholeMachine apply() {
        this.setStatus(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());
        this.setUpdateTime(new Date());
        return this;
    }

    public HardwareWholeMachine pass() {
        this.setStatus(HardwareValueEnum.NODE_PASS.getValue());
        this.setUpdateTime(new Date());
        return this;
    }

    public HardwareWholeMachine reject() {
        this.setStatus(HardwareValueEnum.NODE_REJECT.getValue());
        this.setUpdateTime(new Date());
        return this;
    }

    public String toSimpleJsonString() {
        JSONObject simple = new JSONObject();
        simple.put("hardwareFactory", this.getHardwareFactory());
        simple.put("hardwareModel", this.getHardwareModel());
        simple.put("osVersion", this.getOsVersion());
        simple.put("architecture", this.getArchitecture());
        simple.put("date", this.getDate());
        simple.put("status", this.getStatus());
        return simple.toJSONString();
    }
}
