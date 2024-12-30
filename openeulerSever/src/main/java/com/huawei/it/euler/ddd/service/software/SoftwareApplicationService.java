/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.software;

import com.huawei.it.euler.ddd.domain.account.UserInfo;
import com.huawei.it.euler.ddd.domain.notice.NoticeMessage;
import com.huawei.it.euler.ddd.domain.notice.NoticeMessageRepository;
import com.huawei.it.euler.ddd.domain.notice.policy.SendManager;
import com.huawei.it.euler.ddd.domain.software.SoftwareStatistics;
import com.huawei.it.euler.ddd.service.AccountService;
import com.huawei.it.euler.ddd.service.notice.NoticeApplicationService;
import com.huawei.it.euler.ddd.service.software.cqe.ApplyIntelEvent;
import com.huawei.it.euler.ddd.service.software.cqe.ApproveIntelEvent;
import com.huawei.it.euler.ddd.service.software.cqe.RejectToUserEvent;
import com.huawei.it.euler.ddd.service.software.cqe.SoftwareStatisticsQuery;
import com.huawei.it.euler.mapper.RoleMapper;
import com.huawei.it.euler.mapper.SoftwareMapper;
import com.huawei.it.euler.model.entity.Software;
import com.huawei.it.euler.model.enumeration.RoleEnum;
import com.huawei.it.euler.model.vo.RoleVo;
import com.huawei.it.euler.util.ExcelUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 测评业务 application service
 *
 * @author zhaoyan
 * @since 2024-12-19
 */
@Service
public class SoftwareApplicationService {

    private static final Integer ALL_PERMISSION = 0;

    @Autowired
    private SoftwareMapper mapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private ExcelUtils excelUtils;

    @Autowired
    private AccountService accountService;

    @Autowired
    private NoticeMessageRepository noticeMessageRepository;

    @Autowired
    private SendManager sendManager;

    @Autowired
    private NoticeApplicationService noticeApplicationService;

    /**
     * 测评业务统计数据导出
     * @param query 查询参数
     * @param response 数据响应
     * @param loginUser 登录账号
     * @throws IOException IO异常
     */
    public void exportStatistics(SoftwareStatisticsQuery query, HttpServletResponse response, UserInfo loginUser) throws IOException {
        Set<Integer> dateScopeSet = roleMapper.findRoleInfoByUserId(Integer.valueOf(loginUser.getUuid()))
                .stream().map(RoleVo::getDataScope).filter(Objects::nonNull).collect(Collectors.toSet());
        if (!dateScopeSet.isEmpty() && !dateScopeSet.contains(ALL_PERMISSION)) {
            query.setTestOrgIdList(dateScopeSet.stream().toList());
        }
        List<SoftwareStatistics> statistics = mapper.statistics(query);
        if (query.getProductTypeList() != null && !query.getProductTypeList().isEmpty()){
            statistics = statistics.stream().filter(item -> query.getProductTypeList().contains(item.getProductType())).toList();
        }
        excelUtils.exportSoftWareStatistics(statistics, response);
    }

    /**
     * 英特尔测评业务申请事件处理，通知到英特尔审核人员
     * @param event 英特尔业务申请事件
     */
    @TransactionalEventListener
    @Async
    public void applyIntelNotice(ApplyIntelEvent event) {
        List<UserInfo> userInfoList = accountService.getUserInfoList(RoleEnum.OPENATOM_INTEL.getRoleId());
        for (UserInfo userInfo : userInfoList) {
            NoticeMessage noticeMessage = sendManager.prepareNotice(userInfo, event);
            noticeMessage = noticeApplicationService.sendNotice(noticeMessage);
            noticeMessageRepository.record(noticeMessage);
        }
    }

    /**
     * 英特尔测评业务审核事件处理，取消待办
     * @param event 测评业务申请事件
     */
    @TransactionalEventListener
    @Async
    public void approveIntelNotice(ApproveIntelEvent event) {
        Software software = event.getSoftware();
        UserInfo userInfo = accountService.getUserInfo(software.getUserUuid());
        NoticeMessage noticeMessage = sendManager.prepareNotice(userInfo, event);
        noticeMessage = noticeApplicationService.sendNotice(noticeMessage);
        noticeMessageRepository.record(noticeMessage);
    }

    /**
     * 测评业务申请事件处理，驳回到伙伴节点场景， 通知伙伴
     * @param event 测评业务申请事件
     */
    @TransactionalEventListener
    @Async
    public void rejectToUserNotice(RejectToUserEvent event) {
        Software software = event.getSoftware();
        UserInfo userInfo = accountService.getUserInfo(software.getUserUuid());
        NoticeMessage noticeMessage = sendManager.prepareNotice(userInfo, event);
        noticeMessage = noticeApplicationService.sendNotice(noticeMessage);
        noticeMessageRepository.record(noticeMessage);
    }
}
