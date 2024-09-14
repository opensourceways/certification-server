package com.huawei.it.euler.ddd.domain.account;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleFactory {
    public int getDefaultRoleId(){
        return 1;
    }

    public RoleRelationship createWithDefaultRole(String uuid){
        RoleRelationship roleRelationship = new RoleRelationship();
        roleRelationship.setUuid(uuid);
        roleRelationship.setRoleId(getDefaultRoleId());
        return roleRelationship;
    }

    public List<Role> getPartnerRoleList(){
        Role role1 = new Role();
        role1.setId(1);
        role1.setRole("user");
        role1.setRoleName("合作伙伴");
        Role role2 = new Role();
        role2.setId(7);
        role2.setRole("OSV_user");
        role2.setRoleName("OSV伙伴");
        List<Role> partnerRoleList = new ArrayList<>();
        partnerRoleList.add(role1);
        partnerRoleList.add(role2);
        return partnerRoleList;
    }
}
