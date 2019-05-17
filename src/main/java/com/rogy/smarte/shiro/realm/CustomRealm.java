package com.rogy.smarte.shiro.realm;

import com.rogy.smarte.service.IUserRolePermissionsService;
import com.rogy.smarte.util.MD5Config;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * 权限认证
 * Created by vostor on 2018/11/14.
 */
public class CustomRealm extends AuthorizingRealm {
    private final static Logger logger = LoggerFactory.getLogger(CustomRealm.class);
    @Autowired
    private IUserRolePermissionsService IUserRolePermissionsService;

    /**
     * 认证
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername();
        String password = IUserRolePermissionsService.getPasswordByUsername(username);
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(username, password, super.getName());
        simpleAuthenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(MD5Config.SALT));
        return simpleAuthenticationInfo;
    }

    /**
     * 授权
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String username = (String) super.getAvailablePrincipal(principalCollection);
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        Set<String> roles = IUserRolePermissionsService.getRoles(username);
        authorizationInfo.setRoles(roles);// 设置角色
        logger.warn("角色=[{}]", roles.toString());
        Set<String> permissions = IUserRolePermissionsService.getPermissions(roles);
        authorizationInfo.addStringPermissions(permissions);//设置权限
        logger.warn("权限=[{}]", permissions.toString());
        return authorizationInfo;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        Md5Hash md5Hash = new Md5Hash("123456", MD5Config.SALT, MD5Config.ITERATIONS);//E10ADC3949BA59ABBE56E057F20F883E
        System.out.println(md5Hash);
    }
}
