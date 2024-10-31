/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.interfaces;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.ddd.domain.hardware.HardwareApprovalNode;
import com.huawei.it.euler.ddd.domain.hardware.HardwareWholeMachine;
import com.huawei.it.euler.ddd.domain.hardware.HardwareWholeMachineSelectVO;
import com.huawei.it.euler.ddd.service.*;
import com.huawei.it.euler.exception.NoLoginException;
import com.huawei.it.euler.exception.ParamException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hardwareWholeMachine")
public class HardwareWholeMachineApi {

    @Autowired
    private HardwareWholeMachineApplicationService wholeMachineApplicationService;

    @Autowired
    private AccountService accountService;

    @GetMapping("/getPage")
    public JsonResponse<Page<HardwareWholeMachine>> getPage(HardwareWholeMachineSelectVO selectVO, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        selectVO.setUserUuid(loginUuid);
        Page<HardwareWholeMachine> wholeMachinePage = wholeMachineApplicationService.getPage(selectVO);
        return JsonResponse.success(wholeMachinePage);
    }

    @GetMapping("/getById")
    public JsonResponse<HardwareWholeMachine> getById(@RequestParam("id") @NotNull(message = "id不能为空") Integer id, HttpServletRequest request) throws NoLoginException {
        HardwareWholeMachine wholeMachine = wholeMachineApplicationService.getById(id);
        return JsonResponse.success(wholeMachine);
    }

    @PostMapping("/insert")
    public JsonResponse<InsertResponse> insert(@RequestBody @Valid HardwareWholeMachineAddCommand addCommand, HttpServletRequest request) throws NoLoginException, ParamException {
//        String loginUuid = accountService.getLoginUuid(request);
        String loginUuid = "123";
        InsertResponse insert = wholeMachineApplicationService.insert(addCommand, loginUuid);
        return JsonResponse.success(insert);
    }

    @PostMapping("/edit")
    public JsonResponse<Boolean> edit(@RequestBody HardwareWholeMachineEditCommand editCommand) throws NoLoginException {
        //        String loginUuid = accountService.getLoginUuid(request);
        String loginUuid = "123";
        wholeMachineApplicationService.edit(editCommand, loginUuid);
        return JsonResponse.success(null);
    }

    @PostMapping("/delete")
    public JsonResponse<Boolean> delete(@RequestBody HardwareApprovalNode approvalNode) throws NoLoginException {
        String loginUuid = "123";
        approvalNode.setHardwareId(Integer.valueOf(loginUuid));
        wholeMachineApplicationService.delete(approvalNode);
        return JsonResponse.success(null);
    }

    @PostMapping("/apply")
    public JsonResponse<Boolean> apply(@RequestBody HardwareApprovalNode approvalNode) throws NoLoginException {
        String loginUuid = "123";
        approvalNode.setHardwareId(Integer.valueOf(loginUuid));
        wholeMachineApplicationService.apply(approvalNode);
        return JsonResponse.success(null);
    }

    @PostMapping("/approval")
    public JsonResponse<Boolean> pass(@RequestBody HardwareApprovalNode approvalNode) throws NoLoginException {
        String loginUuid = "123";
        approvalNode.setHardwareId(Integer.valueOf(loginUuid));
        wholeMachineApplicationService.pass(approvalNode);
        return JsonResponse.success(null);
    }

    @PostMapping("/approval")
    public JsonResponse<Boolean> reject(@RequestBody HardwareApprovalNode approvalNode) throws NoLoginException {
        String loginUuid = "123";
        approvalNode.setHardwareId(Integer.valueOf(loginUuid));
        wholeMachineApplicationService.reject(approvalNode);
        return JsonResponse.success(null);
    }
}
