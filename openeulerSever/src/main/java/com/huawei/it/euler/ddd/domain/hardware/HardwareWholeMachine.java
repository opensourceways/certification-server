/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

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
    private String HardwareFactory;

    /**
     * 硬件型号
     */
    private String HardwareModel;

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
        this.setStatus(HardwareStatus.WAIT_APPLY.getStatus());
        this.setApplyTime(new Date());
        return this;
    }

    public HardwareWholeMachine apply() {
        this.setStatus(HardwareStatus.WAIT_APPROVE.getStatus());
        this.setUpdateTime(new Date());
        return this;
    }

    public HardwareWholeMachine pass() {
        this.setStatus(HardwareStatus.PASS.getStatus());
        this.setUpdateTime(new Date());
        return this;
    }

    public HardwareWholeMachine reject() {
        this.setStatus(HardwareStatus.REJECT.getStatus());
        this.setUpdateTime(new Date());
        return this;
    }
}
