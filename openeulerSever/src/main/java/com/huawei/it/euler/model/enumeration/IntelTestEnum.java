package com.huawei.it.euler.model.enumeration;

import lombok.Getter;

@Getter
public enum IntelTestEnum {
    CPU_VENDOR("英特尔"),
    INNVOTATION_CENTER("开放原子-英特尔联合验证中心"),
    HASHRATEPLATFORM("Intel");

    private String name;

    IntelTestEnum(String name) {
        this.name = name;
    }
}
