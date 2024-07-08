/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.service.impl;

import com.google.common.collect.Maps;
import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.exception.InputException;
import com.huawei.it.euler.mapper.CompatibleDataMapper;
import com.huawei.it.euler.mapper.ProtocolMapper;
import com.huawei.it.euler.mapper.SoftwareMapper;
import com.huawei.it.euler.mapper.UserMapper;
import com.huawei.it.euler.model.constant.CompanyStatusConstant;
import com.huawei.it.euler.model.constant.CompatibleOperationConstant;
import com.huawei.it.euler.model.constant.CompatibleStatusConstant;
import com.huawei.it.euler.model.entity.*;
import com.huawei.it.euler.model.enumeration.ProtocolEnum;
import com.huawei.it.euler.model.enumeration.RoleEnum;
import com.huawei.it.euler.model.vo.*;
import com.huawei.it.euler.service.CompanyService;
import com.huawei.it.euler.service.CompatibleDataService;
import com.huawei.it.euler.util.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CompatibleDataServiceImpl
 *
 * @since 2024/07/04
 */
@Service
public class CompatibleDataServiceImpl implements CompatibleDataService {
    /**
     * 兼容性数据导入：默认公司名称
     */
    private static final String DEFAULT_COMPANY_NAME = "XXXX有限公司";

    /**
     * 兼容性数据导入模板相对路径
     */
    private static final String DATA_TEMPLATE = "/static/兼容性数据导入模板.xlsx";

    private static final HashMap<String, List<String>> SYSTEM_NAME_VERSION = new HashMap() {{
       put("KylinSec", Arrays.asList("3.4-5A", "3.5.1"));
       put("Kylin Linux Advanced Server", Arrays.asList("V10 SP1", "V10 SP2"));
       put("UOS Server", Arrays.asList("20 1020e", "20 1050e"));
       put("FusionOS", Collections.singletonList("22"));
    }};

    private static final HashMap<String, HashMap<String, List<String>>> SERVER = new HashMap() {{
        put("Kunpeng916", new HashMap<String, List<String>>() {{
            put("华为", Arrays.asList("Taishan 100", "Taishan200", "2288"));
            put("其他", Collections.singletonList("其他"));
        }});
        put("Kunpeng920", new HashMap<String, List<String>>() {{
            put("华为", Arrays.asList("Taishan 100", "Taishan200", "2288"));
            put("清华同方", Arrays.asList("超强K620", "超强Z520-M1"));
            put("宝德", Arrays.asList("自强PR210K", "自强PR212K"));
            put("百信", Arrays.asList("恒山TS02E-F30", "恒山TS02F-F30"));
            put("长虹", Arrays.asList("TG215 B1", "TG223 B1", "TG225 A1", "TG225 B1", "TG245 B1"));
            put("H3C", Collections.singletonList("R4960 G3"));
            put("黄河", Collections.singletonList("2280 V2"));
            put("海峡星云", Collections.singletonList("海峡星云"));
            put("中国普天", Arrays.asList("CP R1200K", "CP R2100K", "CP R2200K", "CP R2210K", "CP R2400K", "CP R4200K"));
            put("神州鲲泰", Arrays.asList("R522", "R722", "R524", "R724"));
            put("湘江鲲鹏", Collections.singletonList("衡山 R2102S"));
            put("广电运通", Collections.singletonList("200-RK2280"));
            put("长江计算", Arrays.asList("R220K V2", "R420K V2"));
            put("其他", Collections.singletonList("其他"));
        }});
        put("飞腾", new HashMap<String, List<String>>() {{
            put("浪潮", Collections.singletonList("浪潮英政CS5260F"));
            put("其他", Collections.singletonList("其他"));
        }});
        put("海光", new HashMap<String, List<String>>() {{
            put("中科曙光", Arrays.asList("H620-G30", "H620-G30A", "曙光龙腾L620-G25"));
            put("其他", Collections.singletonList("其他"));
        }});
        put("龙芯", new HashMap<String, List<String>>() {{
            put("中科曙光", Arrays.asList("H620-G30", "H620-G30A", "曙光龙腾L620-G25"));
            put("浪潮", Collections.singletonList("浪潮英政CS5260F"));
            put("天玥", Collections.singletonList("天玥天熠SR119220"));
            put("GITSTAR", Collections.singletonList("GITSTAR集特"));
            put("其他", Collections.singletonList("其他"));
        }});
        put("兆芯", new HashMap<String, List<String>>() {{
            put("清华同方", Arrays.asList("超强K620", "超强Z520-M1"));
            put("联想", Collections.singletonList("ThinkSystem SR658Z"));
            put("火星高科", Collections.singletonList("火星舱多节点服务器"));
            put("其他", Collections.singletonList("其他"));
        }});
        put("AMD", new HashMap<String, List<String>>() {{
            put("长城超云", Arrays.asList("R5218", "R2210-M1", "R2216"));
            put("其他", Collections.singletonList("其他"));
        }});
        put("Intel", new HashMap<String, List<String>>() {{
            put("华为", Arrays.asList("Taishan 100", "Taishan200", "2288"));
            put("超聚变", Collections.singletonList("5288 V3"));
            put("其他", Collections.singletonList("其他"));
        }});
    }};

    private static final HashMap<String, List<String>> PRODUCT_TYPE_SUBTYPE = new HashMap() {{
        put("基础软件", Arrays.asList("HPC", "分布式存储", "大数据", "虚拟化", "数据库", "Web", "操作系统",
                "CDN", "通用软件", "其他", "互联网"));
        put("行业软件", Arrays.asList("电力", "央国企", "金融", "运营商", "安平", "互联网", "数字政府", "其他"));
        put("硬件", Arrays.asList("整机", "DIMM", "RAID卡", "HDD", "SSD", "FC", "NIC", "IB", "GPU", "FPGA", "其他"));
    }};

    private static final List<String> REGION = Arrays.asList("北京", "天津", "山西", "河北", "辽宁", "江苏", "浙江", "上海",
            "福建", "江西", "山东", "中原", "湖南", "长江", "广州", "珠海", "深圳", "广西", "四川", "重庆", "贵州", "云南", "陕西", "甘肃");

    private static final HashSet<String> STATUS_APPROVAL_LIST = new HashSet<>(Arrays.asList("审核中", "已通过"));

    private static String updateStatus;

    @Autowired
    private CompatibleDataMapper compatibleDataMapper;

    @Resource
    private CompanyService companyService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SoftwareMapper softwareMapper;

    @Autowired
    private ProtocolMapper protocolMapper;

    @Autowired
    private ExcelUtils excelUtils;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private EncryptUtils encryptUtils;

    @Override
    public void downloadDataTemplate(HttpServletResponse response) throws IOException {
        try (InputStream inputStream = CompatibleDataServiceImpl.class.getResourceAsStream(DATA_TEMPLATE)) {
            response.reset();
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            response.addHeader("Access-Control-Allow-Headers", "Content-Type");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode("兼容性数据导入模板.xlsx", "UTF-8"));
            byte[] b = new byte[1024];
            int len;
            while ((len = inputStream.read(b)) > 0) {
                response.getOutputStream().write(b, 0, len);
            }
            response.getOutputStream().flush();
        }
    }

    @Override
    public JsonResponse<ExcelInfoVo> uploadTemplate(MultipartFile file, HttpServletRequest request)
            throws InputException {
        FileModel fileModel = fileUtils.uploadFile(file, null, 1, "", request);
        softwareMapper.insertAttachment(fileModel);
        String fileSize = file.getSize() / 1000 + "KB";
        ExcelInfoVo vo = new ExcelInfoVo(fileModel.getFileId(), fileModel.getFileName(), fileSize);
        return JsonResponse.success(vo);
    }

    @Override
    @Transactional
    public JsonResponse<FileDataVo> readCompatibleData(HttpServletResponse response, HttpServletRequest request,
                                                       String fileId) throws InputException, IllegalAccessException {
        String cookieUuid = UserUtils.getCookieUuid(request);
        String userUuid = encryptUtils.aesDecrypt(cookieUuid);
        EulerUser user = userMapper.findByUuid(userUuid);
        if (Objects.equals(user.getUseable(), 0)) {
            throw new InputException("您已注销账号！无法上传兼容性数据");
        }
        CompanyVo companyVo = companyService.findCompanyByUserUuid(userUuid);
        if (companyVo == null || !Objects.equals(companyVo.getStatus(), CompanyStatusConstant.COMPANY_PASSED)) {
            throw new InputException("上传兼容性数据需要完成企业实名认证");
        }
        Protocol protocol = protocolMapper.selectProtocolDesc(
                ProtocolEnum.COMPATIBILITY_LIST_USAGE_STATEMENT.getProtocolType(), userUuid);
        if (protocol == null || Objects.equals(protocol.getStatus(), 0)) {
            throw new InputException("上传兼容性数据需要签署兼容性清单使用声明");
        }
        Integer dataId = compatibleDataMapper.selectMaxDataId();
        dataId = dataId == null ? 0 : dataId;
        List<CompatibleDataInfo> excelInfo = excelUtils.getExcelInfo(fileId);
        List<CompatibleSimilarVo> similarVoList = compatibleDataMapper.selectDataByStatus();
        if (excelInfo == null || excelInfo.isEmpty()) {
            return JsonResponse.failed("未能获取到兼容性数据信息");
        }
        FileDataVo fileDataVo = checkDataIsSuccess(dataId, excelInfo, similarVoList, companyVo, user);
        return JsonResponse.success(fileDataVo);
    }

    private FileDataVo checkDataIsSuccess(int dataId, List<CompatibleDataInfo> excelInfo,
        List<CompatibleSimilarVo> similarVoList, CompanyVo companyVo, EulerUser user) throws IllegalAccessException {
        Date date = new Date();
        Integer row = 1;
        Integer successRows = 0;
        Integer failedRows = 0;
        List<Integer> failedData = new ArrayList<>();
        List<Integer> repeatData = new ArrayList<>();
        List<CompatibleSimilarVo> successDataList = new ArrayList<>();
        List<CompatibleDataInfo> dataLists  = new ArrayList<>();
        for (CompatibleDataInfo dataInfo : excelInfo) {
            row++;
            String companyName = dataInfo.getCompanyName();
            CompatibleSimilarVo vo = new CompatibleSimilarVo();
            BeanUtils.copyProperties(dataInfo, vo);
            if (!ObjectUtils.checkFieldAllNull(dataInfo) && !DEFAULT_COMPANY_NAME.equals(companyName)) {
                if (!checkDataInfo(dataInfo)) {
                    failedRows++; // 失败的为必填项为null，内容与下拉选择不符，字符过长
                    failedData.add(row);
                } else if (similarVoList.contains(vo) || successDataList.contains(vo)) {
                    failedRows++; // 表格里数据重复，已上传的已通过/审核中的数据重复
                    repeatData.add(row);
                    failedData.add(row);
                } else if (companyVo != null) {
                    dataInfo.setDataId(++dataId).setUploadCompany(companyVo.getCompanyName())
                            .setCreatedBy(user.getUsername()).setStatus("审核中").setApplyTime(date)
                            .setUpdateTime(date).setUuid(user.getUuid());
                    successRows++;
                    dataLists.add(dataInfo);
                    successDataList.add(vo);
                }
            }
        }
        if (!dataLists.isEmpty()) {
            compatibleDataMapper.insertDataList(dataLists);
            compatibleDataMapper.insertDataApproval(dataLists);
        }
        Collections.sort(failedData);
        return new FileDataVo().setSuccessRows(successRows).setFailedRows(failedRows).setFailedData(failedData)
                .setRepeatData(repeatData);
    }

    private boolean checkDataInfo(CompatibleDataInfo dataInfo) {
        String companyName = dataInfo.getCompanyName();
        String productName = dataInfo.getProductName();
        String productVersion = dataInfo.getProductVersion();
        String systemName = dataInfo.getSystemName();
        String systemVersion = dataInfo.getSystemVersion();
        String serverPlatform = dataInfo.getServerPlatform();
        String productType = dataInfo.getProductType();
        String productSubtype = dataInfo.getProductSubtype();
        String region = dataInfo.getRegion();
        List<String> list = new ArrayList<>();
        Collections.addAll(list, companyName, productName, productVersion, systemName, systemVersion, serverPlatform,
                productType, productSubtype, region);
        for (String str : list) {
            if (str == null || str.trim().isEmpty() || str.trim().length() > 2000) {
                return false;
            }
        }
        if (dataInfo.getIssuanceDate() == null) {
            return false;
        }
        String productFunctions = dataInfo.getProductFunctions();
        String usageScene = dataInfo.getUsageScene();
        String serverSupplier = dataInfo.getServerSupplier();
        String serverModel = dataInfo.getServerModel();
        for (String str : Arrays.asList(productFunctions, usageScene, serverSupplier, serverModel)) {
            if (str != null && str.trim().length() > 2000) {
                return false;
            }
        }
        return SYSTEM_NAME_VERSION.get(systemName).contains(systemVersion)
                && SERVER.get(serverPlatform).containsKey(serverSupplier)
                && SERVER.get(serverPlatform).get(serverSupplier).contains(serverModel)
                && PRODUCT_TYPE_SUBTYPE.get(productType).contains(productSubtype)
                && REGION.contains(region);
    }

    @Override
    public JsonResponse<Map<String, Object>> findDataList(CompatibleDataSearchVo searchVo, String cookieUuid) {
        String userUuid = encryptUtils.aesDecrypt(cookieUuid);
        List<Integer> role = compatibleDataMapper.selectUserRoleByUuid(userUuid);
        List<CompatibleDataInfo> dataList;
        if (role.contains(RoleEnum.OSV_USER.getRoleId())) {
            searchVo.setUuid(userUuid);
            dataList = compatibleDataMapper.getDataList(searchVo);
        } else {
            dataList = compatibleDataMapper.getDataList(searchVo);
        }
        List<CompatibleDataInfoVo> dataInfoVos = new ArrayList<>();
        for (CompatibleDataInfo dataInfo : dataList) {
            CompatibleDataInfoVo dataInfoVo = new CompatibleDataInfoVo();
            BeanUtils.copyProperties(dataInfo, dataInfoVo);
            // role=5是欧拉社区旗舰店角色，可审核
            if (CompatibleStatusConstant.UNDER_REVIEW.equals(dataInfoVo.getStatus())
                    && role.contains(RoleEnum.FLAG_STORE.getRoleId())) {
                dataInfoVo.setOperation(CompatibleOperationConstant.REVIEW);
            } else {
                dataInfoVo.setOperation(CompatibleOperationConstant.VIEW);
            }
            dataInfoVos.add(dataInfoVo);
        }
        Map<String, Object> hashMap = Maps.newHashMap();
        hashMap.put("data", ListPageUtils.getListPage(dataInfoVos, searchVo.getCurPage(), searchVo.getPageSize()));
        hashMap.put("total", dataInfoVos.size());
        return JsonResponse.success(hashMap);
    }

    @Override
    public JsonResponse<String> approvalCompatibleData(ApprovalDataVo vo, String cookieUuid) {
        String userUuid = encryptUtils.aesDecrypt(cookieUuid);
        EulerUser user = userMapper.findByUuid(userUuid);
        List<Integer> dataIdList = vo.getDataIdList();
        Integer handlerResult = vo.getHandlerResult();
        List<String> statusList = compatibleDataMapper.getStatus(dataIdList);
        if (statusList.size() > 1) {
            return JsonResponse.failedResult("不能批量审核不同状态的数据");
        }
        if (!STATUS_APPROVAL_LIST.containsAll(statusList)) {
            return JsonResponse.failedResult("不能审核已驳回状态的数据");
        }
        if (handlerResult == 1 && statusList.containsAll(Arrays.asList("已通过", "已驳回"))) {
            return JsonResponse.failedResult("不能审核通过该状态的数据");
        }
        Date date = new Date();
        updateStatus = handlerResult == 1 ? "已通过" : "已驳回";
        compatibleDataMapper.updateDataInfo(dataIdList, updateStatus, date);
        List<CompatibleDataInfo> dataInfoList = compatibleDataMapper.selectDataInfoDetail(dataIdList);
        List<CompatibleDataApproval> dataApprovalList = dataInfoList.stream().map(dataInfo -> {
            CompatibleDataApproval dataApproval = new CompatibleDataApproval();
            BeanUtils.copyProperties(dataInfo, dataApproval, "status");
            dataApproval.setHandler(user.getUsername())
                    .setHandlerResult(handlerResult)
                    .setHandlerComment(vo.getHandlerComment())
                    .setHandlerTime(date)
                    .setStatus(updateStatus);
            return dataApproval;
        }).collect(Collectors.toList());
        compatibleDataMapper.insertApprovalInfo(dataApprovalList);
        return JsonResponse.success();
    }
}
