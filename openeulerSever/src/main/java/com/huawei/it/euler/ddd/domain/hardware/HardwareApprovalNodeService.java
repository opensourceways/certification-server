/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 硬件审批操作记录表 服务实现类
 *
 * @author zhaoyan
 * @since 2024-10-09
 */
@Service
public class HardwareApprovalNodeService {

    @Autowired
    private HardwareApprovalNodeRepositoryImpl nodeRepository;

    @Autowired
    private HardwareFactory hardwareFactory;

    public List<HardwareApprovalNode> getList(Integer hardwareId, String hardwareType, String handlerNode) {
        QueryWrapper<HardwareApprovalNodePO> queryWrapper = new QueryWrapper<>();
        if (hardwareId > 0) {
            queryWrapper.eq("hardware_id", hardwareId);
        }
        if (!StringUtils.isEmpty(hardwareType)) {
            queryWrapper.eq("hardware_type", hardwareType);
        }
        if (!StringUtils.isEmpty(handlerNode)) {
            queryWrapper.eq("handler_node", handlerNode);
        }
        List<HardwareApprovalNodePO> nodePOList = nodeRepository.list(queryWrapper);
        return nodePOList.stream().map(item -> hardwareFactory.createApprovalNode(item)).toList();
    }

    public boolean insert(HardwareApprovalNode approvalNode) {
        HardwareApprovalNodePO approvalNodePO = hardwareFactory.createApprovalNodePO(approvalNode);
        return nodeRepository.save(approvalNodePO);
    }
}
