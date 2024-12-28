/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.notice.policy.impl;

import com.huawei.it.euler.ddd.domain.account.UserInfo;
import com.huawei.it.euler.ddd.domain.notice.NoticeMessage;
import com.huawei.it.euler.ddd.domain.notice.policy.SendPolicy;
import com.huawei.it.euler.ddd.domain.notice.primitive.ApplyIntelTestEventNoticeVariable;
import com.huawei.it.euler.ddd.domain.notice.primitive.MsgType;
import com.huawei.it.euler.ddd.domain.notice.primitive.SendType;
import com.huawei.it.euler.ddd.domain.software.primitive.IntelScenario;
import com.huawei.it.euler.ddd.infrastructure.email.EmailTemplateVariable;
import com.huawei.it.euler.ddd.infrastructure.kafka.KafKaMessageDTO;
import com.huawei.it.euler.ddd.infrastructure.kafka.KafkaMessageTemplate;
import com.huawei.it.euler.ddd.service.software.cqe.ApplyIntelEvent;
import com.huawei.it.euler.model.entity.Software;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEvent;

import java.util.Date;

/**
 * 英特尔测评业务申请通知策略
 *
 * @author zhaoyan
 * @since 2024-12-20
 */
public class ApplyIntelEmailNoticePolicy implements SendPolicy {

    @Override
    public boolean canSend(UserInfo userInfo, ApplicationEvent event) {
        // 邮箱不存在
        if (StringUtils.isEmpty(userInfo.getEmail())){
            return false;
        }
        // 非测评审批事件
        if (!(event instanceof ApplyIntelEvent approveEvent)){
            return false;
        }

        Software software = approveEvent.getSoftware();
        // 是否英特尔业务
        return IntelScenario.isIntel(software.getAsId());
    }

    @Override
    public NoticeMessage prepareSend(UserInfo userInfo, ApplicationEvent event) {
        ApplyIntelEvent applyIntelEvent = (ApplyIntelEvent) event;
        Software software = applyIntelEvent.getSoftware();
        UserInfo applicant = applyIntelEvent.getApplicant();

        NoticeMessage noticeMessage = new NoticeMessage();
        noticeMessage.setReceiver(userInfo.getEmail());
        noticeMessage.setMsgType(MsgType.INTEL_APPLY_NOTICE);
        noticeMessage.setSendType(SendType.EMAIL);
        noticeMessage.setTemplate(EmailTemplateVariable.APPLY_INTEL_NOTICE_PATH);
        noticeMessage.setSubject(EmailTemplateVariable.APPLY_INTEL_NOTICE_SUBJECT);

        ApplyIntelTestEventNoticeVariable variable = new ApplyIntelTestEventNoticeVariable();
        variable.setCompanyName(software.getCompanyName());
        variable.setProductName(software.getProductName());
        variable.setProductFunctionDesc(software.getProductFunctionDesc());
        variable.setUsageScenesDesc(software.getUsageScenesDesc());
        variable.setProductVersion(software.getProductVersion());
        variable.setOsName(software.getOsName());
        variable.setOsVersion(software.getOsVersion());
        variable.setJsonHashRatePlatform(software.getJsonHashRatePlatform());
        variable.setProductType(software.getProductType());
        variable.setUserName(applicant.getUserName());
        variable.setUserEmail(applicant.getEmail());
        variable.setUserPhone(applicant.getPhone());
        noticeMessage.setContent(variable.getEmailContent());

        KafKaMessageDTO messageDTO = new KafKaMessageDTO();
        messageDTO.setUser(userInfo.getUuid());
        messageDTO.setType(KafkaMessageTemplate.TYPE_TODO);
        messageDTO.setTodoId(software.getId());
        messageDTO.setTodoStatus(KafkaMessageTemplate.TODO_STATUS_CREATE);
        messageDTO.setContent(KafkaMessageTemplate.CONTENT_TODO);
        messageDTO.setRedirectUrl(KafkaMessageTemplate.getSoftwareDetailUrl(String.valueOf(software.getId()),software.getProductName()));
        messageDTO.setCreateTime(new Date());
        noticeMessage.setKafKaMessageDTO(messageDTO);

        noticeMessage.sendCreate();
        return noticeMessage;
    }
}
