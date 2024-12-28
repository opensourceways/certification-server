/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.notice.policy.impl;

import com.huawei.it.euler.ddd.domain.account.UserInfo;
import com.huawei.it.euler.ddd.domain.notice.NoticeMessage;
import com.huawei.it.euler.ddd.domain.notice.policy.SendPolicy;
import com.huawei.it.euler.ddd.domain.notice.primitive.MsgType;
import com.huawei.it.euler.ddd.domain.notice.primitive.SendType;
import com.huawei.it.euler.ddd.infrastructure.kafka.KafKaMessageDTO;
import com.huawei.it.euler.ddd.infrastructure.kafka.KafkaMessageTemplate;
import com.huawei.it.euler.ddd.service.software.cqe.ApproveIntelEvent;
import com.huawei.it.euler.model.entity.Software;
import org.springframework.context.ApplicationEvent;

import java.util.Date;

/**
 * 英特尔测评业务审核通知策略
 *
 * @author zhaoyan
 * @since 2024-12-26
 */

public class ApproveIntelKafkaNoticePolicy implements SendPolicy {
    @Override
    public boolean canSend(UserInfo userInfo, ApplicationEvent event) {
        return event instanceof ApproveIntelEvent;
    }

    @Override
    public NoticeMessage prepareSend(UserInfo userInfo, ApplicationEvent event) {
        ApproveIntelEvent approveIntelEvent = (ApproveIntelEvent) event;
        Software software = approveIntelEvent.getSoftware();
        NoticeMessage noticeMessage = new NoticeMessage();
        noticeMessage.setReceiver(userInfo.getEmail());
        noticeMessage.setMsgType(MsgType.INTEL_APPROVE_NOTICE);
        noticeMessage.setSendType(SendType.KAFKA);

        KafKaMessageDTO messageDTO = new KafKaMessageDTO();
        messageDTO.setUser(userInfo.getUuid());
        messageDTO.setType(KafkaMessageTemplate.TYPE_TODO);
        messageDTO.setTodoId(software.getId());
        messageDTO.setTodoStatus(KafkaMessageTemplate.TODO_STATUS_DONE);
        messageDTO.setCreateTime(new Date());
        noticeMessage.setKafKaMessageDTO(messageDTO);

        noticeMessage.sendCreate();
        return noticeMessage;
    }
}
