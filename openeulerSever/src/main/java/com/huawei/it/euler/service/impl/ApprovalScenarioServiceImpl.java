package com.huawei.it.euler.service.impl;

import com.huawei.it.euler.mapper.ApprovalPathNodeMapper;
import com.huawei.it.euler.mapper.ApprovalScenarioMapper;
import com.huawei.it.euler.mapper.InnovationCenterMapper;
import com.huawei.it.euler.model.entity.ApprovalPathNode;
import com.huawei.it.euler.model.entity.ApprovalScenario;
import com.huawei.it.euler.model.entity.InnovationCenter;
import com.huawei.it.euler.service.ApprovalScenarioService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApprovalScenarioServiceImpl implements ApprovalScenarioService {

    @Autowired
    private ApprovalScenarioMapper approvalScenarioMapper;

    @Autowired
    private InnovationCenterMapper innovationCenterMapper;

    @Autowired
    private ApprovalPathNodeMapper approvalPathNodeMapper;

    @Override
    public ApprovalScenario findById(Integer id) {
        return approvalScenarioMapper.findById(id);
    }

    @Override
    public List<ApprovalPathNode> findApprovalPath(String innovationCenter, String cpuVendor) {
        InnovationCenter innovationCenterObj = innovationCenterMapper.findByName(innovationCenter);
        if (innovationCenterObj == null) {
            innovationCenterObj = innovationCenterMapper.findDefault();
        }

        // find all scenarios supported by this innovation center
        List<ApprovalScenario> approvalScenarioList = approvalScenarioMapper.findByIcId(innovationCenterObj.getId());
        if (approvalScenarioList.isEmpty()) {
            approvalScenarioList = approvalScenarioMapper.findByIcId(1);
        }

        ApprovalScenario approvalScenario = getApprovalScenarioByCpuVendor(cpuVendor, approvalScenarioList);

        return approvalPathNodeMapper.findNodeByAsId(approvalScenario.getId());
    }

    /**
     * default choice the fist one, because most innovation center only support one scenario,
     * but open-atom-intel center support two test scenario, their difference is the value of cpu vendor,
     * conditions are 'cpu vendor == null' and 'cpu vendor == '英特尔'.
     * this logic will select by cpu vendor fixed for min refactor, it can improve by Aviator in the future.
     * @param cpuVendor cpu vendor
     * @param approvalScenarioList approval scenario collection
     * @return suitable approval scenario
     */
    private static ApprovalScenario getApprovalScenarioByCpuVendor(String cpuVendor, List<ApprovalScenario> approvalScenarioList) {
        ApprovalScenario approvalScenario = approvalScenarioList.get(0);

        if (approvalScenarioList.size() > 1) {
            for (ApprovalScenario scenario : approvalScenarioList) {
                if (StringUtils.isEmpty(scenario.getConditions())) {
                    if (StringUtils.isEmpty(cpuVendor)) {
                        approvalScenario = scenario;
                    }
                } else if (scenario.getConditions().equals(cpuVendor)) {
                    approvalScenario = scenario;
                }
            }
        }
        return approvalScenario;
    }

}
