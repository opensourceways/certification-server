/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.masterdata;

import com.huawei.it.euler.ddd.domain.masterdata.Os;
import com.huawei.it.euler.ddd.service.masterdata.cqe.OsCommand;
import com.huawei.it.euler.ddd.service.masterdata.cqe.OsQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * 操作系统 工厂类
 *
 * @author zhaoyan
 * @since 2024-11-14
 */
@Component
public class OsFactory {
    public Os toOs(OsCommand command) {
        Os os = new Os();
        BeanUtils.copyProperties(command, os);
        return os;
    }

    public OsQuery toQuery(OsCommand command) {
        OsQuery query = new OsQuery();
        query.setOsName(command.getOsName());
        query.setOsVersion(command.getOsVersion());
        query.setRelatedOsVersion(command.getRelatedOsVersion());
        return query;
    }
}
