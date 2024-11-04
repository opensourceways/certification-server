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
import com.huawei.it.euler.exception.InputException;
import com.huawei.it.euler.exception.NoLoginException;
import com.huawei.it.euler.exception.ParamException;
import com.huawei.it.euler.model.vo.ExcelInfoVo;
import com.huawei.it.euler.model.vo.FileDataVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        String loginUuid = accountService.getLoginUuid(request);
        InsertResponse insert = wholeMachineApplicationService.insert(addCommand, loginUuid);
        return JsonResponse.success(insert);
    }

    /**
     * 上传兼容性数据表格
     *
     * @param file file
     * @param request request
     * @return JsonResponse
     */
    @PostMapping("/uploadTemplate")
    @PreAuthorize("hasAnyRole('OSV_user')")
    public JsonResponse<ExcelInfoVo> uploadTemplate(@RequestParam("file") @NotNull(message = "模板文件不能为空")
                                                    MultipartFile file, HttpServletRequest request) throws InputException, NoLoginException {
        String uuid = accountService.getLoginUuid(request);
//        return compatibleDataService.uploadTemplate(file, uuid);
        return null;
    }

    @GetMapping("/upload")
    @PreAuthorize("hasAnyRole('OSV_user')")
    public JsonResponse<FileDataVo> readCompatibleData(HttpServletRequest request,HttpServletResponse response,
                                                       @RequestParam("fileId") @NotBlank(message = "附件id不能为空") @Length(max = 50, message = "附件id超出范围")
                                                       String fileId) throws InputException, IllegalAccessException, NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
//        return compatibleDataService.readCompatibleData(loginUser, fileId);
        return null;
    }

    @PostMapping("/edit")
    public JsonResponse<Boolean> edit(@RequestBody HardwareWholeMachineEditCommand editCommand, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        wholeMachineApplicationService.edit(editCommand, loginUuid);
        return JsonResponse.success(null);
    }

    @PostMapping("/delete")
    public JsonResponse<Boolean> delete(@RequestBody HardwareApprovalNode approvalNode, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        approvalNode.setHardwareId(Integer.valueOf(loginUuid));
        wholeMachineApplicationService.delete(approvalNode);
        return JsonResponse.success(null);
    }

    @PostMapping("/apply")
    public JsonResponse<Boolean> apply(@RequestBody HardwareApprovalNode approvalNode, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        approvalNode.setHardwareId(Integer.valueOf(loginUuid));
        wholeMachineApplicationService.apply(approvalNode);
        return JsonResponse.success(null);
    }

    @PostMapping("/approval")
    public JsonResponse<Boolean> pass(@RequestBody HardwareApprovalNode approvalNode, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        approvalNode.setHardwareId(Integer.valueOf(loginUuid));
        wholeMachineApplicationService.pass(approvalNode);
        return JsonResponse.success(null);
    }

    @PostMapping("/approval")
    public JsonResponse<Boolean> reject(@RequestBody HardwareApprovalNode approvalNode, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        approvalNode.setHardwareId(Integer.valueOf(loginUuid));
        wholeMachineApplicationService.reject(approvalNode);
        return JsonResponse.success(null);
    }
}
