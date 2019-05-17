package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminDao extends JpaRepository<Admin, String> {

//	@PersistenceContext
//	private EntityManager entityManager;

    /**
     * 查询所有用户信息
     *
     * @return 用户列表
     * @throws Exception
     */
    //@Modifying
    //@Transactional
    @Query("SELECT a FROM Admin a")
    List<Admin> findAll();


//    Admin addOrUpdateAdmin(Admin admin) throws Exception {
//        Admin an = this.save(admin);
//        return an;
//    }

    @Query("SELECT a FROM Admin a WHERE a.username = :username AND a.password = :password")
    List<Admin> findByUsernameAndPassword(String username, String password);

//    public void deleteAdmin(Admin admin) throws Exception {
//        entityManager.remove(admin);
//    }

    /**
     * 根据ID查询用户信息
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Query("SELECT a FROM Admin a WHERE a.id= :id")
    List<Admin> findByIds(String id);


    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return
     * @throws Exception
     */
    @Query("SELECT a FROM Admin a WHERE a.username= :username")
    List<Admin> findByUserName(String username);

}
