/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import com.huawei.it.euler.ddd.domain.masterdata.Os;
import com.huawei.it.euler.ddd.domain.masterdata.OsRepository;
import com.huawei.it.euler.ddd.service.masterdata.OsFactory;
import com.huawei.it.euler.ddd.service.masterdata.cqe.OsCommand;
import com.huawei.it.euler.ddd.service.masterdata.cqe.OsQuery;
import com.huawei.it.euler.ddd.service.masterdata.impl.OsApplicationServiceImpl;
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
 * 操作系统 application测试类
 *
 * @author zhaoyan
 * @since 2024-11-14
 */
@ExtendWith(MockitoExtension.class)
public class OsApplicationServiceTest {
    private static final int BUSI_ID = 1;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OsRepository osRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OsFactory osFactory;

    @InjectMocks
    private OsApplicationServiceImpl osApplicationService;

    @Test
    @DisplayName("新增成功")
    public void testAddSuccess() {
        OsCommand command = getCommand(null);
        OsQuery query = getQuery();
        Os os = getOs();

        Mockito.when(osFactory.toQuery(command)).thenReturn(query);
        Mockito.when(osRepository.findList(query)).thenReturn(null);
        Mockito.when(osFactory.toOs(command)).thenReturn(os);
        Mockito.when(osRepository.add(os)).thenReturn(os);

        Os add = osApplicationService.add(command);

        Assertions.assertEquals(os, add);
    }

    @Test
    @DisplayName("新增失败")
    public void testAddFailure() {
        OsCommand command = getCommand(null);
        OsQuery query = getQuery();
        Os os = getOs();
        List<Os> osList = new ArrayList<>();
        osList.add(os);

        Mockito.when(osFactory.toQuery(command)).thenReturn(query);
        Mockito.when(osRepository.findList(query)).thenReturn(osList);

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> osApplicationService.add(command));

        Assertions.assertEquals("操作系统已存在！", businessException.getMessage());
    }

    @Test
    @DisplayName("删除成功")
    public void testDeleteSuccess() {
        OsCommand command = getCommand(BUSI_ID);

        Mockito.doNothing().when(osRepository).delete(command.getId());

        osApplicationService.delete(command);

        Assertions.assertEquals(BUSI_ID, command.getId());
    }

    @Test
    @DisplayName("列表查询成功")
    public void testFindListSuccess() {
        OsQuery query = getQuery();
        Os os = getOs();
        List<Os> osList = new ArrayList<>();
        osList.add(os);

        Mockito.when(osRepository.findList(query)).thenReturn(osList);

        List<Os> list = osApplicationService.findList(query);

        Assertions.assertEquals(osList, list);
    }

    private OsCommand getCommand(Integer id) {
        OsCommand command = new OsCommand();
        command.setId(id);
        command.setOsName("openEuler");
        command.setOsVersion("1");
        command.setRelatedOsVersion("2");
        command.setLastUpdatedBy("1");
        command.setLastUpdatedTime(new Date());
        return command;
    }

    private OsQuery getQuery() {
        OsQuery query = new OsQuery();
        query.setOsName("openEuler");
        query.setOsVersion("1");
        query.setRelatedOsVersion("2");
        return query;
    }

    private Os getOs() {
        Os os = new Os();
        os.setOsName("openEuler");
        os.setOsVersion("1");
        os.setRelatedOsVersion("2");
        return os;
    }
}
