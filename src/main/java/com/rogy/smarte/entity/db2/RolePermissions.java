package com.rogy.smarte.entity.db2;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "role_permissions", schema = "smarte_webuser", catalog = "")
public class RolePermissions {
    private int id;
    private Integer roleId;
    private Integer permissionsId;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "role_id", nullable = true)
    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    @Basic
    @Column(name = "permissions_id", nullable = true)
    public Integer getPermissionsId() {
        return permissionsId;
    }

    public void setPermissionsId(Integer permissionsId) {
        this.permissionsId = permissionsId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolePermissions that = (RolePermissions) o;
        return id == that.id &&
                Objects.equals(roleId, that.roleId) &&
                Objects.equals(permissionsId, that.permissionsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, roleId, permissionsId);
    }
}
