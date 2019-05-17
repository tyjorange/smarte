package com.rogy.smarte.repository.db2;

import com.rogy.smarte.entity.db2.RolePermissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RolePermissionsRepository extends JpaRepository<RolePermissions, Integer> {

    @Query("SELECT rp FROM RolePermissions rp WHERE rp.roleId = :roleId")
    List<RolePermissions> selectByRoleId(Integer roleId);

}
