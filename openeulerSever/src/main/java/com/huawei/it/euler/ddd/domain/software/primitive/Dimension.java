/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.software.primitive;

import lombok.Getter;

/**
 * 测评业务统计维度
 *
 * @author zhaoyan
 * @since 2024-12-24
 */
@Getter
public enum Dimension {
    /**
     * 新增业务筛选
     */
    DATE_KEY_ADD("application_time","申请时间"),
    /**
     * 签发证书筛选
     */
    DATE_KEY_SIGN("certification_time","签发时间"),

    /**
     * 日期间隔-月
     */
    DATE_PERIOD_MONTH("month","按月统计"),
    /**
     * 日期间隔-季度
     */
    DATE_PERIOD_QUARTER("quarter","按季度统计"),
    /**
     * 日期间隔-年
     */
    DATE_PERIOD_YEAR("year","按年统计"),

    /**
     * 架构定义：北向=ISV
     */
    PRODUCT_TYPE_ARCH_NORTH("north","北向"),

    /**
     * 架构定义：南向=IHV=IHV-server+IHV-server
     */
    PRODUCT_TYPE_ARCH_SOUTH("south","南向"),

    /**
     * 架构定义：OS:OSV
     */
    PRODUCT_TYPE_ARCH_OS("OS","操作系统"),

    /**
     * 产业定义:ISV
     */
    PRODUCT_TYPE_ISV("ISV","独立软件厂商"),

    /**
     * 产业定义:IHV=IHV-server+IHV-server
     */
    PRODUCT_TYPE_IHV("IHV","独立硬件厂商"),

    /**
     * 产业定义:OSV
     */
    PRODUCT_TYPE_OSV("OSV","操作系统厂商"),

    /**
     * 产业定义:IHV-server
     */
    PRODUCT_TYPE_IHV_SERVER("IHV-server","硬件-服务器整机"),

    /**
     * 产业定义:IHV-board
     */
    PRODUCT_TYPE_IHV_BOARD("IHV-board","硬件-板卡");

    private final String value;
    private final String desc;


    Dimension(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static String getArch(String productType) {
        if (PRODUCT_TYPE_ISV.getValue().equals(productType)) {
            return PRODUCT_TYPE_ARCH_NORTH.getValue();
        } else if (PRODUCT_TYPE_IHV_SERVER.getValue().equals(productType) || PRODUCT_TYPE_IHV_BOARD.getValue().equals(productType)) {
            return PRODUCT_TYPE_ARCH_SOUTH.getValue();
        } else {
            return PRODUCT_TYPE_ARCH_OS.getValue();
        }
    }
}
