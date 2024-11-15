/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.interfaces;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.ddd.domain.permission.UserRole;
import com.huawei.it.euler.ddd.service.AccountService;
import com.huawei.it.euler.ddd.service.Command;
import com.huawei.it.euler.ddd.service.permission.UserRoleApplicationService;
import com.huawei.it.euler.ddd.service.permission.cqe.UserRoleCommand;
import com.huawei.it.euler.ddd.service.permission.cqe.UserRoleQuery;
import com.huawei.it.euler.exception.NoLoginException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author zhaoyan
 * @since 2024-11-14
 */
@Tag(name = "用户角色接口", description = "用户角色接口")
@RestController
@RequestMapping("/userRole")
public class UserRoleApi {

    @Autowired
    private UserRoleApplicationService userRoleApplicationService;

    @Autowired
    private AccountService accountService;

    @Operation(summary = "分页查询")
    @GetMapping("/getPage")
    @PreAuthorize("hasAnyRole('operator','admin')")
    public JsonResponse<Page<UserRole>> getPage(@ParameterObject UserRoleQuery query) {
        Page<UserRole> userRolePage = userRoleApplicationService.page(query);
        return JsonResponse.success(userRolePage);
    }

    @Operation(summary = "根据id查询对象", parameters = {@Parameter(name = "id", description = "用户角色id")})
    @GetMapping("/getById")
    @PreAuthorize("hasAnyRole('operator')")
    public JsonResponse<UserRole> getById(@RequestParam("id") @NotNull(message = "id不能为空") Integer id) {
        UserRole userRole = userRoleApplicationService.findById(id);
        return JsonResponse.success(userRole);
    }

    @Operation(summary = "授权")
    @PostMapping("/authorize")
    @PreAuthorize("hasAnyRole('operator')")
    public JsonResponse<UserRole> authorize(@RequestBody @Validated({Command.Add.class}) UserRoleCommand command, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        command.setLastUpdatedBy(loginUuid);
        command.setLastUpdatedTime(new Date());
        UserRole authorize = userRoleApplicationService.authorize(request, command);
        return JsonResponse.success(authorize);
    }

    @Operation(summary = "授权更新")
    @PostMapping("/reauthorize")
    @PreAuthorize("hasAnyRole('operator')")
    public JsonResponse<UserRole> reauthorize(@RequestBody @Validated({Command.Update.class}) UserRoleCommand command, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        command.setLastUpdatedBy(loginUuid);
        command.setLastUpdatedTime(new Date());
        UserRole authorize = userRoleApplicationService.reauthorize(request, command);
        return JsonResponse.success(authorize);
    }

    @Operation(summary = "取消授权")
    @PostMapping("/undoAuthorize")
    @PreAuthorize("hasAnyRole('operator')")
    public JsonResponse<Boolean> undoAuthorize(@RequestBody @Validated({Command.Update.class}) UserRoleCommand command, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        command.setLastUpdatedBy(loginUuid);
        command.setLastUpdatedTime(new Date());
        userRoleApplicationService.undoAuthorize(request, command);
        return JsonResponse.success(true);
    }

}
