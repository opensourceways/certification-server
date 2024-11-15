/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.masterdata.impl;

import com.huawei.it.euler.ddd.domain.masterdata.Os;
import com.huawei.it.euler.ddd.domain.masterdata.OsRepository;
import com.huawei.it.euler.ddd.service.masterdata.OsApplicationService;
import com.huawei.it.euler.ddd.service.masterdata.OsFactory;
import com.huawei.it.euler.ddd.service.masterdata.cqe.OsCommand;
import com.huawei.it.euler.ddd.service.masterdata.cqe.OsQuery;
import com.huawei.it.euler.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 操作系统 application实现类
 *
 * @author zhaoyan
 * @since 2024-11-14
 */
@Slf4j
@Service
public class OsApplicationServiceImpl implements OsApplicationService {

    @Autowired
    private OsRepository osRepository;

    @Autowired
    private OsFactory osFactory;

    @Override
    public Os add(OsCommand command) {
        OsQuery query = osFactory.toQuery(command);
        List<Os> queryList = osRepository.findList(query);
        if (queryList != null && !queryList.isEmpty()) {
            throw new BusinessException("操作系统已存在！");
        }
        Os os = osFactory.toOs(command);
        return osRepository.add(os);
    }

    @Override
    public void delete(OsCommand command) {
        osRepository.delete(command.getId());
    }

    @Override
    public List<Os> findList(OsQuery query) {
        return osRepository.findList(query);
    }
}
