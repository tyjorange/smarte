package com.rogy.smarte.repository.db2;


import com.rogy.smarte.entity.db2.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Query("SELECT ur from UserRole ur where ur.userId = :userId")
    List<UserRole> selectByUserId(String userId);
}
