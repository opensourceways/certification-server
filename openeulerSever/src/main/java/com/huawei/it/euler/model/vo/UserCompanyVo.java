/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import lombok.Data;

/**
 * UserCompanyVo
 *
 * @since 2024/07/03
 */
@Data
public class UserCompanyVo {
    /**
     * 申请状态
     */
    private Integer status;

    /**
     * logo id
     */
    private String logo;

    /**
     * logo文件名
     */
    private String logoName;

    /**
     * 营业执照id
     */
    private String license;

    /**
     * 营业执照名
     */
    private String licenseName;

    /**
     * 企业名
     */
    private String companyName;

    /**
     * 电话号码
     */
    private String telephone;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 企业邮箱
     */
    private String companyMail;

    /**
     * 申请人名
     */
    private String username;

    /**
     * 用户uuid
     */
    private String userUuid;
}
