/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.it.euler.mapper.ApprovalPathNodeMapper;
import com.huawei.it.euler.mapper.InnovationCenterMapper;
import com.huawei.it.euler.model.entity.ApprovalPathNode;
import com.huawei.it.euler.service.ApprovalPathNodeService;

import lombok.extern.slf4j.Slf4j;

/**
 * ApprovalPathNodeServiceImpl
 *
 * @since 2024/07/03
 */
@Service
@Slf4j
public class ApprovalPathNodeServiceImpl implements ApprovalPathNodeService {
    @Autowired
    private ApprovalPathNodeMapper approvalPathNodeMapper;

    @Autowired
    private InnovationCenterMapper innovationCenterMapper;


    @Override
    public ApprovalPathNode findANodeByAsIdAndSoftwareStatus(Integer asId, Integer status) {
        return approvalPathNodeMapper.findNodeByAsIdAndStatus(asId, status);
    }

    @Override
    public List<ApprovalPathNode> findNodeByAsId(Integer icId) {
        return approvalPathNodeMapper.findNodeByAsId(icId);
    }
}
