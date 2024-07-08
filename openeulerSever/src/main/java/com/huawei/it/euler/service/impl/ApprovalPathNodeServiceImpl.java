/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.service.impl;

import com.huawei.it.euler.mapper.ApprovalPathNodeMapper;
import com.huawei.it.euler.mapper.InnovationCenterMapper;
import com.huawei.it.euler.model.entity.ApprovalPathNode;
import com.huawei.it.euler.model.entity.InnovationCenter;
import com.huawei.it.euler.service.ApprovalPathNodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public ApprovalPathNode findANodeByIcIdAndSoftwareStatus(Integer icId, Integer status) {
        log.info("findANodeByIcIdAndSoftwareStatus({}, {})", icId, status);
        ApprovalPathNode approvalPathNode;
        approvalPathNode = approvalPathNodeMapper.findNodeByIcIdAndStatus(icId, status);
        if (approvalPathNode != null) {
            return approvalPathNode;
        }
        InnovationCenter defaultIc = innovationCenterMapper.findDefault();
        approvalPathNode = approvalPathNodeMapper.findNodeByIcIdAndStatus(defaultIc.getId(), status);
        return approvalPathNode;
    }

    @Override
    public List<ApprovalPathNode> findNodeByIcId(Integer icId) {
        List<ApprovalPathNode> approvalPathNodes = approvalPathNodeMapper.findNodeByIcId(icId);
        if (approvalPathNodes.isEmpty()) {
            approvalPathNodes = approvalPathNodeMapper.findDefaultNode();
        }
        return approvalPathNodes;
    }
}
