/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.account;

import lombok.Data;

import java.util.List;

@Data
public class UserInfo {
    private String uuid;

    /**
     * 用户账户名称
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    private String phone;

    private String email;

    private List<Role> roleList;
}
