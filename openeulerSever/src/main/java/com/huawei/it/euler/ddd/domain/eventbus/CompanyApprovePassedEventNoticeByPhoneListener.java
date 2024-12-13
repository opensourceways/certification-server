/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.eventbus;

import com.huawei.it.euler.config.extension.SmsConfig;
import com.huawei.it.euler.ddd.domain.account.UserInfo;
import com.huawei.it.euler.ddd.service.AccountService;
import com.huawei.it.euler.model.vo.CompanyAuditVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 企业审核通过事件 短信通知监听器
 *
 * @author zhaoyan
 * @since 2024-12-13
 */
@Component
public class CompanyApprovePassedEventNoticeByPhoneListener {

    @Value("${sns.templateId}")
    private String templateId;

    @Autowired
    private AccountService accountService;

    @Autowired
    private SmsConfig smsConfig;

    @TransactionalEventListener
    @Async
    public void phoneNoticeListener(CompanyApprovePassedEvent event) {
        CompanyAuditVo companyAuditVo = event.getCompanyAuditVo();
        UserInfo userInfo = accountService.getUserInfo(companyAuditVo.getUserUuid());
        if (!StringUtils.isEmpty(userInfo.getPhone())){
            String status = companyAuditVo.getResult() ? "通过" : "驳回";
            String comment = companyAuditVo.getComment();
            String templateParas = "[\"" + status + "\",\"" + comment + "\"]";
//            String phone = userInfo.getPhone();
            String phone = "18765950879";
            smsConfig.sendNotification(templateId, phone, templateParas);
        }
    }
}
