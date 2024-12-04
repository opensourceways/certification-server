/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.model.enumeration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * 枚举测试类
 *
 * @author zhaoyan
 * @since 2024-12-03
 */
@ExtendWith(MockitoExtension.class)
public class CenterEnumTest {

    private static final int OPEN_EULER_ID = 1;
    private static final String OPEN_EULER_NAME = "openEuler社区";
    private static final String OPEN_EULER_NAME_OLD = "openEuler开源社区";

    @Test
    @DisplayName("根据id查找等于新名称")
    public void testFindById() {
        String byId = CenterEnum.findById(OPEN_EULER_ID);
        Assertions.assertEquals(OPEN_EULER_NAME, byId);
    }

    @Test
    @DisplayName("根据id查找不等于旧名称")
    public void testFindByIdNotEqual() {
        String byId = CenterEnum.findById(OPEN_EULER_ID);
        Assertions.assertNotEquals(OPEN_EULER_NAME_OLD, byId);
    }

    @Test
    @DisplayName("根据旧name查找")
    public void testFindByNameOld(){
        Integer byName = CenterEnum.findByName(OPEN_EULER_NAME_OLD);
        Assertions.assertEquals(OPEN_EULER_ID,byName);
    }

    @Test
    @DisplayName("根据新name查找")
    public void testFindByNameNew(){
        Integer byName = CenterEnum.findByName(OPEN_EULER_NAME);
        Assertions.assertEquals(OPEN_EULER_ID,byName);
    }

    @Test
    @DisplayName("根据新name查找")
    public void testGetAll(){
        List<CenterEnum> allInnovationCenter = new ArrayList<>();
        allInnovationCenter.add(CenterEnum.USER);
        allInnovationCenter.add(CenterEnum.CHINA_REGION);
        allInnovationCenter.add(CenterEnum.SIG_GROUP);
        allInnovationCenter.add(CenterEnum.EULER_IC);
        allInnovationCenter.add(CenterEnum.FLAG_STORE);
        allInnovationCenter.add(CenterEnum.ADMIN);
        allInnovationCenter.add(CenterEnum.OSV_USER);

        List<CenterEnum> allCenters = CenterEnum.getAllCenters();

        Assertions.assertEquals(allInnovationCenter, allCenters);
    }

    @Test
    @DisplayName("根据新name查找")
    public void testGetAll2(){
        List<CenterEnum> allInnovationCenter = new ArrayList<>();
        allInnovationCenter.add(CenterEnum.OLD_USER);
        allInnovationCenter.add(CenterEnum.OLD_CHINA_REGION);
        allInnovationCenter.add(CenterEnum.OLD_SIG_GROUP);
        allInnovationCenter.add(CenterEnum.OLD_EULER_IC);
        allInnovationCenter.add(CenterEnum.OLD_FLAG_STORE);
        allInnovationCenter.add(CenterEnum.OLD_ADMIN);
        allInnovationCenter.add(CenterEnum.USER);
        allInnovationCenter.add(CenterEnum.CHINA_REGION);
        allInnovationCenter.add(CenterEnum.SIG_GROUP);
        allInnovationCenter.add(CenterEnum.EULER_IC);
        allInnovationCenter.add(CenterEnum.FLAG_STORE);
        allInnovationCenter.add(CenterEnum.ADMIN);
        allInnovationCenter.add(CenterEnum.OSV_USER);

        List<CenterEnum> allCenters = CenterEnum.getAllCenters();

        Assertions.assertNotEquals(allInnovationCenter, allCenters);
    }

}
