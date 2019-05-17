package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query("SELECT w FROM User AS w WHERE w.id=:id")
    String selectById(@Param("id") Long id);

    @Query("SELECT u FROM User u WHERE u.username = :userName")
    User selectByUserName(String userName);
}
