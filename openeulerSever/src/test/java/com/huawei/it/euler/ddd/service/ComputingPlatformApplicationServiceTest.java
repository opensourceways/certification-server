/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import com.huawei.it.euler.ddd.domain.masterdata.ComputingPlatformRepository;
import com.huawei.it.euler.ddd.domain.masterdata.ComputingPlatform;
import com.huawei.it.euler.ddd.service.masterdata.ComputingPlatformFactory;
import com.huawei.it.euler.ddd.service.masterdata.cqe.ComputingPlatformCommand;
import com.huawei.it.euler.ddd.service.masterdata.cqe.ComputingPlatformQuery;
import com.huawei.it.euler.ddd.service.masterdata.impl.ComputingPlatformApplicationServiceImpl;
import com.huawei.it.euler.exception.BusinessException;
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
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 算力平台 application测试类
 *
 * @author zhaoyan
 * @since 2024-11-14
 */
@ExtendWith(MockitoExtension.class)
public class ComputingPlatformApplicationServiceTest {
    private static final int BUSI_ID = 1;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ComputingPlatformRepository computingPlatformRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ComputingPlatformFactory computingPlatformFactory;

    @InjectMocks
    private ComputingPlatformApplicationServiceImpl computingPlatformApplicationService;

    @Test
    @DisplayName("新增成功")
    public void testAddSuccess() {
        ComputingPlatformCommand command = getCommand(null);
        ComputingPlatformQuery query = getQuery();
        ComputingPlatform computingPlatform = getComputingPlatform();

        Mockito.when(computingPlatformFactory.toQuery(command)).thenReturn(query);
        Mockito.when(computingPlatformRepository.findList(query)).thenReturn(null);
        Mockito.when(computingPlatformFactory.toComputingPlatform(command)).thenReturn(computingPlatform);
        Mockito.when(computingPlatformRepository.add(computingPlatform)).thenReturn(computingPlatform);

        ComputingPlatform add = computingPlatformApplicationService.add(command);

        Assertions.assertEquals(computingPlatform, add);
    }

    @Test
    @DisplayName("新增失败")
    public void testAddFailure() {
        ComputingPlatformCommand command = getCommand(null);
        ComputingPlatformQuery query = getQuery();
        ComputingPlatform computingPlatform = getComputingPlatform();
        List<ComputingPlatform> computingPlatformList = new ArrayList<>();
        computingPlatformList.add(computingPlatform);

        Mockito.when(computingPlatformFactory.toQuery(command)).thenReturn(query);
        Mockito.when(computingPlatformRepository.findList(query)).thenReturn(computingPlatformList);

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> computingPlatformApplicationService.add(command));

        Assertions.assertEquals("算力平台已存在！", businessException.getMessage());
    }

    @Test
    @DisplayName("删除成功")
    public void testDeleteSuccess() {
        ComputingPlatformCommand command = getCommand(BUSI_ID);

        Mockito.doNothing().when(computingPlatformRepository).delete(command.getId());

        computingPlatformApplicationService.delete(command);

        Assertions.assertEquals(BUSI_ID, command.getId());
    }

    @Test
    @DisplayName("列表查询成功")
    public void testFindListSuccess() {
        ComputingPlatformQuery query = getQuery();
        ComputingPlatform computingPlatform = getComputingPlatform();
        List<ComputingPlatform> computingPlatformList = new ArrayList<>();
        computingPlatformList.add(computingPlatform);

        Mockito.when(computingPlatformRepository.findList(query)).thenReturn(computingPlatformList);

        List<ComputingPlatform> list = computingPlatformApplicationService.findList(query);

        Assertions.assertEquals(computingPlatformList, list);
    }

    private ComputingPlatformCommand getCommand(Integer id) {
        ComputingPlatformCommand command = new ComputingPlatformCommand();
        command.setId(id);
        command.setPlatformName("intel");
        command.setServerProvider("other");
        command.setServerType("other");
        command.setLastUpdatedBy("1");
        command.setLastUpdatedTime(new Date());
        return command;
    }

    private ComputingPlatformQuery getQuery() {
        ComputingPlatformQuery query = new ComputingPlatformQuery();
        query.setPlatformName("intel");
        query.setServerProvider("other");
        query.setServerType("other");
        return query;
    }

    private ComputingPlatform getComputingPlatform() {
        ComputingPlatform computingPlatform = new ComputingPlatform();
        computingPlatform.setPlatformName("intel");
        computingPlatform.setServerProvider("other");
        computingPlatform.setServerType("other");
        return computingPlatform;
    }
}
