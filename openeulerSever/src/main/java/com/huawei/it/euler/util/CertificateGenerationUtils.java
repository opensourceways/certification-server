/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.util;

import com.alibaba.fastjson.JSONObject;
import com.huawei.it.euler.exception.InputException;
import com.huawei.it.euler.mapper.SoftwareMapper;
import com.huawei.it.euler.model.entity.FileModel;
import com.huawei.it.euler.model.entity.GenerateCertificate;
import com.huawei.it.euler.model.vo.ComputingPlatformVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wiki.xsx.core.pdf.doc.XEasyPdfDocument;
import wiki.xsx.core.pdf.doc.XEasyPdfPage;
import wiki.xsx.core.pdf.doc.XEasyPdfPositionStyle;
import wiki.xsx.core.pdf.handler.XEasyPdfHandler;
import wiki.xsx.core.pdf.util.XEasyPdfImageUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 生成pdf证书util
 *
 * @since 2024/07/02
 */
@Component
@Slf4j
public class CertificateGenerationUtils {
    private static final String BKGPDF = "/static/euler_page_front.pdf";

    private static final String DOC = "/doc/msyh.ttc,0";

    private static final String DOCBD = "/doc/msyhbd.ttc,0";

    private static final String OPEN_EULER = "openEuler";

    /**
     * 证书每行最长长度
     */
    private static final Integer MAXIMUM_LENGTH = 60;

    @Value("${certificates.pdfSavePath}")
    private String pdfSavePath;

    @Resource
    private SoftwareMapper softwareMapper;

    @Autowired
    private S3Utils s3Utils;

    /**
     * 获取图片路径
     *
     * @param generateCertificate 证书信息
     * @return 路径
     */
    private String getSignedFileId(GenerateCertificate generateCertificate) {
        return softwareMapper.getSignedFileId(generateCertificate.getId());
    }

    public void generateCertificate(GenerateCertificate generateCertificate) throws IOException{
        String code = "E" + new SimpleDateFormat("yyyyMM").format(new Date()) + "E" + String.valueOf(
                generateCertificate.getId() + 100000).substring(1);
        String path = pdfSavePath + code + ".pdf";
        try (InputStream inputStream = CertificateGenerationUtils.class.getResourceAsStream(BKGPDF);
            XEasyPdfDocument document = getDocument(inputStream, generateCertificate, path)) {
            XEasyPdfPage page = document.getPageList().get(0);
            XEasyPdfPage page1 = document.getPageList().get(1);
            this.generateFront(page, generateCertificate, false, code);
            this.generateSecond(page1, generateCertificate, code);
            document.setFontPath(DOC).save(path);
        }
        String fileId = UUID.randomUUID().toString();
        try (InputStream inputStream = Files.newInputStream(Paths.get(path))) {
            s3Utils.uploadFile(inputStream, fileId);
        } finally {
            deleteDir(path);
        }
        String fileName = code + ".pdf";
        FileModel model = new FileModel();
        model.setFileId(fileId);
        model.setFileName(fileName);
        model.setUpdateTime(new Date());
        model.setSoftwareId(generateCertificate.getId());
        model.setFileType("certificates");
        softwareMapper.insertAttachment(model);
    }

    private XEasyPdfDocument getDocument(InputStream inputStream, GenerateCertificate generateCertificate,
                                         String path) throws IOException {
        XEasyPdfDocument document;
        if (!StringUtils.isNotEmpty(getSignedFileId(generateCertificate))) {
            document = XEasyPdfHandler.Document.load(inputStream);
        } else {
            XEasyPdfHandler.Document
                    .load(inputStream)
                    .replacer()
                    .replaceImage(XEasyPdfImageUtil.read(s3Utils.downloadFile(getSignedFileId(generateCertificate))),
                            Arrays.asList(1, 2), 0)
                    .finish(path);
            document = XEasyPdfHandler.Document.load(path);
        }
        return document;
    }

    /**
     * 预览证书方法
     *
     * @param generateCertificate 证书
     * @param response 响应
     */
    public void previewCertificate(GenerateCertificate generateCertificate, HttpServletResponse response)
            throws InputException, IOException {
        GenerateCertificate certificate = new GenerateCertificate();
        BeanUtils.copyProperties(generateCertificate, certificate);
        certificate.setProductName(generateCertificate.getProductName().trim());
        certificate.setProductVersion(generateCertificate.getProductVersion().trim());
        String dirPath = UUID.randomUUID().toString();
        String fileName = certificate.getCompanyName() + certificate.getId();
        String path = pdfSavePath + dirPath + "/" + fileName + ".pdf";
        String code = "E20xxxxxxxxxx";
        try (InputStream inputStream = CertificateGenerationUtils.class.getResourceAsStream(BKGPDF);
            XEasyPdfDocument document = getDocument(inputStream, generateCertificate, path)) {
                XEasyPdfPage page = document.getPageList().get(0);
                XEasyPdfPage page1 = document.getPageList().get(1);
                this.generateFront(page, generateCertificate, false, code);
                this.generateSecond(page1, generateCertificate, code);
                document.setFontPath(DOC).save(path);
        }
        try (InputStream inputStream = Files.newInputStream(Paths.get(path))) {
            writeResponse(response, inputStream);
        } finally {
            deleteDir(pdfSavePath + dirPath);
        }
    }

    private void writeResponse(HttpServletResponse response, InputStream inStream) throws IOException {
        response.reset();
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("application/pdf;charset=utf-8");
        byte[] b = new byte[1024];
        int len;
        while ((len = inStream.read(b)) > 0) {
            response.getOutputStream().write(b, 0, len);
        }
        response.getOutputStream().flush();
    }

    /**
     * 生成证书第一页
     *
     * @param page 页面
     * @param generateCertificate 页面属性
     * @param isPreview 是否预览
     * @param code 证书编码
     */
    private void generateFront(XEasyPdfPage page, GenerateCertificate generateCertificate, Boolean isPreview,
                               String code) {
        if (page == null) {
            log.info("#None normal#generateFront#page is null");
            return;
        }
        page.addComponent(XEasyPdfHandler.Text.build(
                OPEN_EULER + "版本：" + generateCertificate.getOsName() + " " + generateCertificate.getOsVersion())
                .setHorizontalStyle(XEasyPdfPositionStyle.CENTER).setMarginTop(340F),
                XEasyPdfHandler.Text.build("完成兼容性测试").setHorizontalStyle(XEasyPdfPositionStyle.CENTER)
                        .setMarginTop(80F));
        String validityPeriod = generateCertificate.getValidityPeriod();
        if (StringUtils.isEmpty(validityPeriod)) {
            page.addComponent(XEasyPdfHandler.Text.build("有效期：20xx年xx月-20xx年xx月")
                    .setHorizontalStyle(XEasyPdfPositionStyle.CENTER).setMarginTop(80F));
        } else {
            page.addComponent(XEasyPdfHandler.Text.build("有效期：" + validityPeriod)
                    .setHorizontalStyle(XEasyPdfPositionStyle.CENTER).setMarginTop(80F));
        }
        getFirstComponent(page, generateCertificate, isPreview, code);
        // 处理超长算力平台
        getNewHashratePlatForm(page, generateCertificate);
        // 处理超长公司名
        getNewCompanyNameOfLong(page, generateCertificate);
        // 处理公司名+使用权
        getNewCompanyLine(page, generateCertificate);
        // 处理首行公司名称
        getFirstLineCompanyName(page, generateCertificate);
        // 处理第二行超长产品名
        getSecondLineProductName(page, generateCertificate);
    }

    private void getFirstComponent(XEasyPdfPage page, GenerateCertificate generateCertificate, Boolean isPreview,
                                   String code) {
        page.addComponent(XEasyPdfHandler.Text.build(OPEN_EULER + "开源社区")
                .setHorizontalStyle(XEasyPdfPositionStyle.LEFT).setMarginTop(18F)
                .setMarginLeft(310f), XEasyPdfHandler.Text.build("江大勇")
                .setHorizontalStyle(XEasyPdfPositionStyle.LEFT)
                .setMarginTop(70F).setMarginLeft(310f),
                XEasyPdfHandler.Text.build(OPEN_EULER + "开源社区理事长").setHorizontalStyle(XEasyPdfPositionStyle.LEFT)
                        .setMarginLeft(310f),
                XEasyPdfHandler.Text.build("发证机构：" + OPEN_EULER + "开源社区")
                        .setHorizontalStyle(XEasyPdfPositionStyle.LEFT).setMarginLeft(310f),
                XEasyPdfHandler.Text.build("测试机构：" + generateCertificate.getTestOrganization())
                        .setHorizontalStyle(XEasyPdfPositionStyle.LEFT).setMarginLeft(310f));
        if (isPreview) {
            page.addComponent(XEasyPdfHandler.Text.build("授予日期：20xx年xx月xx日")
                    .setHorizontalStyle(XEasyPdfPositionStyle.LEFT).setMarginLeft(310f));
        } else {
            page.addComponent(XEasyPdfHandler.Text.build("授予日期：" + new SimpleDateFormat("yyyy年MM月dd日")
                            .format(new Date())).setHorizontalStyle(XEasyPdfPositionStyle.LEFT).setMarginLeft(310f));
        }
        page.addComponent(XEasyPdfHandler.Text.build("证书编号：" + code)
                .setHorizontalStyle(XEasyPdfPositionStyle.LEFT).setMarginLeft(310f));
    }

    /**
     * 处理超长算力平台
     *
     * @param page 页面
     * @param generateCertificate 证书字段
     */
    private void getNewHashratePlatForm(XEasyPdfPage page, GenerateCertificate generateCertificate) {
        String newHashratePlatform = getNewHashratePlatform(generateCertificate.getHashratePlatform());
        String[] split = newHashratePlatform.split("/");
        page.addComponent(XEasyPdfHandler.Text.build("算力平台")
                .setHorizontalStyle(XEasyPdfPositionStyle.CENTER).setPosition(0, 470));
        // 选择一个算力平台
        if (split.length == 1) {
            page.addComponent(XEasyPdfHandler.Text.build(String.valueOf(split[0]))
                    .setHorizontalStyle(XEasyPdfPositionStyle.CENTER));
            return;
        }
        // 选择多个算力平台
        int remainNum = split.length; // 未输出的算力平台个数
        for (int i = 0; i < split.length - 1; i++) {
            // 证书格式：每行输出两个算力平台，如果过长，则输出一个
            StringBuilder platform = new StringBuilder();
            if (remainNum > 1) {
                platform.append(split[i]).append("、").append(split[i + 1]);
            }
            if (platform.length() <= MAXIMUM_LENGTH && remainNum >= 2) {
                page.addComponent(XEasyPdfHandler.Text.build(String.valueOf(platform))
                        .setHorizontalStyle(XEasyPdfPositionStyle.CENTER));
                remainNum -= 2;
                if (remainNum != 1) {
                    i++;
                }
            } else if (platform.length() >= MAXIMUM_LENGTH) {
                page.addComponent(XEasyPdfHandler.Text.build(String.valueOf(split[i]))
                        .setHorizontalStyle(XEasyPdfPositionStyle.CENTER));
                remainNum -= 1;
            } else {
                page.addComponent(XEasyPdfHandler.Text.build(String.valueOf(split[i + 1]))
                        .setHorizontalStyle(XEasyPdfPositionStyle.CENTER));
                remainNum -= 1;
            }
        }
    }

    /**
     * 算力平台展示处理
     *
     * @param hashratePlatform 算力平台
     * @return 处理结果
     */
    private String getNewHashratePlatform(String hashratePlatform) {
        List<ComputingPlatformVo> platformVos = JSONObject.parseArray(hashratePlatform)
                .toJavaList(ComputingPlatformVo.class);
        StringBuffer stringBuffer = new StringBuffer();
        platformVos.forEach(
                item -> stringBuffer.append(item.getPlatformName()).append(item.getServerTypes()).append("/"));
        return stringBuffer.substring(0, stringBuffer.lastIndexOf("/"))
                .replace("[", "(").replace("]", ")");
    }

    /**
     * 处理超长公司名（签名上方）
     *
     * @param page 页面
     * @param generateCertificate 证书字段
     */
    private void getNewCompanyNameOfLong(XEasyPdfPage page, GenerateCertificate generateCertificate) {
        String companyName = generateCertificate.getCompanyName();
        int length = companyName.length();
        if (length > 10) {
            if (length < 18) {
                page.addComponent(XEasyPdfHandler.Text.build(companyName).setPosition(125f - (length - 10) * 5, 255f));
            } else {
                int i = length / 2;
                if (length % 2 == 0) {
                    page.addComponent(
                            XEasyPdfHandler.Text.build(companyName.substring(0, i))
                                    .setPosition(125f - (length - 10) * 5, 255f),
                            XEasyPdfHandler.Text.build(companyName.substring(i, length))
                                    .setPosition(125f - (length - 10) * 5, 240f));
                } else {
                    page.addComponent(
                            XEasyPdfHandler.Text.build(companyName.substring(0, i + 1))
                                    .setPosition(125f - (length - 10) * 5, 255f),
                            XEasyPdfHandler.Text.build(companyName.substring(i + 1, length))
                                    .setPosition(125f - (length - 10) * 5, 240f));
                }
            }
        } else {
            page.addComponent(XEasyPdfHandler.Text.build(companyName).setPosition(125f, 255f));
        }
    }

    /**
     * 处理公司名+使用权
     *
     * @param page 页面
     * @param generateCertificate 证书字段
     */
    private void getNewCompanyLine(XEasyPdfPage page, GenerateCertificate generateCertificate) {
        String companyLine = generateCertificate.getCompanyName() + "本证明及相关测评徽标使用权";
        processingOverlongFields(companyLine, 320f, 340f, page);
    }

    /**
     * 处理首行公司名称
     *
     * @param page 页面
     * @param generateCertificate 证书字段
     */
    private void getFirstLineCompanyName(XEasyPdfPage page, GenerateCertificate generateCertificate) {
        String companyName = generateCertificate.getCompanyName();
        processingOverlongFields(companyName, 580f, 580f, page);
    }

    /**
     * 处理第二行产品名称
     *
     * @param page 页面
     * @param generateCertificate 证书字段
     */
    private void getSecondLineProductName(XEasyPdfPage page, GenerateCertificate generateCertificate) {
        String productNameAndVersion = generateCertificate.getProductName() + " " + generateCertificate.getProductVersion();
        processingOverlongFields(productNameAndVersion, 550f, 545f, page);
    }

    /**
     * 处理超长字段公共方法
     *
     * @param fields 处理字段
     * @param oldBeginY 字段不超长在页面定位的高
     * @param newBeginY 字段超长在页面定位的高
     * @param page 页面
     */
    private void processingOverlongFields(String fields, float oldBeginY, float newBeginY, XEasyPdfPage page) {
        int length = fields.length();
        if (length > 40) {
            int i = length / 2;
            page.addComponent(
                    XEasyPdfHandler.Text.build(fields.substring(0, i))
                            .setPosition(0, newBeginY)
                            .setHorizontalStyle(XEasyPdfPositionStyle.CENTER),
                    XEasyPdfHandler.Text.build(fields.substring(i, length))
                            .setHorizontalStyle(XEasyPdfPositionStyle.CENTER));
        } else {
            page.addComponent(XEasyPdfHandler.Text.build(fields)
                    .setPosition(0, oldBeginY)
                    .setHorizontalStyle(XEasyPdfPositionStyle.CENTER));
        }
    }

    /**
     * 生成第二页内容
     *
     * @param page 页面
     * @param generateCertificate 证书字段
     * @param code 证书编号
     */
    private void generateSecond(XEasyPdfPage page, GenerateCertificate generateCertificate, String code) {
        secondPageFirstLine(page, generateCertificate);
        page.addComponent(XEasyPdfHandler.Text.build("根据认证结果，现双方根据以下条款授权对方使用本技术兼容性证明，且甲方授权乙方使用甲")
                .setMarginLeft(50F).setMarginTop(30F).setFontPath(DOCBD),
                XEasyPdfHandler.Text.build("方的认证徽标").setMarginLeft(50F).setFontPath(DOCBD),
                XEasyPdfHandler.Text.build("一、甲方授权乙方使用该证明，且甲方授权乙方使用其认证徽标，")
                        .setMarginLeft(50F).setMarginTop(10F).setFontPath(DOCBD),
                XEasyPdfHandler.Text.build("本证明及认证徽标使用许可为非独占、不可转让的普通许可，")
                        .setMarginLeft(74F).setFontPath(DOCBD),
                XEasyPdfHandler.Text.build("具体授权内容如下：")
                        .setMarginLeft(74F).setFontPath(DOCBD),
                XEasyPdfHandler.Text.build("1. 授权使用条件：")
                        .setMarginTop(10F).setMarginLeft(90F).setFontPath(DOCBD).enableChildComponent(),
                XEasyPdfHandler.Text.build("仅用于证明甲方与乙方就证书所列产品完成基于" + OPEN_EULER + "开源操作系统")
                        .setMarginTop(-17F).setMarginLeft(185F),
                XEasyPdfHandler.Text.build("的兼容性测试").setMarginLeft(185F),
                XEasyPdfHandler.Text.build("2. 授权使用范围：")
                        .setMarginTop(10F).setMarginLeft(90F).setFontPath(DOCBD).enableChildComponent(),
                XEasyPdfHandler.Text.build("在甲方许可方位内使用该证明及认证徽标。").setMarginTop(-17F).setMarginLeft(185F),
                XEasyPdfHandler.Text.build("3. 授权使用时间：")
                        .setMarginTop(10F).setMarginLeft(90F).setFontPath(DOCBD).enableChildComponent(),
                XEasyPdfHandler.Text.build("自证书颁发之日起36个月。").setMarginTop(-17F).setMarginLeft(185F),
                XEasyPdfHandler.Text.build("4. 授权使用方式：")
                        .setMarginTop(10F).setMarginLeft(90F).setFontPath(DOCBD).enableChildComponent(),
                XEasyPdfHandler.Text.build("本证明及认证徽标只能用于说明甲方与乙方产品兼容的目的，双方")
                        .setMarginTop(-17F).setMarginLeft(185F),
                XEasyPdfHandler.Text.build("不得滥用该证明用于其他任何目的。乙方必须严格按照甲方提供的").setMarginLeft(185F),
                XEasyPdfHandler.Text.build("认证徽标图样使用，不得擅自对图样作任何修改。").setMarginLeft(185F),
                XEasyPdfHandler.Text.build("二、未经对方书面同意，双方均不得超出具体授权内容使用本证明及认证徽章。双方授权的使")
                        .setMarginLeft(50F).setMarginTop(10F).setFontPath(DOCBD),
                XEasyPdfHandler.Text.build("用许可，不构成双方任何已有权利的转移。")
                        .setMarginLeft(74F).setFontPath(DOCBD),
                XEasyPdfHandler.Text.build("三、除第一条明确授权外，本证明不包括任何其他授权或许可，也不应被解释为在甲方和乙方")
                        .setMarginLeft(50F).setMarginTop(20F).setFontPath(DOCBD),
                XEasyPdfHandler.Text.build("间创设或者形成任何其他关系；任何一方若违反本认证书的任何约定，对方有权随时单方")
                        .setMarginLeft(74F).setFontPath(DOCBD),
                XEasyPdfHandler.Text.build("撤回本证明的所有授权。")
                        .setMarginLeft(74F).setFontPath(DOCBD),
                XEasyPdfHandler.Text.build("四、授权认证徽标图样")
                        .setMarginLeft(50F).setMarginTop(20F).setFontPath(DOCBD),
                XEasyPdfHandler.Text.build("本证书自甲方发布之日起生效")
                        .setHorizontalStyle(XEasyPdfPositionStyle.RIGHT).setMarginRight(50F).setMarginTop(90F),
                XEasyPdfHandler.Text.build("证书编号：" + code)
                        .setHorizontalStyle(XEasyPdfPositionStyle.RIGHT).setMarginRight(50F));
    }

    /**
     * 第二页第一行超长处理
     *
     * @param page 页面
     * @param generateCertificate 证书字段
     */
    private void secondPageFirstLine(XEasyPdfPage page, GenerateCertificate generateCertificate) {
        if (page == null) {
            log.info("#DealAdvanceed#page null, return.");
            return;
        }
        String str = OPEN_EULER + "开源社区（甲方）与" + generateCertificate.getCompanyName() + "（乙方）完成了产品兼容性测试。";
        int length = str.length();
        if (length > 43) {
            page.addComponent(
                    XEasyPdfHandler.Text.build(str.substring(0, 42))
                            .setMarginLeft(50F)
                            .setMarginTop(180F)
                            .setFontPath(DOCBD),
                    XEasyPdfHandler.Text.build(str.substring(42, length))
                            .setMarginLeft(50F)
                            .setFontPath(DOCBD));
        } else {
            page.addComponent(
                    XEasyPdfHandler.Text.build(str)
                            .setMarginLeft(50F)
                            .setMarginTop(180F)
                            .setFontPath(DOCBD));
        }
    }

    private Boolean deleteDir(String path) {
        File dir = new File(path);
        if (!dir.isDirectory()) {
            return false;
        }
        File[] listFiles = dir.listFiles();
        for (File file : listFiles) {
            if (!file.delete()) {
                log.info("delete file failed, fileName: {}", file.getName());
            }
        }
        return dir.delete();
    }
}
