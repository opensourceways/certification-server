/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 硬件-板卡 筛选VO对象
 *
 * @author zhaoyan
 * @since 2024-09-30
 */
@Data
public class HardwareWholeMachineSelectVO {

    /**
     * id
     */
    private String id;

    /**
     * id集合
     */
    private List<String> idList;

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
     * CPU型号
     */
    private String cpu;

    /**
     * 认证日期
     */
    private String date;

    /**
     * 状态
     */
    private String status;

    /**
     * 状态集合
     */
    private List<String> statusList;

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
     * 请求页码
     */
    private int current;

    /**
     * 请求页数量
     */
    private int size;
}
