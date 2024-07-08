/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Objects;

/**
 * 用户信息
 *
 * @since 2024/06/29
 */
@Data
public class EulerUser {
    @JsonIgnore
    private Integer id;

    private String username;

    private String password;

    @JsonIgnore
    private String encodePassword;

    @JsonIgnore
    private String role;

    private String telephone;

    private String mail;

    private String province;

    private String city;

    private String code;

    private String uuid;

    private Integer useable;

    /**
     * 判断用户手机是否有变化
     *
     * @param telephone 登录手机号
     * @param existUserTel 数据库存的手机号
     * @return boolean
     */
    public boolean hasChange(String telephone, String existUserTel) {
        return !Objects.equals(existUserTel, telephone);
    }

    /**
     * 设置用户基本信息
     *
     * @param telephone 手机号
     */
    public void setInfo(String telephone) {
        this.telephone = telephone;
    }
}
