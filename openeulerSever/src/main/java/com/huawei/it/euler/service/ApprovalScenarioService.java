package com.huawei.it.euler.service;

import com.huawei.it.euler.model.entity.ApprovalPathNode;
import com.huawei.it.euler.model.entity.ApprovalScenario;
import com.huawei.it.euler.model.vo.ProcessVo;

import java.util.List;
import java.util.Map;

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


    /**
     * Execute the approval action to next node: 1 - pass to forward, 2 - reject to back, 3 - transfer to stay.
     * There have three phases in default scenario: apply: 1,2 ; test: 3,4,5; certificate: 6,7,8.
     * In reject action, 3 and 6 forbid back and only back one stop every action.
     * @param asId test scenario id
     * @param processVo approval message
     * @return next node information
     */
    Map<String,String> action(Integer asId, ProcessVo processVo);

    List<ApprovalPathNode> findApprovalPathNode(Integer asId, String status);
}
