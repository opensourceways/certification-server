/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.interfaces;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.ddd.domain.hardware.*;
import com.huawei.it.euler.ddd.service.*;
import com.huawei.it.euler.exception.InputException;
import com.huawei.it.euler.exception.NoLoginException;
import com.huawei.it.euler.exception.ParamException;
import com.huawei.it.euler.model.vo.ExcelInfoVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "板卡接口", description = "硬件板卡业务接口")
@RestController
@RequestMapping("/hardwareBoardCard")
public class HardwareBoardCardApi {

    @Autowired
    private HardwareBoardCardApplicationService boardCardApplicationService;

    @Autowired
    private AccountService accountService;

    @Operation(summary = "分页查询")
    @GetMapping("/getPage")
    public JsonResponse<Page<HardwareBoardCard>> getPage(@ParameterObject HardwareBoardCardSelectVO selectVO, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        selectVO.setUserUuid(loginUuid);
        Page<HardwareBoardCard> wholeMachinePage = boardCardApplicationService.getPage(selectVO);
        return JsonResponse.success(wholeMachinePage);
    }

    @Operation(summary = "根据id查询对象", parameters = {@Parameter(name = "id", description = "板卡id")})
    @GetMapping("/getById")
    public JsonResponse<HardwareBoardCard> getById(@RequestParam("id") @NotNull(message = "id不能为空") Integer id, HttpServletRequest request) throws NoLoginException {
        HardwareBoardCard wholeMachine = boardCardApplicationService.getById(id);
        return JsonResponse.success(wholeMachine);
    }

    @Operation(summary = "新增业务", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "新增业务对象", required = true))
    @PostMapping("/insert")
    public JsonResponse<InsertResponse> insert(@RequestBody @Valid HardwareBoardCardAddCommand addCommand, HttpServletRequest request) throws NoLoginException, ParamException {
        String loginUuid = accountService.getLoginUuid(request);
        InsertResponse insert = boardCardApplicationService.insert(addCommand, loginUuid);
        return JsonResponse.success(insert);
    }

    @Operation(summary = "批量新增业务", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "批量新增业务对象", required = true))
    @PostMapping("/batchInsert")
    public JsonResponse<BatchInsertResponse> batchInsert(@RequestBody @Valid List<HardwareBoardCardAddCommand> addCommandList, HttpServletRequest request) throws NoLoginException, ParamException {
        String loginUuid = accountService.getLoginUuid(request);
        BatchInsertResponse batchInsertResponse = boardCardApplicationService.batchInsert(addCommandList, loginUuid);
        return JsonResponse.success(batchInsertResponse);
    }

    /*@Operation(summary = "批量上传-上传文件")
    @PostMapping(value = "/uploadTemplate",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public JsonResponse<ExcelInfoVo> uploadTemplate(@RequestParam("file") @NotNull(message = "模板文件不能为空") MultipartFile file,
                                                    HttpServletRequest request) throws InputException, NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        ExcelInfoVo excelInfoVo = boardCardApplicationService.uploadTemplate(file, uuid);
        return JsonResponse.success(excelInfoVo);
    }*/

    /*@Operation(summary = "批量上传-读取文件")
    @GetMapping("/upload")
    public JsonResponse<BatchInsertResponse> readCompatibleData(HttpServletRequest request,
                                                                @RequestParam("fileId") @NotBlank(message = "附件id不能为空") @Length(max = 50, message = "附件id超出范围")
                                                                String fileId) throws NoLoginException {
        String uuid = accountService.getLoginUuid(request);
        BatchInsertResponse batchInsertResponse = boardCardApplicationService.readJsonData(fileId, uuid);
        return JsonResponse.success(batchInsertResponse);
    }*/

    @Operation(summary = "编辑")
    @PostMapping("/edit")
    public JsonResponse<Boolean> edit(@RequestBody @Valid HardwareBoardCardEditCommand editCommand, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        boardCardApplicationService.edit(editCommand, loginUuid);
        return JsonResponse.success(null);
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    public JsonResponse<Boolean> delete(@RequestBody @Valid HardwareApprovalNode approvalNode, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        approvalNode.setHandlerUuid(Integer.valueOf(loginUuid));
        boardCardApplicationService.delete(approvalNode);
        return JsonResponse.success(null);
    }

    @Operation(summary = "申请")
    @PostMapping("/apply")
    public JsonResponse<Boolean> apply(@RequestBody @Valid HardwareApprovalNode approvalNode, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        approvalNode.setHandlerUuid(Integer.valueOf(loginUuid));
        boardCardApplicationService.apply(approvalNode);
        return JsonResponse.success(null);
    }

    @Operation(summary = "通过")
    @PostMapping("/pass")
    public JsonResponse<Boolean> pass(@RequestBody @Valid HardwareApprovalNode approvalNode, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        approvalNode.setHandlerUuid(Integer.valueOf(loginUuid));
        boardCardApplicationService.pass(approvalNode);
        return JsonResponse.success(null);
    }

    @Operation(summary = "驳回")
    @PostMapping("/reject")
    public JsonResponse<Boolean> reject(@RequestBody @Valid HardwareApprovalNode approvalNode, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        approvalNode.setHandlerUuid(Integer.valueOf(loginUuid));
        boardCardApplicationService.reject(approvalNode);
        return JsonResponse.success(null);
    }

    @Operation(summary = "关闭")
    @PostMapping("/close")
    public JsonResponse<Boolean> close(@RequestBody @Valid HardwareApprovalNode approvalNode, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        approvalNode.setHandlerUuid(Integer.valueOf(loginUuid));
        boardCardApplicationService.close(approvalNode);
        return JsonResponse.success(null);
    }

    @Operation(summary = "批量操作")
    @PostMapping("/batchApproval")
    public JsonResponse<Boolean> batchApproval(@RequestBody @Valid HardwareApprovalBatchCommand batchCommand, HttpServletRequest request) throws NoLoginException {
        String loginUuid = accountService.getLoginUuid(request);
        batchCommand.setHandlerUuid(Integer.valueOf(loginUuid));
        boardCardApplicationService.batchApproval(batchCommand);
        return JsonResponse.success(null);
    }

    @Operation(summary = "板卡数据公示")
    @GetMapping("/findAll")
    public JsonResponse<Page<HardwareBoardCardDto>> findAll(@ParameterObject HardwareBoardCardSelectVO selectVO) {
        Page<HardwareBoardCardDto> boardCardPage = boardCardApplicationService.pageForCompatibilityList(selectVO);
        return JsonResponse.success(boardCardPage);
    }
}
