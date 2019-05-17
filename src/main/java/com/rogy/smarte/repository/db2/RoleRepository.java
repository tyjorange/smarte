package com.rogy.smarte.repository.db2;

import com.rogy.smarte.entity.db2.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    @Query("SELECT r FROM Role r WHERE r.roleName = :roleName")
    Role selectByRoleName(String roleName);

}
