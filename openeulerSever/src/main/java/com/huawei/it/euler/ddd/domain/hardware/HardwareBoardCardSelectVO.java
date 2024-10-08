/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 硬件-板卡 筛选VO对象
 *
 * @author zhaoyan
 * @since 2024-09-30
 */
@Data
public class HardwareBoardCardSelectVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * id集合
     */
    private List<String> idList;

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
     * 申请人uuid
     */
    private String userUuid;

    /**
     * 状态
     */
    private String status;

    /**
     * 状态集合
     */
    private List<String> statusList;

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
