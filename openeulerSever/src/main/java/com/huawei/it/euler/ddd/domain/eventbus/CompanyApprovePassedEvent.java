/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.eventbus;

import com.huawei.it.euler.model.vo.CompanyAuditVo;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 企业审核通过事件
 *
 * @author zhaoyan
 * @since 2024-12-13
 */
@Getter
public class CompanyApprovePassedEvent extends ApplicationEvent {

    private final CompanyAuditVo companyAuditVo;

    public CompanyApprovePassedEvent(Object source, CompanyAuditVo companyAuditVo) {
        super(source);
        this.companyAuditVo = companyAuditVo;
    }
}
