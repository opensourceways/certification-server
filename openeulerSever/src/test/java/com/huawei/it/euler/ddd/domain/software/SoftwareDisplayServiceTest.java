/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.software;

import com.huawei.it.euler.exception.ParamException;
import com.huawei.it.euler.mapper.SoftwareMapper;
import com.huawei.it.euler.model.entity.Software;
import com.huawei.it.euler.model.enumeration.ErrorCodes;
import com.huawei.it.euler.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
public class SoftwareDisplayServiceTest {

    private static final String USER_UUID = "1";

    private static final int SOFTWARE_ID = 1;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SoftwareDisplayRepositoryImpl displayRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SoftwareMapper softwareMapper;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private UserService userService;

    @InjectMocks
    private SoftwareDisplayService softwareDisplayService;

    @Test
    @DisplayName("隐藏测评业务成功")
    void testHiddenSuccess() {
        Software software = new Software();
        software.setId(1);
        software.setProductName("测试隐藏");

        // setup
        Mockito.when(displayRepository.count(any())).thenReturn(0L);
        Mockito.when(userService.isUserPermission(any(), any())).thenReturn(true);
        Mockito.when(softwareMapper.findById(anyInt())).thenReturn(software);
        Mockito.when(displayRepository.save(any())).thenReturn(true);

        boolean hidden = softwareDisplayService.hidden(USER_UUID, SOFTWARE_ID);

        // verify
        Assertions.assertTrue(hidden);
    }

    @Test
    @DisplayName("隐藏测试业务失败-无权限")
    void testHiddenFailedUnauthorized() {
        // setup
        Mockito.when(userService.isUserPermission(any(), any())).thenReturn(false);

        // run
        ParamException paramException = assertThrows(ParamException.class, () -> {
            softwareDisplayService.hidden(USER_UUID, SOFTWARE_ID);
        });
        // verify
        Assertions.assertEquals(ErrorCodes.UNAUTHORIZED.getMessage(), paramException.getMessage());
    }

    @Test
    @DisplayName("隐藏测试业务失败-已隐藏")
    void testHiddenFailedExist() {
        // setup
        Mockito.when(displayRepository.count(any())).thenReturn(1L);

        // run
        ParamException paramException = assertThrows(ParamException.class, () -> {
            softwareDisplayService.hidden(USER_UUID, SOFTWARE_ID);
        });
        // verify
        Assertions.assertEquals("业务已隐藏，请勿重复操作！", paramException.getMessage());
    }

    @Test
    @DisplayName("取消隐藏测试业务成功")
    void testShowSuccess() {
        // setup
        Mockito.when(displayRepository.removeBatchByIds(any())).thenReturn(true);

        List<Integer> idList = new ArrayList<>();
        idList.add(1);
        boolean show = softwareDisplayService.show(idList);

        // verify
        Assertions.assertTrue(show);
    }

}
