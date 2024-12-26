/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.interfaces;

import com.huawei.it.euler.ddd.domain.account.UserInfo;
import com.huawei.it.euler.ddd.service.AccountService;
import com.huawei.it.euler.ddd.service.software.SoftwareApplicationService;
import com.huawei.it.euler.ddd.service.software.cqe.SoftwareStatisticsQuery;
import com.huawei.it.euler.exception.NoLoginException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 测评业务接口
 *
 * @author zhaoyan
 * @since 2024-12-25
 */
@Tag(name = "测评业务接口", description = "测评业务接口")
@RestController
@RequestMapping("/software")
public class SoftwareApi {

    @Autowired
    private SoftwareApplicationService applicationService;

    @Autowired
    private AccountService accountService;

    @Operation(summary = "测评业务统计数据导出")
    @PostMapping("/exportStatistics")
    public void exportStatistics(@RequestBody @Valid SoftwareStatisticsQuery query, HttpServletResponse response,
                       HttpServletRequest request) throws NoLoginException, IOException {
        UserInfo loginUser = accountService.getLoginUser(request);
        applicationService.exportStatistics(query, response, loginUser);
    }
}
