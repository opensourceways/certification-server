/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service;

import com.huawei.it.euler.ddd.domain.masterdata.Product;
import com.huawei.it.euler.ddd.domain.masterdata.ProductRepository;
import com.huawei.it.euler.ddd.service.masterdata.ProductFactory;
import com.huawei.it.euler.ddd.service.masterdata.cqe.ProductCommand;
import com.huawei.it.euler.ddd.service.masterdata.cqe.ProductQuery;
import com.huawei.it.euler.ddd.service.masterdata.impl.ProductApplicationServiceImpl;
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
 * 产品类型 application测试类
 *
 * @author zhaoyan
 * @since 2024-11-14
 */
@ExtendWith(MockitoExtension.class)
public class ProductApplicationServiceTest {

    private static final int BUSI_ID = 1;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ProductRepository productRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ProductFactory productFactory;

    @InjectMocks
    private ProductApplicationServiceImpl productApplicationService;

    @Test
    @DisplayName("新增成功")
    public void testAddSuccess() {
        ProductCommand command = getCommand(null);
        ProductQuery query = getQuery();
        Product product = getProduct();

        Mockito.when(productFactory.toQuery(command)).thenReturn(query);
        Mockito.when(productRepository.findList(query)).thenReturn(null);
        Mockito.when(productFactory.toProduct(command)).thenReturn(product);
        Mockito.when(productRepository.add(product)).thenReturn(product);

        Product add = productApplicationService.add(command);

        Assertions.assertEquals(product, add);
    }

    @Test
    @DisplayName("新增失败")
    public void testAddFailure() {
        ProductCommand command = getCommand(null);
        ProductQuery query = getQuery();
        Product product = getProduct();
        List<Product> productList = new ArrayList<>();
        productList.add(product);

        Mockito.when(productFactory.toQuery(command)).thenReturn(query);
        Mockito.when(productRepository.findList(query)).thenReturn(productList);

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> productApplicationService.add(command));

        Assertions.assertEquals("产品类型已存在！", businessException.getMessage());
    }

    @Test
    @DisplayName("删除成功")
    public void testDeleteSuccess() {
        ProductCommand command = getCommand(BUSI_ID);

        Mockito.doNothing().when(productRepository).delete(command.getId());

        productApplicationService.delete(command);

        Assertions.assertEquals(BUSI_ID, command.getId());
    }

    @Test
    @DisplayName("列表查询成功")
    public void testFindListSuccess() {
        ProductQuery query = getQuery();
        Product product = getProduct();
        List<Product> productList = new ArrayList<>();
        productList.add(product);

        Mockito.when(productRepository.findList(query)).thenReturn(productList);

        List<Product> list = productApplicationService.findList(query);

        Assertions.assertEquals(productList, list);
    }

    private ProductCommand getCommand(Integer id) {
        ProductCommand command = new ProductCommand();
        command.setId(id);
        command.setProductType("type");
        command.setProductChildrenType("children");
        command.setLastUpdatedBy("1");
        command.setLastUpdatedTime(new Date());
        return command;
    }

    private ProductQuery getQuery() {
        ProductQuery query = new ProductQuery();
        query.setProductType("type");
        query.setProductChildrenType("children");
        return query;
    }

    private Product getProduct() {
        Product product = new Product();
        product.setProductType("type");
        product.setProductChildrenType("children");
        return product;
    }
}
