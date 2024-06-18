package com.huawei.it.euler.service;

import com.huawei.it.euler.model.entity.ApprovalPathNode;
import com.huawei.it.euler.model.entity.ApprovalScenario;

import java.util.List;

public interface ApprovalScenarioService {

    /**
     * find approval scenario by id
     * @param id id
     * @return approval scenario
     */
    ApprovalScenario findById(Integer id);

    /**
     * Find non-user approval node collection by innovation center and cpu vendor.
     * @param innovationCenter innovation center value
     * @param cpuVendor cpu vendor
     * @return approval path node collection
     */
    List<ApprovalPathNode> findApprovalPath(String innovationCenter, String cpuVendor);

}
