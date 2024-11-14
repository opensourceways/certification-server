/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.repository.builder;

import com.huawei.it.euler.ddd.domain.masterdata.Os;
import com.huawei.it.euler.ddd.infrastructure.persistence.po.OsPO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 操作系统构造器
 *
 * @author zhaoyan
 * @since 2024-11-14
 */
@Component
public class OsBuilder {
    public OsPO fromOs(Os os) {
        OsPO osPO = new OsPO();
        BeanUtils.copyProperties(os, osPO);
        return osPO;
    }

    public Os toOs(OsPO osPO) {
        Os os = new Os();
        BeanUtils.copyProperties(osPO, os);
        return os;
    }

    public List<Os> toOsList(List<OsPO> osPOList) {
        return osPOList.stream().map(this::toOs).toList();
    }
}
