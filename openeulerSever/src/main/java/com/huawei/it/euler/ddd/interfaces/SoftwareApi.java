/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.interfaces;

import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.ddd.domain.account.UserInfo;
import com.huawei.it.euler.ddd.domain.software.SoftwareStatistics;
import com.huawei.it.euler.ddd.service.AccountService;
import com.huawei.it.euler.ddd.service.software.SoftwareApplicationService;
import com.huawei.it.euler.ddd.service.software.cqe.SoftwareStatisticsQuery;
import com.huawei.it.euler.exception.NoLoginException;
import com.huawei.it.euler.exception.ParamException;
import com.huawei.it.euler.model.entity.Software;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 测评业务接口
 *
 * @author zhaoyan
 * @since 2024-12-25
 */
@RestController
@RequestMapping("/software")
public class SoftwareApi {

    @Autowired
    private SoftwareApplicationService applicationService;

    @Autowired
    private AccountService accountService;

    @PostMapping("/statistics")
    public JsonResponse<List<SoftwareStatistics>> statistics(@RequestBody @Valid SoftwareStatisticsQuery query, HttpServletRequest request) throws NoLoginException, ParamException {
        UserInfo loginUser = accountService.getLoginUser(request);
        List<SoftwareStatistics> statisticsList = applicationService.statistics(query, loginUser);
        return JsonResponse.success(statisticsList);
    }
}
