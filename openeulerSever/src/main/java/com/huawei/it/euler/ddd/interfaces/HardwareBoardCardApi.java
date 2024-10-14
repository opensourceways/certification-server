/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.interfaces;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.ddd.domain.hardware.*;
import com.huawei.it.euler.ddd.service.AccountService;
import com.huawei.it.euler.ddd.service.HardwareBoardCardApplicationService;
import com.huawei.it.euler.exception.NoLoginException;
import com.huawei.it.euler.exception.ParamException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hardwareBoardCard")
public class HardwareBoardCardApi {

    @Autowired
    private HardwareBoardCardApplicationService boardCardApplicationService;

    @Autowired
    private AccountService accountService;

    @GetMapping("/getPage")
    public JsonResponse<Page<HardwareBoardCard>> getPage(HardwareBoardCardSelectVO selectVO, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        selectVO.setUserUuid(loginUuid);
        Page<HardwareBoardCard> wholeMachinePage = boardCardApplicationService.getPage(selectVO);
        return JsonResponse.success(wholeMachinePage);
    }

    @GetMapping("/getById")
    public JsonResponse<HardwareBoardCard> getById(@RequestParam("id") @NotNull(message = "id不能为空") Integer id, HttpServletRequest request) throws NoLoginException {
        HardwareBoardCard wholeMachine = boardCardApplicationService.getById(id);
        return JsonResponse.success(wholeMachine);
    }

    @PostMapping("/insert")
    public JsonResponse<Integer> insert(@RequestBody HardwareBoardCard wholeMachine, HttpServletRequest request) throws NoLoginException, ParamException {
//        String loginUuid = accountService.getLoginUuid(request);
        String loginUuid = "123";
        HardwareBoardCard insert = boardCardApplicationService.insert(wholeMachine, loginUuid);
        return JsonResponse.success(insert.getId());
    }

    @PostMapping("/edit")
    public JsonResponse<Boolean> edit(@RequestBody HardwareBoardCard wholeMachine) throws NoLoginException {
        boardCardApplicationService.edit(wholeMachine);
        return JsonResponse.success(null);
    }

    @PostMapping("/delete")
    public JsonResponse<Boolean> delete(@RequestBody HardwareApprovalNode approvalNode) throws NoLoginException {
        boardCardApplicationService.delete(approvalNode);
        return JsonResponse.success(null);
    }

    @PostMapping("/apply")
    public JsonResponse<Boolean> apply(@RequestBody HardwareApprovalNode approvalNode) throws NoLoginException {
        boardCardApplicationService.apply(approvalNode);
        return JsonResponse.success(null);
    }
    
    @PostMapping("/approval")
    public JsonResponse<Boolean> approval(@RequestBody HardwareApprovalNode approvalNode) throws NoLoginException {
        boardCardApplicationService.approval(approvalNode);
        return JsonResponse.success(null);
    }
}
