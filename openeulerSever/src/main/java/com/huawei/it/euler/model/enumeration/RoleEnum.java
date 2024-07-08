/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.enumeration;

import lombok.Getter;

/**
 * 角色枚举类
 *
 * @since 2024/06/29
 */
public enum RoleEnum {
    USER(1, "user", "合作伙伴"),
    CHINA_REGION(2, "china_region", "中国区"),
    SIG_GROUP(3, "sig_group", "社区兼容性SIG组"),
    EULER_IC(4, "euler_ic", "区域欧拉创新中心"),
    FLAG_STORE(5, "flag_store", "欧拉社区旗舰店"),
    ADMIN(6, "admin", "管理员"),
    OSV_USER(7, "OSV_user", "OSV伙伴");

    @Getter
    private final Integer roleId; // 角色id

    @Getter
    private final String role; // 角色英文名

    @Getter
    private final String roleName; // 角色中文名

    RoleEnum(Integer roleId, String role, String roleName) {
        this.roleId = roleId;
        this.role = role;
        this.roleName = roleName;
    }
}
