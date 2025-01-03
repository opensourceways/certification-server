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
import com.huawei.it.euler.ddd.infrastructure.kafka.KafKaMessageDTO;
import com.huawei.it.euler.ddd.infrastructure.kafka.KafkaMessageTemplate;
import com.huawei.it.euler.ddd.infrastructure.sms.SmsProperties;
import com.huawei.it.euler.ddd.service.company.cqe.CompanyApproveResultEvent;
import com.huawei.it.euler.model.vo.CompanyAuditVo;
import com.huawei.it.euler.util.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEvent;

import java.util.Date;

/**
 * 企业审核通知策略
 *
 * @author zhaoyan
 * @since 2024-12-19
 */
public class CompanyApprovePhoneNoticePolicy implements SendPolicy {

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

        NoticeMessage noticeMessage = new NoticeMessage();
        noticeMessage.setMsgType(MsgType.COMPANY_APPROVE);
        noticeMessage.setReceiver(userInfo.getPhone());
        noticeMessage.setSendType(SendType.PHONE);
        SmsProperties smsProperties = SpringUtil.getBean("smsProperties", SmsProperties.class);
        noticeMessage.setTemplate(smsProperties.getTemplateId());

        String result = ApproveResult.findDesc(companyAuditVo.getResult());
        CompanyApproveNoticeVariable approveNoticeVariable = new CompanyApproveNoticeVariable();
        approveNoticeVariable.setResult(result);
        approveNoticeVariable.setComment(companyAuditVo.getComment());
        noticeMessage.setContent(approveNoticeVariable.getPhoneTemplateParameters());

        KafKaMessageDTO messageDTO = new KafKaMessageDTO();
        messageDTO.setUser(userInfo.getUuid());
        messageDTO.setType(KafkaMessageTemplate.TYPE_NOTICE);
        String companyNoticeContent = KafkaMessageTemplate.getCompanyNoticeContent(result, companyAuditVo.getComment());
        messageDTO.setContent(companyNoticeContent);
        messageDTO.setRedirectUrl(KafkaMessageTemplate.URL_INDEX);
        messageDTO.setCreateTime(new Date());
        noticeMessage.setKafKaMessageDTO(messageDTO);
        return noticeMessage;
    }
}