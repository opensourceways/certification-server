/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.domain.masterdata;

import com.huawei.it.euler.ddd.service.masterdata.cqe.ProductQuery;

import java.util.List;

/**
 * 产品类型 持久化接口
 *
 * @author zhaoyan
 * @since 2024-11-14
 */
public interface ProductRepository {
    public Product add(Product product);

    public void delete(Integer id);

    public List<Product> findList(ProductQuery query);

}
