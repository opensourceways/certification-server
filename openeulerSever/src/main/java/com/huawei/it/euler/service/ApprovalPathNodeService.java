/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.service;

import com.huawei.it.euler.model.entity.ApprovalPathNode;

import java.util.List;

/**
 * ApprovalPathNodeService
 *
 * @since 2024/07/03
 */
public interface ApprovalPathNodeService {
    /**
     * 根据创新中心和流程节点查询审批节点
     *
     * @param icId 创新中心id
     * @param status 状态
     * @return 审批节点信息
     */
    ApprovalPathNode findANodeByIcIdAndSoftwareStatus(Integer icId, Integer status);

    /**
     * Find approval node by approval scenario id and software status.
     * @param asId approval scenario id
     * @param status software status
     * @return approval node
     */
    ApprovalPathNode findANodeByAsIdAndSoftwareStatus(Integer asId, Integer status);

    /**
     * 根据创新中心id查询节点
     *
     * @param icId 创新中心id
     * @return 列表
     */
    List<ApprovalPathNode> findNodeByIcId(Integer icId);

    /**
     * Find approval node by approval scenario id.
     * @param icId
     * @return
     */
    List<ApprovalPathNode> findNodeByAsId(Integer icId);

}
