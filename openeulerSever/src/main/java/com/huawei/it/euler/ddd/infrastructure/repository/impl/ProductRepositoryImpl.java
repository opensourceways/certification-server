/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.CaseFormat;
import com.huawei.it.euler.ddd.domain.masterdata.Product;
import com.huawei.it.euler.ddd.domain.masterdata.ProductRepository;
import com.huawei.it.euler.ddd.infrastructure.persistence.mapper.ProductMapper;
import com.huawei.it.euler.ddd.infrastructure.persistence.po.ProductPO;
import com.huawei.it.euler.ddd.infrastructure.repository.builder.ProductBuilder;
import com.huawei.it.euler.ddd.service.masterdata.cqe.ProductQuery;
import com.huawei.it.euler.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 产品类型 持久化实现类
 *
 * @author zhaoyan
 * @since 2024-11-14
 */
@Repository
public class ProductRepositoryImpl implements ProductRepository {

    @Autowired
    private ProductMapper mapper;

    @Autowired
    private ProductBuilder builder;

    private static final List<String> SORT_COLUMN =
            new ArrayList<>(Arrays.asList("productType", "productChildrenType"));

    @Override
    public Product add(Product product) {
        ProductPO productPO = builder.fromProduct(product);
        mapper.insert(productPO);
        return builder.toProduct(productPO);
    }

    @Override
    public void delete(Integer id) {
        ProductPO productPO = mapper.selectById(id);
        if (productPO == null) {
            throw new BusinessException("产品类型不存在！");
        }
        mapper.deleteById(id);
    }

    public QueryWrapper<ProductPO> createQueryWrapper(ProductQuery query) {
        QueryWrapper<ProductPO> queryWrapper = new QueryWrapper<>();
        if (query.getId() != null) {
            queryWrapper.eq("id", query.getId());
        }
        if (!StringUtils.isEmpty(query.getProductType())) {
            queryWrapper.like("product_type", query.getProductType());
        }
        if (!StringUtils.isEmpty(query.getProductChildrenType())) {
            queryWrapper.like("product_children_type", query.getProductChildrenType());
        }
        if (query.getAscSort() != null && !query.getAscSort().isEmpty()) {
            List<String> ascList = query.getAscSort().stream().filter(SORT_COLUMN::contains)
                    .map(item -> CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, item))
                    .toList();
            if (!ascList.isEmpty()) {
                queryWrapper.orderByAsc(ascList);
            }
        }
        if (query.getDescSort() != null && !query.getDescSort().isEmpty()) {
            List<String> descList = query.getDescSort().stream().filter(SORT_COLUMN::contains)
                    .map(item -> CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, item))
                    .toList();
            if (!descList.isEmpty()) {
                queryWrapper.orderByAsc(descList);
            }
        }
        return queryWrapper;
    }

    @Override
    public List<Product> findList(ProductQuery query) {
        QueryWrapper<ProductPO> queryWrapper = createQueryWrapper(query);
        List<ProductPO> productPOS = mapper.selectList(queryWrapper);
        return builder.toProductList(productPOS);
    }
}
