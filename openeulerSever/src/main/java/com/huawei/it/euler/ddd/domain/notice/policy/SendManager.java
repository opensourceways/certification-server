/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.notice.policy;

import com.huawei.it.euler.ddd.domain.account.UserInfo;
import com.huawei.it.euler.ddd.domain.notice.NoticeMessage;
import com.huawei.it.euler.ddd.domain.notice.policy.impl.ApplyIntelEmailNoticePolicy;
import com.huawei.it.euler.ddd.domain.notice.policy.impl.ApproveIntelKafkaNoticePolicy;
import com.huawei.it.euler.ddd.domain.notice.policy.impl.CompanyApprovePhoneNoticePolicy;
import com.huawei.it.euler.ddd.domain.notice.policy.impl.RejectToUserEmailNoticePolicy;
import com.huawei.it.euler.ddd.domain.notice.policy.impl.RejectToUserPhoneNoticePolicy;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 发送策略管理对象
 *
 * @author zhaoyan
 * @since 2024-12-19
 */
@Component
public class SendManager {

    private static final List<SendPolicy> POLICIES = new ArrayList<>();
    static {
        POLICIES.add(new ApplyIntelEmailNoticePolicy());
        POLICIES.add(new ApproveIntelKafkaNoticePolicy());
        POLICIES.add(new CompanyApprovePhoneNoticePolicy());
        POLICIES.add(new RejectToUserEmailNoticePolicy());
        POLICIES.add(new RejectToUserPhoneNoticePolicy());
    }

    /**
     * 系统通知消息发送准备
     * @param userInfo 通知接收人信息
     * @param event 通知事件
     * @return 系统通知消息对象
     */
    public NoticeMessage prepareNotice(UserInfo userInfo, ApplicationEvent event){
        for (SendPolicy policy : POLICIES) {
            if (!policy.canSend(userInfo,event)){
                continue;
            }
            return policy.prepareSend(userInfo,event);
        }
        NoticeMessage noticeMessage = new NoticeMessage();
        noticeMessage.setReceiver(userInfo.getUuid());
        noticeMessage.setContent(event.getSource().toString());
        noticeMessage.sendCreate();
        noticeMessage.sendFailed("no handle policy");
        return noticeMessage;
    }
}
