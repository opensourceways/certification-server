/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.notice.policy.impl;

import com.huawei.it.euler.ddd.domain.account.UserInfo;
import com.huawei.it.euler.ddd.domain.notice.NoticeMessage;
import com.huawei.it.euler.ddd.domain.notice.primitive.MsgType;
import com.huawei.it.euler.ddd.domain.notice.primitive.SendType;
import com.huawei.it.euler.ddd.domain.notice.policy.SendPolicy;
import com.huawei.it.euler.ddd.domain.notice.primitive.ApproveEventNoticeVariable;
import com.huawei.it.euler.ddd.infrastructure.email.EmailTemplateVariable;
import com.huawei.it.euler.ddd.infrastructure.kafka.KafKaMessageDTO;
import com.huawei.it.euler.ddd.infrastructure.kafka.KafkaMessageTemplate;
import com.huawei.it.euler.ddd.service.software.cqe.RejectToUserEvent;
import com.huawei.it.euler.model.entity.Software;
import com.huawei.it.euler.model.enumeration.HandlerResultEnum;
import com.huawei.it.euler.model.enumeration.NodeEnum;
import com.huawei.it.euler.model.enumeration.RoleEnum;
import com.huawei.it.euler.model.vo.ProcessVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEvent;

import java.util.Date;

/**
 * 测评业务驳回通知用户-邮箱
 *
 * @author zhaoyan
 * @since 2024-12-20
 */
public class RejectToUserEmailNoticePolicy implements SendPolicy {

    @Override
    public boolean canSend(UserInfo userInfo, ApplicationEvent event) {
        // 邮箱不存在
        if (StringUtils.isEmpty(userInfo.getEmail())){
            return false;
        }
        // 自动邮箱
        if (userInfo.getEmail().endsWith(EmailTemplateVariable.AUTO_GENERATE_EMAIL)){
           return false;
        }
        // 非测评审批事件
        if (!(event instanceof RejectToUserEvent rejectToUserEvent)){
            return false;
        }
        ProcessVo processVo = rejectToUserEvent.getProcessVo();
        // 非驳回操作
        if (!HandlerResultEnum.REJECT.getId().equals(processVo.getHandlerResult())) {
            return false;
        }
        Software software = rejectToUserEvent.getSoftware();
        // 是否用户节点
        return RoleEnum.USER.getRoleId().equals(software.getReviewRole());
    }

    @Override
    public NoticeMessage prepareSend(UserInfo userInfo, ApplicationEvent event) {
        NoticeMessage noticeMessage = new NoticeMessage();
        noticeMessage.setReceiver(userInfo.getEmail());
        noticeMessage.setMsgType(MsgType.SOFTWARE_PROGRESS_NOTICE);
        noticeMessage.setSendType(SendType.EMAIL);
        noticeMessage.setTemplate(EmailTemplateVariable.PROGRESS_NOTICE_TEMPLATE_PATH);
        noticeMessage.setSubject(EmailTemplateVariable.PROGRESS_NOTICE_SUBJECT);
        noticeMessage.sendCreate();

        RejectToUserEvent rejectToUserEvent = (RejectToUserEvent) event;
        Software software = rejectToUserEvent.getSoftware();
        ProcessVo processVo = rejectToUserEvent.getProcessVo();
        String result = HandlerResultEnum.findById(processVo.getHandlerResult());
        String status = NodeEnum.findById(software.getStatus());

        ApproveEventNoticeVariable variable = new ApproveEventNoticeVariable();
        variable.setUserName(userInfo.getUserName());
        variable.setProductName(software.getProductName());
        variable.setResult(result);
        variable.setStatus(status);
        noticeMessage.setContent(variable.getEmailContent());

        KafKaMessageDTO messageDTO = new KafKaMessageDTO();
        messageDTO.setUser(userInfo.getUuid());
        messageDTO.setType(KafkaMessageTemplate.TYPE_NOTICE);
        messageDTO.setContent(KafkaMessageTemplate.getProgressNoticeContent(software.getProductName(),result, status));
        messageDTO.setRedirectUrl(KafkaMessageTemplate.getSoftwareDetailUrl(String.valueOf(software.getId()),software.getProductName()));
        messageDTO.setCreateTime(new Date());
        noticeMessage.setKafKaMessageDTO(messageDTO);
        return noticeMessage;
    }
}
