package com.huawei.it.euler.ddd.domain.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleFactory roleFactory;

    public void setDefaultRole(String uuid) {
        RoleRelationship relationship = roleFactory.createWithDefaultRole(uuid);
        roleRepository.insert(relationship);
    }

    public List<Role> getRoleInfoByUuid(String uuid) {
        return roleRepository.findRoleInfoByUuid(uuid);
    }

    public List<GrantedAuthority> getUserAuthorities(String uuid) {
        List<String> roleList = roleRepository.findRoleByUuid(uuid);
        String authorities = "";
        if (!roleList.isEmpty()) {
            String roleNames = roleList.stream().map(role -> "ROLE_" + role).collect(Collectors.joining(","));
            authorities = authorities.concat(roleNames);
        }
        return AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
    }

    public boolean isPartner(List<Role> roleValueList) {
        List<Role> partnerRoleList = roleFactory.getPartnerRoleList();
        return partnerRoleList.stream().anyMatch(roleValueList::contains);
    }

    public List<String> getUuidListByRoleId(int roleId) {
        return roleRepository.findUuidByRoleId(roleId);
    }
}
