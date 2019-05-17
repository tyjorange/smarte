package com.rogy.smarte.entity.db2;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Permissions {
    private int permissionsId;
    private String permissionsName;

    @Id
    @Column(name = "permissions_id", nullable = false)
    public int getPermissionsId() {
        return permissionsId;
    }

    public void setPermissionsId(int permissionsId) {
        this.permissionsId = permissionsId;
    }

    @Basic
    @Column(name = "permissions_name", nullable = true, length = 255)
    public String getPermissionsName() {
        return permissionsName;
    }

    public void setPermissionsName(String permissionsName) {
        this.permissionsName = permissionsName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permissions that = (Permissions) o;
        return permissionsId == that.permissionsId &&
                Objects.equals(permissionsName, that.permissionsName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permissionsId, permissionsName);
    }
}
