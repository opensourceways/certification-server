package com.huawei.it.euler.ddd.domain.account;

import lombok.Data;

import java.util.List;

@Data
public class UserInfo {
    private String uuid;

    private String userName;

    private String nickName;

    private String phone;

    private String email;

    private List<Role> roleList;
}
