/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import com.alibaba.fastjson.JSONObject;
import com.huawei.it.euler.exception.BusinessException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

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
     * 密级
     */
    private String securityLevel;

    /**
     * 关联整机id
     */
    private String wholeMachineIds;

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

    /**
     * 最新意见
     */
    private HardwareApprovalNode lastApproval;

    public HardwareBoardCard create(String uuid) {
        this.setUserUuid(uuid);
        this.setStatus(HardwareValueEnum.NODE_WAIT_APPLY.getValue());
        this.setApplyTime(new Date());
        return this;
    }

    public HardwareBoardCard createTemp(String uuid) {
        this.setUserUuid(uuid);
        this.setStatus(HardwareValueEnum.NODE_TEMP.getValue());
        this.setApplyTime(new Date());
        return this;
    }

    public HardwareBoardCard edit() {
        if (!HardwareValueEnum.NODE_TEMP.getValue().equals(this.getStatus())
                && !HardwareValueEnum.NODE_WAIT_APPLY.getValue().equals(this.getStatus())
                && !HardwareValueEnum.NODE_REJECT.getValue().equals(this.getStatus())) {
            throw new BusinessException("当前板卡数据状态无法进行编辑操作！");
        }
        this.setUpdateTime(new Date());
        return this;
    }

    public HardwareBoardCard delete() {
        if (!HardwareValueEnum.NODE_TEMP.getValue().equals(this.getStatus())
                && !HardwareValueEnum.NODE_WAIT_APPLY.getValue().equals(this.getStatus())) {
            throw new BusinessException("当前板卡数据状态无法进行删除操作！");
        }
        if (!StringUtils.isEmpty(this.getWholeMachineIds())) {
            throw new BusinessException("当前板卡关联整机，无法删除！");
        }
        this.setStatus(HardwareValueEnum.NODE_DELETE.getValue());
        this.setUpdateTime(new Date());
        return this;
    }

    public HardwareBoardCard apply() {
        if (!HardwareValueEnum.NODE_WAIT_APPLY.getValue().equals(this.getStatus())
                && !HardwareValueEnum.NODE_REJECT.getValue().equals(this.getStatus())) {
            throw new BusinessException("当前板卡数据状态无法进行申请操作！");
        }
        this.setStatus(HardwareValueEnum.NODE_WAIT_APPROVE.getValue());
        this.setUpdateTime(new Date());
        return this;
    }

    public HardwareBoardCard pass() {
        if (!HardwareValueEnum.NODE_WAIT_APPROVE.getValue().equals(this.getStatus())
                && !HardwareValueEnum.NODE_TEMP.getValue().equals(this.getStatus())) {
            throw new BusinessException("当前板卡数据状态无法进行审批操作！");
        }
        this.setStatus(HardwareValueEnum.NODE_PASS.getValue());
        this.setUpdateTime(new Date());
        return this;
    }

    public HardwareBoardCard reject() {
        if (!HardwareValueEnum.NODE_WAIT_APPROVE.getValue().equals(this.getStatus())) {
            throw new BusinessException("当前板卡数据状态无法进行审批操作！");
        }
        this.setStatus(HardwareValueEnum.NODE_REJECT.getValue());
        this.setUpdateTime(new Date());
        return this;
    }

    public HardwareBoardCard close() {
        if (!HardwareValueEnum.NODE_WAIT_APPROVE.getValue().equals(this.getStatus())
                && !HardwareValueEnum.NODE_REJECT.getValue().equals(this.getStatus())
                && !HardwareValueEnum.NODE_TEMP.getValue().equals(this.getStatus())) {
            throw new BusinessException("当前板卡数据状态无法进行关闭操作！");
        }
        this.setStatus(HardwareValueEnum.NODE_CLOSE.getValue());
        this.setUpdateTime(new Date());
        return this;
    }

    public HardwareBoardCard addWholeMachine(Integer wholeMachineId) {
        if (StringUtils.isEmpty(this.getWholeMachineIds())) {
            this.setWholeMachineIds(String.valueOf(wholeMachineId));
        } else {
            this.setWholeMachineIds(this.getWholeMachineIds() + "," + wholeMachineId);
        }
        return this;
    }

    public HardwareBoardCard removeWholeMachine(Integer wholeMachineId) {
        if (!StringUtils.isEmpty(this.getWholeMachineIds())) {
            String[] split = this.getWholeMachineIds().split(",");
            String idStr = String.valueOf(wholeMachineId);
            String collect = null;
            if (!idStr.equals(this.getWholeMachineIds())) {
                collect = Arrays.stream(split).filter(id -> !id.equals(idStr)).collect(Collectors.joining(","));
            }
            this.setWholeMachineIds(collect);
        }
        return this;
    }

    public String toSimpleJsonString() {
        JSONObject simple = new JSONObject();
        simple.put("操作系统", this.getOs());
        simple.put("架构", this.getArchitecture());
        simple.put("板卡型号", this.getBoardModel());
        simple.put("芯片型号", this.getChipModel());
        return simple.toJSONString();
    }
}
