/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.masterdata.impl;

import com.huawei.it.euler.ddd.domain.masterdata.Product;
import com.huawei.it.euler.ddd.domain.masterdata.ProductRepository;
import com.huawei.it.euler.ddd.service.masterdata.ProductApplicationService;
import com.huawei.it.euler.ddd.service.masterdata.ProductFactory;
import com.huawei.it.euler.ddd.service.masterdata.cqe.ProductCommand;
import com.huawei.it.euler.ddd.service.masterdata.cqe.ProductQuery;
import com.huawei.it.euler.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 产品类型 application实现类
 *
 * @author zhaoyan
 * @since 2024-11-14
 */
@Slf4j
@Service
public class ProductApplicationServiceImpl implements ProductApplicationService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductFactory productFactory;

    @Override
    public Product add(ProductCommand command) {
        ProductQuery query = productFactory.toQuery(command);
        List<Product> queryList = productRepository.findList(query);
        if (queryList != null && !queryList.isEmpty()) {
            throw new BusinessException("产品类型已存在！");
        }
        Product product = productFactory.toProduct(command);
        return productRepository.add(product);
    }

    @Override
    public void delete(ProductCommand command) {
        productRepository.delete(command.getId());
    }

    @Override
    public List<Product> findList(ProductQuery query) {
        return productRepository.findList(query);
    }
}
