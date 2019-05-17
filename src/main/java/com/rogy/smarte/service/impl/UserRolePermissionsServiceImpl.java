package com.rogy.smarte.service.impl;

import com.rogy.smarte.entity.db1.User;
import com.rogy.smarte.entity.db2.*;
import com.rogy.smarte.repository.db1.UserRepository;
import com.rogy.smarte.repository.db2.*;
import com.rogy.smarte.service.IUserRolePermissionsService;
import org.apache.shiro.authc.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by vostor on 2018/12/29.
 */
@Service
public class UserRolePermissionsServiceImpl implements IUserRolePermissionsService {
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private RolePermissionsRepository rolePermissionsRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PermissionsRepository permissionsRepository;

    /**
     * 获取用户头像
     *
     * @param webUserName
     * @return
     */
    @Override
    public String getAvatarByUsername(String webUserName) {
        User user = userRepository.selectByUserName(webUserName);
        if (user == null) {
            throw new AuthenticationException();
        }
        return user.getHeadImg();
    }

    /**
     * 获取用户密码
     *
     * @param userName
     * @return
     */
    @Override
    public String getPasswordByUsername(String userName) {
        User user = userRepository.selectByUserName(userName);
        if (user == null) {
            throw new AuthenticationException();
        }
        return user.getPassword();
    }

    /**
     * 获取用户角色
     *
     * @param userName
     * @return
     */
    @Override
    public Set<String> getRoles(String userName) {
        String userId = this.getWebUserId(userName);
        List<UserRole> list = userRoleRepository.selectByUserId(userId);
        Set<String> roles = new HashSet<>();
        list.forEach(uR -> roles.add(this.getRoleName(uR.getRoleId())));
        return roles;
    }

    /**
     * 获取角色权限
     *
     * @param roles
     * @return
     */
    @Override
    public Set<String> getPermissions(Set<String> roles) {
        Set<String> permissions = new HashSet<>();
        roles.forEach(roleName -> {
            List<RolePermissions> rolePermissions = rolePermissionsRepository.selectByRoleId(this.getRoleId(roleName));
            rolePermissions.forEach(rp -> permissions.add(this.getPermissionsName(rp.getPermissionsId())));
        });
        return permissions;
    }

    /**
     * 根据id获取权限名
     *
     * @param permissionsId
     * @return
     */
    @Override
    public String getPermissionsName(Integer permissionsId) {
        Optional<Permissions> byId = permissionsRepository.findById(permissionsId);
        return byId.map(Permissions::getPermissionsName).orElse(null);
    }

    /**
     * 根据用户名获取id
     *
     * @param webUserName
     * @return
     */
    @Override
    public String getWebUserId(String webUserName) {
        User user = userRepository.selectByUserName(webUserName);
        if (user == null) {
            throw new AuthenticationException();
        }
        return user.getId();
    }

    /**
     * 根据id获取角色名
     *
     * @param roleId
     * @return
     */
    @Override
    public String getRoleName(Integer roleId) {
        Optional<Role> byId = roleRepository.findById(roleId);
        return byId.map(Role::getRoleName).orElse(null);
    }

    /**
     * 根据角色名称获取id
     *
     * @param roleName
     * @return
     */
    @Override
    public Integer getRoleId(String roleName) {
        Role role = roleRepository.selectByRoleName(roleName);
        return role.getRoleId();
    }

}
