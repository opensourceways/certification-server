/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import com.alibaba.fastjson.JSONObject;
import com.huawei.it.euler.exception.BusinessException;
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
     * 整机厂商-中文
     */
    private String hardwareFactoryZy;

    /**
     * 硬件厂商-英文
     */
    private String hardwareFactoryEn;

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
     * 整机信息id集合:1,2,3
     */
    private String boardCardIds;

    /**
     * 整机信息集合
     */
    private List<HardwareBoardCard> boardCards;

    /**
     * 兼容性配置
     */
    private HardwareCompatibilityConfiguration compatibilityConfiguration;

    /**
     * 密级
     */
    private String securityLevel;

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

    public HardwareWholeMachine create(String uuid) {
        this.setUserUuid(uuid);
        this.setStatus(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        this.setApplyTime(new Date());
        return this;
    }

    public HardwareWholeMachine edit(){
        if (!HardwareValueEnum.NODE_WAIT_APPLY.getValue().equals(this.getStatus())
                && !HardwareValueEnum.NODE_REJECT.getValue().equals(this.getStatus())) {
            throw new BusinessException("当前整机数据状态无法进行编辑操作！");
        }
        this.setUpdateTime(new Date());
        return this;
    }

    public HardwareWholeMachine delete() {
        if (!HardwareValueEnum.NODE_WAIT_APPLY.getValue().equals(this.getStatus())) {
            throw new BusinessException("当前整机数据状态无法进行删除操作！");
        }
        this.setStatus(HardwareValueEnum.NODE_DELETE.getValue());
        this.setUpdateTime(new Date());
        return this;
    }

    public HardwareWholeMachine apply() {
        if (!HardwareValueEnum.NODE_WAIT_APPLY.getValue().equals(this.getStatus())
                && !HardwareValueEnum.NODE_REJECT.getValue().equals(this.getStatus())) {
            throw new BusinessException("当前整机数据状态无法进行申请操作！");
        }
        this.setStatus(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());
        this.setUpdateTime(new Date());
        return this;
    }

    public HardwareWholeMachine pass() {
        if (!HardwareValueEnum.NODE_WAIT_APPROVE.getValue().equals(this.getStatus())) {
            throw new BusinessException("当前整机数据状态无法进行审批操作！");
        }
        this.setStatus(HardwareValueEnum.NODE_PASS.getValue());
        this.setUpdateTime(new Date());
        return this;
    }

    public HardwareWholeMachine reject() {
        if (!HardwareValueEnum.NODE_WAIT_APPROVE.getValue().equals(this.getStatus())) {
            throw new BusinessException("当前整机数据状态无法进行审批操作！");
        }
        this.setStatus(HardwareValueEnum.NODE_REJECT.getValue());
        this.setUpdateTime(new Date());
        return this;
    }

    public HardwareWholeMachine close() {
        if (!HardwareValueEnum.NODE_WAIT_APPROVE.getValue().equals(this.getStatus())
                && !HardwareValueEnum.NODE_REJECT.getValue().equals(this.getStatus())) {
            throw new BusinessException("当前整机数据状态无法进行关闭操作！");
        }
        this.setStatus(HardwareValueEnum.NODE_CLOSE.getValue());
        this.setUpdateTime(new Date());
        return this;
    }

    public String toSimpleJsonString() {
        JSONObject simple = new JSONObject();
        simple.put("整机厂商中文名称", this.getHardwareFactoryZy());
        simple.put("整机厂商英文名称", this.getHardwareFactoryEn());
        simple.put("整机型号", this.getHardwareModel());
        simple.put("操作系统版本", this.getOsVersion());
        simple.put("架构", this.getArchitecture());
        simple.put("认证日期", this.getDate());
        return simple.toJSONString();
    }
}
