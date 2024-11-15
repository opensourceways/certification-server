/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.interfaces;

import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.ddd.domain.masterdata.Os;
import com.huawei.it.euler.ddd.service.AccountService;
import com.huawei.it.euler.ddd.service.Command;
import com.huawei.it.euler.ddd.service.masterdata.OsApplicationService;
import com.huawei.it.euler.ddd.service.masterdata.cqe.OsCommand;
import com.huawei.it.euler.ddd.service.masterdata.cqe.OsQuery;
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
@Tag(name = "操作系统接口", description = "操作系统接口")
@RestController
@RequestMapping("/os")
public class OsApi {
    @Autowired
    private OsApplicationService osApplicationService;

    @Autowired
    private AccountService accountService;

    @Operation(summary = "列表查询")
    @GetMapping("/getList")
    @PreAuthorize("hasAnyRole('operator')")
    public JsonResponse<List<Os>> getList(@ParameterObject OsQuery query) {
        List<Os> osList = osApplicationService.findList(query);
        return JsonResponse.success(osList);
    }

    @Operation(summary = "新增")
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('operator')")
    public JsonResponse<Os> add(@RequestBody @Validated({Command.Add.class}) OsCommand command, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        command.setLastUpdatedBy(loginUuid);
        command.setLastUpdatedTime(new Date());
        Os os = osApplicationService.add(command);
        return JsonResponse.success(os);
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAnyRole('operator')")
    public JsonResponse<Boolean> delete(@RequestBody @Validated({Command.Update.class}) OsCommand command) throws NoLoginException {
        osApplicationService.delete(command);
        return JsonResponse.success(true);
    }
}
