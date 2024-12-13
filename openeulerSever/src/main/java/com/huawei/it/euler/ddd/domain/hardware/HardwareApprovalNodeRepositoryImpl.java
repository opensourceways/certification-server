/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.hardware;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huawei.it.euler.ddd.service.HardwareApprovalNodeSelectVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 硬件审批操作记录表 持久化实现类
 *
 * @author zhaoyan
 * @since 2024-10-09
 */
@Service
public class HardwareApprovalNodeRepositoryImpl extends ServiceImpl<HardwareApprovalNodeMapper, HardwareApprovalNodePO> implements IService<HardwareApprovalNodePO> {

    @Autowired
    private HardwareFactory hardwareFactory;

    public List<HardwareApprovalNode> getList(HardwareApprovalNodeSelectVO selectVO) {
        QueryWrapper<HardwareApprovalNodePO> queryWrapper = new QueryWrapper<>();
        if (selectVO.getHardwareId() > 0) {
            queryWrapper.eq("hardware_id", selectVO.getHardwareId());
        }
        if (!StringUtils.isEmpty(selectVO.getHardwareType())) {
            queryWrapper.eq("hardware_type", selectVO.getHardwareType());
        }
        if (!StringUtils.isEmpty(selectVO.getHandlerNode())) {
            queryWrapper.eq("handler_node", selectVO.getHandlerNode());
        }
        List<HardwareApprovalNodePO> nodePOList = list(queryWrapper);
        return nodePOList.stream().map(item -> hardwareFactory.createApprovalNode(item)).toList();
    }

    public boolean save(HardwareApprovalNode approvalNode) {
        HardwareApprovalNodePO approvalNodePO = hardwareFactory.createApprovalNodePO(approvalNode);
        return this.save(approvalNodePO);
    }

    public boolean saveBatch(List<HardwareApprovalNode> approvalNodeList) {
        List<HardwareApprovalNodePO> approvalNodePOList = approvalNodeList.stream().map(hardwareFactory::createApprovalNodePO).toList();
        return this.saveBatch(approvalNodePOList);
    }
}
