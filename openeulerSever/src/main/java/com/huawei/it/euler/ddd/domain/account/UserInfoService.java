package com.huawei.it.euler.ddd.domain.account;

import com.github.benmanes.caffeine.cache.Cache;
import com.huawei.it.euler.ddd.infrastructure.oidc.OidcAuthService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserInfoService implements UserDetailsService {

    private static final String ACCOUNT_INVAILD = "账号或密码错误";

    @Resource
    private Cache<String, Object> persistentCache;

    @Autowired
    private RoleService roleService;

    @Autowired
    private OidcAuthService oidcAuthService;

    public void saveUser(UserInfo userInfo){
        List<Role> roleInfoList = roleService.getRoleInfoByUuid(userInfo.getUuid());
        if (roleInfoList.isEmpty()) {
            roleService.setDefaultRole(userInfo.getUuid());
//            roleInfoList = roleService.getRoleInfoByUuid(userInfo.getUuid());
        }
//        userInfo.setRoleList(roleInfoList);
//        persistentCache.put(userInfo.getUuid(), userInfo);
    }

    public UserInfo getUser(String uuid){
        return oidcAuthService.getUserInfo(uuid);
//        return (UserInfo) persistentCache.getIfPresent(uuid);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userInfo = getUser(username);
        List<GrantedAuthority> userAuthorities = roleService.getUserAuthorities(username);
        UserDetails userDetails;
        if (userInfo != null) {
            userDetails = new User(username, null, userAuthorities);
        } else {
            throw new UsernameNotFoundException(ACCOUNT_INVAILD);
        }
        return userDetails;
    }
}
