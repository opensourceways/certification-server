/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.software;

import com.huawei.it.euler.ddd.domain.account.UserInfo;
import com.huawei.it.euler.ddd.domain.notice.NoticeMessage;
import com.huawei.it.euler.ddd.domain.notice.NoticeMessageRepository;
import com.huawei.it.euler.ddd.domain.notice.policy.SendManager;
import com.huawei.it.euler.ddd.service.AccountService;
import com.huawei.it.euler.ddd.service.notice.NoticeApplicationService;
import com.huawei.it.euler.ddd.service.software.cqe.ApplyIntelTestEvent;
import com.huawei.it.euler.ddd.service.software.cqe.ApproveEvent;
import com.huawei.it.euler.model.entity.Software;
import com.huawei.it.euler.model.enumeration.RoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

/**
 * 测评业务 application service
 *
 * @author zhaoyan
 * @since 2024-12-19
 */
@Service
public class SoftwareApplicationService {

    @Autowired
    private AccountService accountService;

    @Autowired
    private NoticeMessageRepository noticeMessageRepository;

    @Autowired
    private SendManager sendManager;

    @Autowired
    private NoticeApplicationService noticeApplicationService;

    /**
     * 英特尔测评业务申请事件处理，通知到英特尔审核人员
     * @param event 英特尔业务申请事件
     */
    @TransactionalEventListener
    @Async
    public void intelApplyNotice(ApplyIntelTestEvent event) {
        List<UserInfo> userInfoList = accountService.getUserInfoList(RoleEnum.OPENATOM_INTEL.getRoleId());
        for (UserInfo userInfo : userInfoList) {
            NoticeMessage noticeMessage = sendManager.prepareNotice(userInfo, event);
            noticeMessage = noticeApplicationService.sendNotice(noticeMessage);
            noticeMessageRepository.record(noticeMessage);
        }
    }

    /**
     * 测评业务申请事件处理，驳回到伙伴节点场景， 通知伙伴
     * @param event 测评业务申请事件
     */
    @TransactionalEventListener
    @Async
    public void progressNoticeHandler(ApproveEvent event) {
        Software software = event.getSoftware();
        UserInfo userInfo = accountService.getUserInfo(software.getUserUuid());
        NoticeMessage noticeMessage = sendManager.prepareNotice(userInfo, event);
        noticeMessage = noticeApplicationService.sendNotice(noticeMessage);
        noticeMessageRepository.record(noticeMessage);
    }

}
