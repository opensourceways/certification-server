/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.notice.policy.impl;

import com.huawei.it.euler.ddd.domain.account.UserInfo;
import com.huawei.it.euler.ddd.domain.company.primitive.ApproveResult;
import com.huawei.it.euler.ddd.domain.notice.NoticeMessage;
import com.huawei.it.euler.ddd.domain.notice.primitive.CompanyApproveNoticeVariable;
import com.huawei.it.euler.ddd.domain.notice.primitive.MsgType;
import com.huawei.it.euler.ddd.domain.notice.primitive.SendType;
import com.huawei.it.euler.ddd.domain.notice.policy.SendPolicy;
import com.huawei.it.euler.ddd.infrastructure.sms.SmsProperties;
import com.huawei.it.euler.ddd.service.company.cqe.CompanyApproveResultEvent;
import com.huawei.it.euler.model.vo.CompanyAuditVo;
import com.huawei.it.euler.util.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEvent;

/**
 * 企业审核通知策略
 *
 * @author zhaoyan
 * @since 2024-12-19
 */
public class CompanyApproveNoticePolicy implements SendPolicy {

    @Override
    public boolean canSend(UserInfo userInfo, ApplicationEvent event) {
        // 手机不存在
        if (StringUtils.isEmpty(userInfo.getPhone())){
            return false;
        }
        return event instanceof CompanyApproveResultEvent;
    }

    @Override
    public NoticeMessage prepareSend(UserInfo userInfo, ApplicationEvent event) {
        CompanyApproveResultEvent approveResultEvent = (CompanyApproveResultEvent) event;
        CompanyAuditVo companyAuditVo = approveResultEvent.getCompanyAuditVo();

        CompanyApproveNoticeVariable approveNoticeVariable = new CompanyApproveNoticeVariable();
        approveNoticeVariable.setResult(ApproveResult.findDesc(companyAuditVo.getResult()));
        approveNoticeVariable.setComment(companyAuditVo.getComment());

        NoticeMessage noticeMessage = new NoticeMessage();
        noticeMessage.setMsgType(MsgType.COMPANY_APPROVE);
        noticeMessage.setReceiver(userInfo.getPhone());
        noticeMessage.setSendType(SendType.PHONE);
        SmsProperties smsProperties = SpringUtil.getBean("smsProperties", SmsProperties.class);
        noticeMessage.setTemplate(smsProperties.getTemplateId());
        noticeMessage.setContent(approveNoticeVariable.getPhoneTemplateParameters());
        return noticeMessage;
    }
}