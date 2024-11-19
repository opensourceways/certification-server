/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.interfaces;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.ddd.domain.hardware.HardwareApprovalNode;
import com.huawei.it.euler.ddd.domain.hardware.HardwareValueEnum;
import com.huawei.it.euler.ddd.domain.hardware.HardwareWholeMachine;
import com.huawei.it.euler.ddd.service.HardwareWholeMachineSelectVO;
import com.huawei.it.euler.ddd.service.*;
import com.huawei.it.euler.exception.InputException;
import com.huawei.it.euler.exception.NoLoginException;
import com.huawei.it.euler.exception.ParamException;
import com.huawei.it.euler.model.vo.ExcelInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "整机接口", description = "硬件整机业务接口")
@RestController
@RequestMapping("/hardwareWholeMachine")
public class HardwareWholeMachineApi {

    @Autowired
    private HardwareWholeMachineApplicationService wholeMachineApplicationService;

    @Autowired
    private AccountService accountService;

    @Operation(summary = "分页查询")
    @GetMapping("/getPage")
    public JsonResponse<Page<HardwareWholeMachine>> getPage(@ParameterObject HardwareWholeMachineSelectVO selectVO, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        selectVO.setUserUuid(loginUuid);
        Page<HardwareWholeMachine> wholeMachinePage = wholeMachineApplicationService.getPage(selectVO);
        return JsonResponse.success(wholeMachinePage);
    }

    @Operation(summary = "根据id查询对象", parameters = {@Parameter(name = "id", description = "板卡id")})
    @GetMapping("/getById")
    public JsonResponse<HardwareWholeMachine> getById(@RequestParam("id") @NotNull(message = "id不能为空") Integer id, HttpServletRequest request) throws NoLoginException {
        HardwareWholeMachine wholeMachine = wholeMachineApplicationService.getById(id);
        return JsonResponse.success(wholeMachine);
    }

    @Operation(summary = "新增业务", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "新增业务对象", required = true))
    @PostMapping("/insert")
    public JsonResponse<InsertResponse> insert(@RequestBody @Valid HardwareWholeMachineAddCommand addCommand, HttpServletRequest request) throws NoLoginException, ParamException {
        String loginUuid = accountService.getLoginUuid(request);
        InsertResponse insert = wholeMachineApplicationService.insert(addCommand, loginUuid);
        return JsonResponse.success(insert);
    }

    @Operation(summary = "批量新增业务", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "批量新增业务对象", required = true))
    @PostMapping("/batchInsert")
    public JsonResponse<BatchInsertResponse> batchInsert(@RequestBody @Valid List<HardwareWholeMachineAddCommand> addCommandList, HttpServletRequest request) throws NoLoginException, ParamException {
        String loginUuid = accountService.getLoginUuid(request);
        BatchInsertResponse batchInsertResponse = wholeMachineApplicationService.batchInsert(addCommandList, loginUuid);
        return JsonResponse.success(batchInsertResponse);
    }

    /*@PostMapping("/uploadTemplate")
    @PreAuthorize("hasAnyRole('OSV_user')")
    public JsonResponse<ExcelInfoVo> uploadTemplate(@RequestParam("file") @NotNull(message = "模板文件不能为空")
                                                    MultipartFile file, HttpServletRequest request) throws InputException, NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        ExcelInfoVo excelInfoVo = wholeMachineApplicationService.uploadTemplate(file, uuid);
        return JsonResponse.success(excelInfoVo);
    }

    @GetMapping("/upload")
    @PreAuthorize("hasAnyRole('OSV_user')")
    public JsonResponse<BatchInsertResponse> readCompatibleData(HttpServletRequest request,
                                                                @RequestParam("fileId") @NotBlank(message = "附件id不能为空") @Length(max = 50, message = "附件id超出范围")
                                                                String fileId) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        BatchInsertResponse batchInsertResponse = wholeMachineApplicationService.readJsonData(fileId, loginUuid);
        return JsonResponse.success(batchInsertResponse);
    }*/

    @Operation(summary = "编辑")
    @PostMapping("/edit")
    public JsonResponse<Boolean> edit(@RequestBody @Valid HardwareWholeMachineEditCommand editCommand, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        wholeMachineApplicationService.edit(editCommand, loginUuid);
        return JsonResponse.success(null);
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    public JsonResponse<Boolean> delete(@RequestBody @Valid HardwareApprovalNode approvalNode, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        approvalNode.setHandlerUuid(Integer.valueOf(loginUuid));
        wholeMachineApplicationService.delete(approvalNode);
        return JsonResponse.success(null);
    }

    @Operation(summary = "申请")
    @PostMapping("/apply")
    public JsonResponse<Boolean> apply(@RequestBody @Valid HardwareApprovalNode approvalNode, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        approvalNode.setHandlerUuid(Integer.valueOf(loginUuid));
        wholeMachineApplicationService.apply(approvalNode);
        return JsonResponse.success(null);
    }

    @Operation(summary = "通过")
    @PostMapping("/pass")
    public JsonResponse<Boolean> pass(@RequestBody @Valid HardwareApprovalNode approvalNode, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        approvalNode.setHandlerUuid(Integer.valueOf(loginUuid));
        wholeMachineApplicationService.pass(approvalNode);
        return JsonResponse.success(null);
    }

    @Operation(summary = "驳回")
    @PostMapping("/reject")
    public JsonResponse<Boolean> reject(@RequestBody @Valid HardwareApprovalNode approvalNode, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        approvalNode.setHandlerUuid(Integer.valueOf(loginUuid));
        wholeMachineApplicationService.reject(approvalNode);
        return JsonResponse.success(null);
    }

    @Operation(summary = "关闭")
    @PostMapping("/close")
    public JsonResponse<Boolean> close(@RequestBody @Valid HardwareApprovalNode approvalNode, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        approvalNode.setHandlerUuid(Integer.valueOf(loginUuid));
        wholeMachineApplicationService.close(approvalNode);
        return JsonResponse.success(null);
    }

    @Operation(summary = "批量操作")
    @PostMapping("/batchApproval")
    public JsonResponse<Boolean> batchApproval(@RequestBody @Valid HardwareApprovalBatchCommand batchCommand, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        batchCommand.setHandlerUuid(Integer.valueOf(loginUuid));
        wholeMachineApplicationService.batchApproval(batchCommand);
        return JsonResponse.success(null);
    }

    @Operation(summary = "筛选条件")
    @GetMapping("/filterCriteria")
    public JsonResponse<JSONObject> findFilter() {
        JSONObject jsonObject = new JSONObject();
        List<String> os = wholeMachineApplicationService.getOs();
        jsonObject.set("os", os);
        return JsonResponse.success(jsonObject);
    }

    @Operation(summary = "整机数据公示")
    @GetMapping("/findAll")
    public JsonResponse<Page<HardwareWholeMachineListDto>> findAll(@ParameterObject HardwareWholeMachineSelectVO selectVO) {
        Page<HardwareWholeMachineListDto> wholeMachinePage = wholeMachineApplicationService.pageForCompatibilityList(selectVO);
        return JsonResponse.success(wholeMachinePage);
    }

    @Operation(summary = "整机数据公示")
    @GetMapping("/findById")
    public JsonResponse<HardwareWholeMachineDto> findById(Integer id) {
        HardwareWholeMachineDto hardwareWholeMachine = wholeMachineApplicationService.getByIdForCompatibilityList(id);
        return JsonResponse.success(hardwareWholeMachine);
    }
}
