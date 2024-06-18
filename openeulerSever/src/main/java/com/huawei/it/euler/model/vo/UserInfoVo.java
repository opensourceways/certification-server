/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.vo;

import lombok.Data;

import java.util.List;

/**
 * UserInfoVo
 *
 * @since 2024/07/06
 */
@Data
public class UserInfoVo {
    private String username;

    private String telephone;

    private String mail;

    private String province;

    private String city;

    private String uuid;

    private List<String> roles;

    private List<String> roleNames;
}
