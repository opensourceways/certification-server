/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.masterdata;

import com.huawei.it.euler.ddd.domain.masterdata.Product;
import com.huawei.it.euler.ddd.service.masterdata.cqe.ProductCommand;
import com.huawei.it.euler.ddd.service.masterdata.cqe.ProductQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * 产品类型 工厂类
 *
 * @author zhaoyan
 * @since 2024-11-14
 */
@Component
public class ProductFactory {
    public Product toProduct(ProductCommand command) {
        Product product = new Product();
        BeanUtils.copyProperties(command, product);
        return product;
    }

    public ProductQuery toQuery(ProductCommand command) {
        ProductQuery query = new ProductQuery();
        query.setProductType(command.getProductType());
        query.setProductChildrenType(command.getProductChildrenType());
        return query;
    }
}
