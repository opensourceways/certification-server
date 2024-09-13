package com.huawei.it.euler.ddd.infrastructure.cache;

import com.huawei.it.euler.ddd.domain.account.RoleService;
import com.huawei.it.euler.ddd.domain.account.UserInfo;
import com.huawei.it.euler.ddd.domain.account.UserInfoService;
import com.huawei.it.euler.ddd.infrastructure.oidc.OidcAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AutoLoadUserToCacheService implements CommandLineRunner {

    @Autowired
    private RoleService roleService;

    @Autowired
    private OidcAuthService oidcAuthService;

    @Autowired
    private UserInfoService userInfoService;

    @Override
    public void run(String... args) throws Exception {
        List<String> allUuidList = roleService.findAllUuid();
        for (String uuid : allUuidList) {
            UserInfo userInfo = oidcAuthService.getUserInfo(uuid);
            if (userInfo != null){
                userInfoService.saveUser(userInfo);
            }
        }
    }
}
