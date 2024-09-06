package com.huawei.it.euler.ddd.domain.account;

import com.huawei.it.euler.common.JsonResponse;
import com.huawei.it.euler.exception.ParamException;
import com.huawei.it.euler.mapper.ProtocolMapper;
import com.huawei.it.euler.model.entity.Protocol;
import com.huawei.it.euler.model.enumeration.ProtocolEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

@Service
@Transactional
public class ProtocolService {

    @Autowired
    private ProtocolMapper protocolMapper;

    public JsonResponse<String> signAgreement(Integer protocolType, String uuid) {
        Date date = new Date();
        Protocol protocol = new Protocol();
        protocol.setProtocolType(protocolType)
                .setProtocolName(ProtocolEnum.getProtocolNameByType(protocolType))
                .setStatus(1)
                .setCreatedBy(uuid)
                .setCreatedTime(date)
                .setUpdatedBy(uuid)
                .setUpdatedTime(date);
        if (Objects.equals(protocolType, ProtocolEnum.PRIVACY_POLICY.getProtocolType())) {
            Protocol signedProtocol = protocolMapper.selectProtocolByType(protocolType, uuid);
            if (signedProtocol != null) {
                throw new ParamException("无需重复签署");
            }
        }
        int count = protocolMapper.insertUserSignProtocol(protocol);
        if (count == 0) {
            throw new ParamException("签署失败");
        }
        return JsonResponse.success();
    }

    public JsonResponse<String> cancelAgreement(Integer protocolType, String uuid) {
        Protocol signedProtocol = protocolMapper.selectProtocolDesc(protocolType, uuid);
        if (signedProtocol == null) {
            throw new ParamException("未签署该协议");
        }
        int count = protocolMapper.cancelSignedProtocol(signedProtocol.getId());
        if (count == 0) {
            throw new ParamException("撤销签署失败");
        }
        return JsonResponse.success();
    }
}
