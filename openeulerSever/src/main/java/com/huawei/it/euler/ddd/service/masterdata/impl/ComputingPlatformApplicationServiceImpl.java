/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.masterdata.impl;

import com.huawei.it.euler.ddd.domain.masterdata.ComputingPlatform;
import com.huawei.it.euler.ddd.domain.masterdata.ComputingPlatformRepository;
import com.huawei.it.euler.ddd.service.masterdata.ComputingPlatformFactory;
import com.huawei.it.euler.ddd.service.masterdata.ComputingPlatformApplicationService;
import com.huawei.it.euler.ddd.service.masterdata.cqe.ComputingPlatformCommand;
import com.huawei.it.euler.ddd.service.masterdata.cqe.ComputingPlatformQuery;
import com.huawei.it.euler.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 算力平台 application实现类
 *
 * @author zhaoyan
 * @since 2024-11-13
 */
@Slf4j
@Service
public class ComputingPlatformApplicationServiceImpl implements ComputingPlatformApplicationService {

    @Autowired
    private ComputingPlatformRepository computingPlatformRepository;

    @Autowired
    private ComputingPlatformFactory computingPlatformFactory;

    @Override
    public ComputingPlatform add(ComputingPlatformCommand command) {
        ComputingPlatformQuery query = computingPlatformFactory.toQuery(command);
        List<ComputingPlatform> queryList = computingPlatformRepository.findList(query);
        if (queryList != null && !queryList.isEmpty()) {
            throw new BusinessException("算力平台已存在！");
        }
        ComputingPlatform computingPlatform = computingPlatformFactory.toComputingPlatform(command);
        return computingPlatformRepository.add(computingPlatform);
    }

    @Override
    public void delete(ComputingPlatformCommand command) {
        computingPlatformRepository.delete(command.getId());
    }

    @Override
    public List<ComputingPlatform> findList(ComputingPlatformQuery query) {
        return computingPlatformRepository.findList(query);
    }
}
