/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.model.vo.ApprovalPathSearchVo;
import com.huawei.it.euler.model.vo.ApprovalPathVo;

/**
 * ApprovalPathService
 *
 * @since 2024/07/03
 */
public interface ApprovalPathService {
    /**
     * 分页查询审批路径
     *
     * @param approvalPathSearchVo 筛选条件
     * @param page 页面
     * @return 列表
     */
    IPage<ApprovalPathVo> findApprovalPathByPage(ApprovalPathSearchVo approvalPathSearchVo, Page<ApprovalPathVo> page);
}
