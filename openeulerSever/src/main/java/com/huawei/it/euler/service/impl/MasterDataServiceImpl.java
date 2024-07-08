/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.service.impl;

import com.huawei.it.euler.mapper.MasterDataMapper;
import com.huawei.it.euler.model.entity.ComputingPlatform;
import com.huawei.it.euler.model.entity.Os;
import com.huawei.it.euler.model.entity.Product;
import com.huawei.it.euler.model.vo.*;
import com.huawei.it.euler.service.MasterDataService;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MasterDataServiceImpl
 *
 * @since 2024/07/04
 */
public class MasterDataServiceImpl implements MasterDataService {
    @Resource
    private MasterDataMapper masterDataMapper;

    @Override
    public List<OsVo> findAllOs() {
        List<Os> os = masterDataMapper.selectAllOs();
        Map<String, List<Os>> collect = os.stream().collect(Collectors.groupingBy(Os::getOsName));
        List<OsVo> osVos = new ArrayList<>();
        for (Map.Entry<String, List<Os>> entry : collect.entrySet()) {
            OsVo osVo = new OsVo();
            String osName = entry.getKey();
            osVo.setOsName(osName);
            osVo.setOsVersion(collect.get(osName).stream().map(Os::getOsVersion).collect(Collectors.toList()));
            osVos.add(osVo);
        }
        return osVos;
    }

    @Override
    public List<ProductVo> findAllProduct() {
        List<Product> products = masterDataMapper.selectAllProduct();
        Map<String, List<Product>> collect = products.stream().collect(Collectors.groupingBy(Product::getProductType));
        List<ProductVo> productVos = new ArrayList<>();
        for (Map.Entry<String, List<Product>> entry : collect.entrySet()) {
            ProductVo productVo = new ProductVo();
            String productType = entry.getKey();
            productVo.setProductType(productType);
            List<ProductType> coll= collect.get(productType).stream().map(item -> {
                ProductType productType1 = new ProductType();
                productType1.setProductType(item.getProductChildrenType());
                return productType1;
            }).collect(Collectors.toList());
            productVo.setChildren(coll);
            productVos.add(productVo);
        }
        return productVos;
    }

    @Override
    public List<String> findAllInnovationCenter() {
        return masterDataMapper.selectAllInnovationCenter();
    }

    @Override
    public List<HashRatePlatformVo> findAllComputingPlatform() {
        List<ComputingPlatform> computingPlatforms = masterDataMapper.selectAllComputingPlatform();
        Set<String> platformNames = computingPlatforms.stream()
                .collect(Collectors.groupingBy(ComputingPlatform::getPlatformName)).keySet();
        List<HashRatePlatformVo> computingPlatformVos = new ArrayList<>();
        platformNames.stream().forEach(platformName -> {
            HashRatePlatformVo computingPlatformVo = new HashRatePlatformVo();
            List<String> providers = computingPlatforms.stream()
                    .filter(item -> item.getPlatformName().equals(platformName))
                    .map(ComputingPlatform::getServerProvider)
                    .distinct()
                    .collect(Collectors.toList());
            List<ProviderAndServerType> providerAndServerTypes = new ArrayList<>();
            providers.stream().forEach(provider -> {
                List<String> serverTypes = computingPlatforms.stream()
                        .filter(item -> item.getPlatformName().equals(platformName)
                                && item.getServerProvider().equals(provider))
                        .map(ComputingPlatform::getServerType)
                        .collect(Collectors.toList());
                ProviderAndServerType providerAndServerType = new ProviderAndServerType();
                providerAndServerType.setServerProvider(provider);
                providerAndServerType.setServerTypes(serverTypes);
                providerAndServerTypes.add(providerAndServerType);
            });
            computingPlatformVo.setPlatformName(platformName);
            computingPlatformVo.setProviderAndServerTypes(providerAndServerTypes);
            computingPlatformVos.add(computingPlatformVo);
        });
        return computingPlatformVos;
    }
}
