/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.entity;

import lombok.Data;

import java.util.Date;

/**
 * 企业信息
 *
 * @since 2024/07/03
 */
@Data
public class Company {
    /**
     * id
     */
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 用户电话号码
     */
    private String userTelephone;

    /**
     * 申请状态
     */
    private Integer status;

    /**
     * logo文件id
     */
    private String logo;

    /**
     * logo文件名
     */
    private String logoName;

    /**
     * 企业注册国家/地区
     */
    private String region;

    /**
     * 企业营业执照文件id
     */
    private String license;

    /**
     * 企业营业执照文件名
     */
    private String licenseName;

    /**
     * 企业名
     */
    private String companyName;

    /**
     * 企业信用代码
     */
    private String creditCode;

    /**
     * 企业注册地址
     */
    private String address;

    /**
     * 企业法人
     */
    private String legalPerson;

    /**
     * 企业注册资金
     */
    private String registrationCapital;

    /**
     * 企业注册日期
     */
    private String registrationDate;

    /**
     * 企业营业执照过期日期
     */
    private String expirationDate;

    /**
     * 评审意见
     */
    private String approvalComment;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 企业实名认证申请时间
     */
    private Date applyTime;

    /**
     * 企业邮箱
     */
    private String companyMail;

    /**
     * 用户uuid
     */
    private String userUuid;

    /**
     * 是否通过工商注册校验
     */
    private Boolean isCheckedPass;
}
