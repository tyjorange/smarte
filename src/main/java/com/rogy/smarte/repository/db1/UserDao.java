package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao extends JpaRepository<User, String> {

//    @PersistenceContext
//    private EntityManager entityManager;

    @Query("SELECT u FROM User u WHERE u.outerId = :outerID AND u.outerType = :outerType")
    List<User> findByOuterIDAndOuterType(String outerID, String outerType);

    @Query("SELECT u FROM User u WHERE u.phone = :phone")
    List<User> findByPhone(String phone);

    @Query("SELECT u FROM User u WHERE u.phone = :phone AND u.password = :password")
    List<User> findByPhoneAndPassword(String phone, String password);

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.password = :password")
    List<User> findByUsernameAndPassword(String username, String password);

    @Query("SELECT u FROM User u WHERE u.username = :username")
    List<User> findByUsername(String username);

//    public void addOrUpdateUser(User user) throws Exception {
//        entityManager.merge(user);
//    }

    /**
     * 后台增加或修改用户
     *
     * @param user
     * @return
     * @throws Exception
     */
//    List<User> addUpdateUser(User user) throws Exception {
//        User us = entityManager.merge(user);
//        return us;
//    }
//
//    public void deleteUser(User user) throws Exception {
//        entityManager.remove(user);
//    }

    /**
     * 根据ID查询用户信息
     *
     * @param id
     * @return
     * @throws Exception
     */
    @Query("SELECT u FROM User u WHERE u.id = :id")
    List<User> findByIds(String id);

    /**
     * 查询所有用户信息
     *
     * @param
     * @return
     * @throws Exception
     */
//    public List<User> findAll() throws Exception {
//        TypedQuery<User> query = entityManager.createNamedQuery("User.findAll",
//                User.class);
//        List<User> result = query.getResultList();
//        if (result == null || result.isEmpty())
//            return null;
//        return result;
//    }

//    List<User> findUser(UserBean ub, Integer start, Integer length)
//            throws Exception {
//        String sql = "select u.* from User u where 1=1 ";
//        if (ub != null) {
//            if (ub.getUsername() != null && !ub.getUsername().isEmpty()) {
//                sql += " and u.username like '%" + ub.getUsername() + "%' ";
//            }
//        }
//        sql += " order by u.timeMills asc ";
//        if (start != null && start >= 0 && length != null && length > 0)
//            sql += "limit " + start + ", " + length;
//        Query query = entityManager.createNativeQuery(sql, User.class);
//        List<User> result = query.getResultList();
//        return result;
//    }
//
//    public int findUserCount(UserBean ub) throws Exception {
//        String sql = "select  count(*)  from User u where 1=1 ";
//        if (ub != null) {
//            if (ub.getUsername() != null && !ub.getUsername().isEmpty()) {
//                sql += " and u.username like '%" + ub.getUsername() + "%' ";
//            }
//        }
//        Query query = entityManager.createNativeQuery(sql);
//        BigInteger count = (BigInteger) query.getSingleResult();
//        return count.intValue();
//    }

}
