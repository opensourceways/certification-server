/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.interfaces;

import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.ddd.domain.masterdata.ComputingPlatform;
import com.huawei.it.euler.ddd.service.AccountService;
import com.huawei.it.euler.ddd.service.Command;
import com.huawei.it.euler.ddd.service.masterdata.ComputingPlatformApplicationService;
import com.huawei.it.euler.ddd.service.masterdata.cqe.ComputingPlatformCommand;
import com.huawei.it.euler.ddd.service.masterdata.cqe.ComputingPlatformQuery;
import com.huawei.it.euler.exception.NoLoginException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author zhaoyan
 * @since 2024-11-14
 */
@Tag(name = "算力平台接口", description = "算力平台接口")
@RestController
@RequestMapping("/computingPlatform")
public class ComputingPlatformApi {

    @Autowired
    private ComputingPlatformApplicationService computingPlatformApplicationService;

    @Autowired
    private AccountService accountService;

    @Operation(summary = "列表查询")
    @GetMapping("/getList")
    @PreAuthorize("hasAnyRole('operator')")
    public JsonResponse<List<ComputingPlatform>> getList(@ParameterObject ComputingPlatformQuery query) {
        List<ComputingPlatform> computingPlatformList = computingPlatformApplicationService.findList(query);
        return JsonResponse.success(computingPlatformList);
    }

    @Operation(summary = "新增")
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('operator')")
    public JsonResponse<ComputingPlatform> add(@RequestBody @Validated({Command.Add.class}) ComputingPlatformCommand command, HttpServletRequest request) throws NoLoginException {
                String loginUuid = accountService.getLoginUuid(request);
        command.setLastUpdatedBy(loginUuid);
        command.setLastUpdatedTime(new Date());
        ComputingPlatform computingPlatform = computingPlatformApplicationService.add(command);
        return JsonResponse.success(computingPlatform);
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAnyRole('operator')")
    public JsonResponse<Boolean> delete(@RequestBody @Validated({Command.Update.class}) ComputingPlatformCommand command) throws NoLoginException {
        computingPlatformApplicationService.delete(command);
        return JsonResponse.success(true);
    }
}
