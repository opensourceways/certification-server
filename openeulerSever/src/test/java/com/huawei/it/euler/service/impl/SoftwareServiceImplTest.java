package com.huawei.it.euler.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.huawei.it.euler.mapper.SoftwareMapper;
import com.huawei.it.euler.model.entity.Software;
import com.huawei.it.euler.model.vo.ComputingPlatformVo;
import com.huawei.it.euler.service.UserService;

/**
 * {@link SoftwareServiceImpl}测试类
 */
@ExtendWith(MockitoExtension.class)
class SoftwareServiceImplTest {
    private static final Integer USER_ID = 1;

    private static final String USER_UUID = "1";

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private SoftwareMapper softwareMapper;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private UserService userService;

    @InjectMocks
    private SoftwareServiceImpl softwareServiceImpl;

    @Test
    @DisplayName("根据id查询软件")
    void testFindByIdFail() {
        Software software = initTestSoftware();
        // setup
        Mockito.when(userService.isUserPermission(any(), any())).thenReturn(true);
        Mockito.when(softwareMapper.findById(anyInt())).thenReturn(initTestSoftware2());

        // run
        Software result = softwareServiceImpl.findById(USER_ID, USER_UUID);
        // verify
        Assertions.assertEquals(software, result);
    }

    @Test
    void updateSoftware() {}

    @Test
    void insertSoftware() {}

    @Test
    void commonProcess() {}

    @Test
    void testingPhase() {}

    private Software initTestSoftware() {
        Software software = new Software();
        software.setId(1);
        software.setStatus(1);
        software.setJsonHashRatePlatform("[{\"serverTypes\":[\"超强Z520-M1\"],\"platformName\":\"兆芯\",\"serverProvider\":\"清华同方\"}]");
        ComputingPlatformVo computingPlatformVo = new ComputingPlatformVo();
        computingPlatformVo.setPlatformName("兆芯");
        computingPlatformVo.setServerProvider("清华同方");
        computingPlatformVo.setServerTypes(List.of("超强Z520-M1"));
        software.setHashratePlatformList(List.of(computingPlatformVo));
        software.setPlatforms(List.of("兆芯/清华同方/超强Z520-M1"));
        return software;
    }

    private Software initTestSoftware2() {
        Software software = new Software();
        software.setId(1);
        software.setStatus(1);
        software.setJsonHashRatePlatform("[{\"serverTypes\":[\"超强Z520-M1\"],\"platformName\":\"兆芯\",\"serverProvider\":\"清华同方\"}]");
        ComputingPlatformVo computingPlatformVo = new ComputingPlatformVo();
        computingPlatformVo.setPlatformName("兆芯");
        computingPlatformVo.setServerProvider("清华同方");
        computingPlatformVo.setServerTypes(List.of("超强Z520-M1"));
        software.setHashratePlatformList(List.of(computingPlatformVo));
        return software;
    }
}