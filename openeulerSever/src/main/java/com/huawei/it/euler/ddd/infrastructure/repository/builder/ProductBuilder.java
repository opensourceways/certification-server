/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.infrastructure.repository.builder;

import com.huawei.it.euler.ddd.domain.masterdata.Product;
import com.huawei.it.euler.ddd.infrastructure.persistence.po.ProductPO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 产品类型构造器
 *
 * @author zhaoyan
 * @since 2024-11-14
 */
@Component
public class ProductBuilder {
    public ProductPO fromProduct(Product product) {
        ProductPO productPO = new ProductPO();
        BeanUtils.copyProperties(product, productPO);
        return productPO;
    }

    public Product toProduct(ProductPO productPO) {
        Product product = new Product();
        BeanUtils.copyProperties(productPO, product);
        return product;
    }

    public List<Product> toProductList(List<ProductPO> productPOList) {
        return productPOList.stream().map(this::toProduct).toList();
    }
}
