/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.util;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.HexUtil;
import com.huawei.it.euler.exception.InputException;
import com.huawei.it.euler.model.entity.FileModel;
import com.obs.services.internal.ServiceException;
import lombok.extern.slf4j.Slf4j;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * FileUtils
 *
 * @since 2024/07/02
 */
@Slf4j
@Component
public class FileUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 文件大小
     */
    private static final Long FILE_SIZE = 1024 * 1024 * 50L;

    /**
     * logo和营业执照文件大小
     */
    private static final Long LOGO_LICENSE_FILE_SIZE = 1024 * 1024 * 10L;

    @Autowired
    private S3Utils s3Utils;

    @Autowired
    private EncryptUtils encryptUtils;

    private static final HashMap<String, String> M_FILE_TYPE_MAP = new HashMap<>();

    static {
        M_FILE_TYPE_MAP.put("JPG", "FFD8FF");
        M_FILE_TYPE_MAP.put("JPEG", "FFD8FF");
        M_FILE_TYPE_MAP.put("PNG", "89504E47");
        M_FILE_TYPE_MAP.put("GIF", "47494638");
        M_FILE_TYPE_MAP.put("TIF", "49492A00");
        M_FILE_TYPE_MAP.put("BMP", "424D");
        M_FILE_TYPE_MAP.put("DWG", "41433130");
        M_FILE_TYPE_MAP.put("PSD", "38425053");
        M_FILE_TYPE_MAP.put("RTF", "7B5C727466");
        M_FILE_TYPE_MAP.put("XML", "3C3F786D6C");
        M_FILE_TYPE_MAP.put("HTML", "68746D6C3E");
        M_FILE_TYPE_MAP.put("EML", "44656C69766572792D646174653A");
        M_FILE_TYPE_MAP.put("DOC", "D0CF11E0");
        M_FILE_TYPE_MAP.put("XLS", "D0CF11E0");
        M_FILE_TYPE_MAP.put("MDB", "5374616E64617264204A");
        M_FILE_TYPE_MAP.put("PS", "252150532D41646F6265");
        M_FILE_TYPE_MAP.put("PDF", "255044462D312E");
        M_FILE_TYPE_MAP.put("DOCX", "504B0304");
        M_FILE_TYPE_MAP.put("XLSX", "504B0304");
        M_FILE_TYPE_MAP.put("RAR", "52617221");
        M_FILE_TYPE_MAP.put("WAV", "57415645");
        M_FILE_TYPE_MAP.put("AVI", "41564920");
        M_FILE_TYPE_MAP.put("RM", "2E524D46");
        M_FILE_TYPE_MAP.put("MPG", "000001BA");
        M_FILE_TYPE_MAP.put("MOV", "6D6F6F76");
        M_FILE_TYPE_MAP.put("ASF", "3026B2758E66CF11");
        M_FILE_TYPE_MAP.put("MID", "4D546864");
        M_FILE_TYPE_MAP.put("GZ", "1F8B08");
        M_FILE_TYPE_MAP.put("ZIP", "504B0304");
        M_FILE_TYPE_MAP.put("MP4", "00000020667479706D70");
        M_FILE_TYPE_MAP.put("7Z", "7F454C46");
    }

    /**
     * 文件上传
     *
     * @param file 文件
     * @param softwareId 软件id
     * @param fileTypeCode 文件类型编码
     * @param fileType 文件类型，logo，执照license，测试报告testReport，签名sign，证书certificates
     * @param request request
     * @return 文件的存储路径
     */
    public FileModel uploadFile(MultipartFile file, Integer softwareId, Integer fileTypeCode,
                                String fileType, HttpServletRequest request) throws InputException {
        checkFileSize(file, fileType);
        FileModel fileModel = new FileModel();
        fileModel.setSoftwareId(softwareId);
        String originalFilename = file.getOriginalFilename();
        List<String> typeList = Collections.emptyList();
        // 通过前端传入的文件类型编码判断传入类型是否满足要求：1-文件，2-图片
        switch (fileTypeCode) {
            case 1:
                typeList = Lists.newArrayList("PDF", "XLSX", "XLS", "DOCX", "DOC", "ZIP");
                break;
            case 2:
                typeList = Lists.newArrayList("PNG", "JPG", "JPEG", "BMP");
                break;
            default:
                break;
        }
        if (CollectionUtils.isEmpty(typeList)) {
            throw new InputException("文件类型不符合要求！");
        }
        if (StringUtils.isEmpty(originalFilename)) {
            throw new InputException("文件名有误！");
        }
        boolean checkFileExtension = checkFileExtension(file, typeList);
        if (checkFileExtension) {
            fileModel.setFileType(fileType);
            fileModel.setFileName(originalFilename);
        } else {
            throw new InputException("文件类型不符合要求！");
        }
        String fileId = UUID.randomUUID().toString();
        fileModel.setFileId(fileId);
        try {
            s3Utils.uploadFile(file, fileId);
            fileModel.setUpdateTime(new Date());
            String cookieUuid = UserUtils.getCookieUuid(request);
            fileModel.setUuid(encryptUtils.aesDecrypt(cookieUuid));
            return fileModel;
        } catch (IOException e) {
            throw new InputException("上传失败" + e.getMessage());
        }
    }

    private static void checkFileSize(MultipartFile file, String fileType) throws InputException {
        long size = Objects.equals(fileType, "logo") || Objects.equals(fileType, "license")
                ? LOGO_LICENSE_FILE_SIZE : FILE_SIZE;
        if (file.isEmpty()) {
            throw new InputException("上传文件不能为空！");
        }
        if (file.getSize() > size) {
            throw new InputException("文件大小超过" + size / 1024L / 1024L + "M");
        }
    }

    private boolean checkFileExtension(MultipartFile file, List<String> typeList) {
        String fileExt = getFileExtNameUpperCase(file.getOriginalFilename());
        if (fileExt != null && !typeList.contains(fileExt)) {
            return false;
        }
        try (InputStream inputStream = file.getInputStream()) {
            // 对文件类型做补充检查
            String fileType = getFileType(file, fileExt);
            if (StringUtils.isEmpty(fileType)) {
                fileType = getVideoFileType(file);
            }
            if (StringUtils.isEmpty(fileType)) {
                fileType = FileTypeUtil.getType(inputStream);
            }
            if (StringUtils.isEmpty(fileType)) {
                LOGGER.warn("checkFileExtension=====> unknown file type: {}", fileType);
                return false;
            }
            fileType = Optional.of(fileType).map(x -> x.toUpperCase(Locale.ROOT)).orElse("");
            if (!typeList.contains(fileType)) {
                return false;
            }
        } catch (IOException e) {
            log.error("checkFile error", e);
            throw new ServiceException("校验文件类型出错");
        }
        return true;
    }

    /**
     * 通过魔数判断文件是否为ext文件名后缀标识的文件类型，如果不匹配返回null
     * 如果需要补充文件魔数，需修改M_FILE_TYPE_MAP
     *
     * @param file 文件
     * @param ext 文件后缀
     * @return 文件类型
     */
    public static String getFileType(MultipartFile file, String ext) {
        String magicNum = getFileHeader(file);
        if (magicNum == null) {
            return null;
        }
        String fileTypeMagicNum = M_FILE_TYPE_MAP.get(ext);
        if (fileTypeMagicNum == null) {
            return null;
        }
        if (magicNum.toUpperCase(Locale.ROOT).startsWith(fileTypeMagicNum)) {
            return ext;
        }
        return null;
    }

    private static String getFileHeader(MultipartFile file) {
        String magicNum;
        try (InputStream inputStream = file.getInputStream()) {
            byte[] buffer = new byte[50];
            int count = inputStream.read(buffer, 0, buffer.length);
            if (Objects.equals(count, -1)) {
                LOGGER.warn("there is no more data because the end of the stream has bean reached");
                return null;
            }
            magicNum = HexUtil.encodeHexStr(buffer);
        } catch (IOException e) {
            LOGGER.error("read InputStream failed");
            return null;
        }
        LOGGER.info("getFileHeader=======>文件的头部信息：{}", magicNum);
        return magicNum;
    }

    private static String getFileExtNameUpperCase(String fileName) {
        String fileExtName = getFileExtName(fileName);
        if (fileExtName != null) {
            return fileExtName.toUpperCase(Locale.ROOT);
        }
        return null;
    }

    private static String getFileExtName(String fileName) {
        int index = fileName.lastIndexOf(46);
        if (index != -1 && index != fileName.length() - 1) {
            return fileName.substring(index + 1);
        } else {
            // 没有文件扩展名
            return null;
        }
    }

    private static String getVideoFileType(MultipartFile file) throws IOException {
        byte[] fileHeader = new byte[8];
        int read = file.getInputStream().read(fileHeader);
        return new String(fileHeader, StandardCharsets.UTF_8).toLowerCase(Locale.ROOT).contains("ftyp") ? "MP4" : "";
    }

    public void downloadFile(String fileId, String originalFilename, HttpServletResponse response)
            throws UnsupportedEncodingException, InputException {
        response.reset();
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(originalFilename, "UTF-8"));
        download(fileId, response);
    }

    private void download(String fileId, HttpServletResponse response) throws InputException {
        try (InputStream inputStream = s3Utils.downloadFile(fileId);
             ServletOutputStream outputStream = response.getOutputStream()) {
            byte[] b = new byte[1024];
            int len;
            while ((len = inputStream.read(b)) > 0) {
                outputStream.write(b, 0, len);
            }
            outputStream.flush();
        } catch (IOException e) {
            throw new InputException("文件下载异常" + e.getMessage());
        }
    }

    public void previewImage(String fileId, HttpServletResponse response) throws InputException {
        response.reset();
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        download(fileId, response);
    }
}
