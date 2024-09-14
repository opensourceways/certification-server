/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.enumeration;

import java.util.List;
import java.util.Objects;

import lombok.Getter;

/**
 * 角色枚举类
 *
 * @since 2024/06/29
 */
@Getter
public enum RoleEnum {
    // user
    USER(1, "user", "合作伙伴"),
    // china region
    CHINA_REGION(2, "china_region", "中国区"),
    // euler ic
    EULER_IC(4, "euler_ic", "区域欧拉创新中心"),
    // flag store
    FLAG_STORE(5, "flag_store", "欧拉社区旗舰店"),
    // admin
    ADMIN(6, "admin", "管理员"),
    // osv user
    OSV_USER(7, "OSV_user", "OSV伙伴"),
    // openatom intel
    OPENATOM_INTEL(8, "openatom_intel", "开放原子-英特尔联合验证中心"),
    // report review
    REPORT_REVIEW(11, "report_review", "报告复审"),
    // certificate issuance
    CERTIFICATE_ISSUANCE(12, "certificate_issuance", "证书签发");

    private final Integer roleId; // 角色id

    private final String role; // 角色英文名

    private final String roleName; // 角色中文名

    RoleEnum(Integer roleId, String role, String roleName) {
        this.roleId = roleId;
        this.role = role;
        this.roleName = roleName;
    }

    public static boolean isUser(List<Integer> roles) {
        for (Integer role : roles) {
            if (Objects.equals(role, USER.getRoleId()) || Objects.equals(role, OSV_USER.getRoleId())) {
                return true;
            }
        }
        return false;
    }
}
