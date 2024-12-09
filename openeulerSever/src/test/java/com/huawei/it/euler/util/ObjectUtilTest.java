/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.util;

import com.huawei.it.euler.ddd.service.HardwareBoardCardAddCommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 对象工具类
 *
 * @author zhaoyan
 * @since 2024-12-05
 */
@ExtendWith(MockitoExtension.class)
public class ObjectUtilTest {

    private static String STRING = "123";

    @Test
    @DisplayName("对象为空判断null")
    void testIsEmptySuccessOfNull() {
        Object object = null;
        boolean empty = ObjectUtil.isEmpty(object);
        Assertions.assertTrue(empty);

        object = new Object();
        boolean empty1 = ObjectUtil.isEmpty(object);
        Assertions.assertFalse(empty1);
    }

    @Test
    @DisplayName("对象为空判断String")
    void testIsEmptySuccessString() {
        String str = "";
        boolean empty = ObjectUtil.isEmpty(str);
        Assertions.assertTrue(empty);

        str = STRING;
        boolean empty1 = ObjectUtil.isEmpty(str);
        Assertions.assertFalse(empty1);

        String strNull = null;
        boolean empty2 = ObjectUtil.isEmpty(strNull);
        Assertions.assertTrue(empty2);

        strNull = STRING;
        boolean empty3 = ObjectUtil.isEmpty(strNull);
        Assertions.assertFalse(empty3);
    }

    @Test
    @DisplayName("对象为空判断Collection")
    void testIsEmptySuccessCollection() {
        List<String> tList = new ArrayList<>();
        boolean listEmpty = ObjectUtil.isEmpty(tList);
        Assertions.assertTrue(listEmpty);

        tList.add(STRING);
        boolean listEmpty1 = ObjectUtil.isEmpty(tList);
        Assertions.assertFalse(listEmpty1);

        Set<String> set = new HashSet<>();
        boolean setEmpty = ObjectUtil.isEmpty(set);
        Assertions.assertTrue(setEmpty);

        set.add(STRING);
        boolean setEmpty1 = ObjectUtil.isEmpty(set);
        Assertions.assertFalse(setEmpty1);

        Queue<String> queue = new LinkedBlockingQueue<>();
        boolean queueEmpty = ObjectUtil.isEmpty(queue);
        Assertions.assertTrue(queueEmpty);

        queue.add(STRING);
        boolean queueEmpty1 = ObjectUtil.isEmpty(queue);
        Assertions.assertFalse(queueEmpty1);
    }

    @Test
    @DisplayName("对象为空判断Map")
    void testIsEmptySuccessMap() {
        Map<String, String> hashMap = new HashMap<>();
        boolean empty = ObjectUtil.isEmpty(hashMap);
        Assertions.assertTrue(empty);

        hashMap.put(STRING, STRING);
        boolean empty1 = ObjectUtil.isEmpty(hashMap);
        Assertions.assertFalse(empty1);

        Map<String, String> linkedMap = new LinkedHashMap<>();
        boolean empty2 = ObjectUtil.isEmpty(linkedMap);
        Assertions.assertTrue(empty2);

        linkedMap.put(STRING, STRING);
        boolean empty3 = ObjectUtil.isEmpty(linkedMap);
        Assertions.assertFalse(empty3);

        Map<String, String> treeMap = new TreeMap<>();
        boolean empty4 = ObjectUtil.isEmpty(treeMap);
        Assertions.assertTrue(empty4);

        treeMap.put(STRING, STRING);
        boolean empty5 = ObjectUtil.isEmpty(treeMap);
        Assertions.assertFalse(empty5);
    }

    @Test
    @DisplayName("对象为空判断Array")
    void testIsEmptySuccessArray() {
        Object[] objArr = new Object[]{};
        boolean empty = ObjectUtil.isEmpty(objArr);
        Assertions.assertTrue(empty);

        Object[] objArr1 = new Object[]{new Object()};
        boolean empty1 = ObjectUtil.isEmpty(objArr1);
        Assertions.assertFalse(empty1);

        Integer[] intArr = new Integer[]{};
        boolean empty2 = ObjectUtil.isEmpty(intArr);
        Assertions.assertTrue(empty2);

        Integer[] intArr1 = new Integer[]{1};
        boolean empty3 = ObjectUtil.isEmpty(intArr1);
        Assertions.assertFalse(empty3);

        char[] charArr = new char[]{};
        boolean empty4 = ObjectUtil.isEmpty(charArr);
        Assertions.assertFalse(empty4);
    }

    @Test
    @DisplayName("板卡插入成功")
    void testCheckFieldAllNullSuccess() {
        HardwareBoardCardAddCommand addCommand = new HardwareBoardCardAddCommand();
        boolean fieldAllNull = ObjectUtil.checkFieldAllNull(addCommand);
        Assertions.assertTrue(fieldAllNull);

        HardwareBoardCardAddCommand addCommand1 = new HardwareBoardCardAddCommand();
        addCommand1.setArchitecture("");
        addCommand1.setBoardModel("");
        addCommand1.setChipModel("");
        addCommand1.setChipVendor("");
        addCommand1.setDate("");
        addCommand1.setDeviceID("");
        addCommand1.setDownloadLink("");
        addCommand1.setDriverName("");
        addCommand1.setDriverSize("");
        addCommand1.setItem("");
        addCommand1.setOs("");
        addCommand1.setSecurityLevel("");
        addCommand1.setSha256("");
        addCommand1.setSsID("");
        addCommand1.setSvID("");
        addCommand1.setType("");
        addCommand1.setVendorID("");
        addCommand1.setVersion("");
        boolean fieldAllNull1 = ObjectUtil.checkFieldAllNull(addCommand1);
        Assertions.assertTrue(fieldAllNull1);
    }

}
