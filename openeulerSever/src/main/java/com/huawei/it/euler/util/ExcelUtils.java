/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.util;


import com.alibaba.fastjson.JSONObject;
import com.huawei.it.euler.mapper.SoftwareMapper;
import com.huawei.it.euler.model.entity.Attachments;
import com.huawei.it.euler.model.entity.CompatibleDataInfo;
import com.huawei.it.euler.model.vo.CompatibleDataVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * excel处理工具类
 *
 * @since 2024/07/01
 */
@Slf4j
@Component
public class ExcelUtils {
    // 总行数
    private static int totalRows = 0;

    // 总列数
    private static int totalCells = 0;

    // 对象属性名
    private static List<String> valueNameList = new ArrayList<>();

    // 对象属性类型
    private static List<String> valueTypeList = new ArrayList<>();

    @Autowired
    private SoftwareMapper softwareMapper;

    @Autowired
    private S3Utils s3Utils;

    /**
     * 获取excel文件数据
     *
     * @param fileId 文件id
     * @return 兼容性数据
     */
    public List<CompatibleDataInfo> getExcelInfo(String fileId) {
        Attachments attachments = softwareMapper.downloadAttachments(fileId);
        String fileName = attachments.getFileName();
        if (!isExcel(fileName)) {
            return null;
        }
        try (InputStream inputStream = s3Utils.downloadFile(fileId)) {
            if (inputStream != null) {
                Workbook workbook = WorkbookFactory.create(inputStream);
                return readExcelValue(workbook);
            }
        } catch (IOException | EncryptedDocumentException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // 循环每行、每列返回数据，需对日期、数字格式进行处理
    private List<CompatibleDataInfo> readExcelValue(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(0); // 第一个shell
        totalRows = sheet.getPhysicalNumberOfRows(); // excel的行数
        if (totalRows > 1 && sheet.getRow(0) != null) {
            totalCells = sheet.getRow(0).getPhysicalNumberOfCells(); // excel的列数
        }
        valueNameList = paresBeanPropertyName(CompatibleDataVo.class);
        valueTypeList = paresBeanPropertyTypeName(CompatibleDataVo.class);
        List<CompatibleDataInfo> dataInfoList = new ArrayList<>();
        for (int i = 1; i < totalRows; i++) { // 循环excel每一行，默认跳过第一行
            Row row = sheet.getRow(i);
            HashMap<String, Object> map = new HashMap<>();
            if (row == null) {
                continue;
            }
            for (int j = 0; j <= totalCells - 1; j++) { // 循环excel每一列
                Cell cell = row.getCell(j);
                if (cell == null) {
                    continue;
                }
                getMapProperties(map, j, cell);
            }
            CompatibleDataVo vo = JSONObject.parseObject(JSONObject.toJSONString(map), CompatibleDataVo.class);
            CompatibleDataInfo dataInfo = new CompatibleDataInfo();
            BeanUtils.copyProperties(vo, dataInfo);
            dataInfoList.add(dataInfo);
        }
        return dataInfoList;
    }

    private void getMapProperties(HashMap<String, Object> map, int j, Cell cell) {
        // 将数据输入到map
        if ("java.util.Date".equals(valueTypeList.get(j))) {
            if (cell.getCellType() == CellType.NUMERIC) { // 时间格式化
                Date cellValue = cell.getDateCellValue();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                String date = dateFormat.format(cellValue);
                map.put(valueNameList.get(j), date);
            } else {
                map.put(valueNameList.get(j), null);
            }
        } else {
            if (cell.getCellType() == CellType.NUMERIC && String.valueOf(cell).endsWith(".0")) {
                // 整数直接读取会自动加上.0，需格式化
                DecimalFormat decimalFormat = new DecimalFormat("#");
                double num = cell.getNumericCellValue();
                String res = decimalFormat.format(num);
                map.put(valueNameList.get(j), res);
            } else {
                map.put(valueNameList.get(j), String.valueOf(cell).trim());
            }
        }
    }

    /**
     * 判断是否是excel文件
     *
     * @param fileName 文件名
     * @return boolean
     */
    private boolean isExcel(String fileName) {
        return fileName != null && (isExcel2003(fileName) || isExcel2007(fileName));
    }

    private boolean isExcel2003(String fileName) {
        return fileName.matches("^.+\\.(?i)(xls)$");
    }

    private boolean isExcel2007(String fileName) {
        return fileName.matches("^.+\\.(?i)(xlsx)$");
    }

    /**
     * 依序获取class对象属性的名字
     *
     * @param clz class
     * @return List<String>
     */
    private static <T> List<String> paresBeanPropertyName(Class<T> clz) {
        // 获取class的属性字段
        Field[] fields = clz.getDeclaredFields();
        if (fields.length > 0) {
            List<String> beanPropertyNames = new ArrayList<>();
            for (Field field : fields) {
                ReflectionUtils.makeAccessible(field);
                beanPropertyNames.add(field.getName());
            }
            return beanPropertyNames;
        }
        return null;
    }

    /**
     * 依序获取class对象属性的数据类型
     *
     * @param clz class
     * @return List<String>
     */
    private static <T> List<String> paresBeanPropertyTypeName(Class<T> clz) {
        Field[] fields = clz.getDeclaredFields();
        if (fields.length > 0) {
            List<String> beanPropertyTypes = new ArrayList<>();
            for (Field field : fields) {
                ReflectionUtils.makeAccessible(field);
                beanPropertyTypes.add(field.getType().getName());
            }
            return beanPropertyTypes;
        }
        return null;
    }
}
