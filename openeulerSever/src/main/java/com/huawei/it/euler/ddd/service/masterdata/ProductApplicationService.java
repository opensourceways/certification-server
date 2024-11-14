/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.ddd.service.masterdata;

import com.huawei.it.euler.ddd.domain.masterdata.Product;
import com.huawei.it.euler.ddd.service.masterdata.cqe.ProductCommand;
import com.huawei.it.euler.ddd.service.masterdata.cqe.ProductQuery;

import java.util.List;

/**
 * 产品类型 application接口
 *
 * @author zhaoyan
 * @since 2024-11-14
 */
public interface ProductApplicationService {
    public Product add(ProductCommand command);

    public void delete(ProductCommand command);

    public List<Product> findList(ProductQuery query);
}
