package com.rogy.smarte.service;

import java.util.Set;

public interface IUserRolePermissionsService {
    /**
     * 获取用户头像
     *
     * @param webUserName
     * @return
     */
    String getAvatarByUsername(String webUserName);

    /**
     * 获取用户密码
     *
     * @param userName
     * @return
     */
    String getPasswordByUsername(String userName);

    /**
     * 获取用户角色
     *
     * @param userName
     * @return
     */
    Set<String> getRoles(String userName);

    /**
     * 获取角色权限
     *
     * @param roles
     * @return
     */
    Set<String> getPermissions(Set<String> roles);

    /**
     * 根据id获取权限名
     *
     * @param permissionsId
     * @return
     */
    String getPermissionsName(Integer permissionsId);

    /**
     * 根据用户名获取id
     *
     * @param webUserName
     * @return
     */
    String getWebUserId(String webUserName);

    /**
     * 根据id获取角色名
     *
     * @param roleId
     * @return
     */
    String getRoleName(Integer roleId);

    /**
     * 根据角色名称获取id
     *
     * @param roleName
     * @return
     */
    Integer getRoleId(String roleName);
}
