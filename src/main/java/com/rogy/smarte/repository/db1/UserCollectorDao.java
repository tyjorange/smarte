package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.Admin;
import com.rogy.smarte.entity.db1.UserCollector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.List;

@Repository
public interface UserCollectorDao extends JpaRepository<UserCollector, String> {

    //	@PersistenceContext
//	private EntityManager entityManager;
    @Query("SELECT u FROM UserCollector u WHERE u.user.id = :userID AND u.collector.collectorID = :collectorID")
    List<UserCollector> findByUserIDAndCollectorID(String userID, Integer collectorID);

//    public UserCollector addOrUpdateUserCollector(UserCollector userCollector)
//            throws Exception {
//        return entityManager.merge(userCollector);
//    }

    //    public void deleteUserCollector(UserCollector uc) throws Exception {
//        entityManager.remove(uc);
//    }
    @Query("SELECT u FROM UserCollector u WHERE u.collector.collectorID = :collectorID")
    List<UserCollector> findByCollectorID(Integer collectorID);

    @Query("SELECT u FROM UserCollector u WHERE u.user.username = :username AND u.collector.collectorID = :collectorID")
    List<UserCollector> findByUsernameAndCollectorID(String username, Integer collectorID);

    @Query("SELECT u FROM UserCollector u WHERE u.user.id = :userID")
    List<UserCollector> findByUserID(String userID);

//    public List<UserCollector> findFsu(UserCollectorExp exp, Integer start, Integer length) throws Exception {
//        String sql = "select * from user_collector uc WHERE 1=1";
//        if (exp != null) {
//            if (exp.getUsername() != null && !exp.getUsername().isEmpty()) {
//                sql += " and uc.userID in (SELECT id from user  where  username like '%"
//                        + exp.getUsername() + "%')";
//            }
//            if (exp.getCollectorname() != null
//                    && !exp.getCollectorname().isEmpty()) {
//                sql += " and uc.collectorID in (SELECT collectorID from collector  where  name like '%"
//                        + exp.getCollectorname() + "%')";
//            }
//
//        }
//        if (start != null && start >= 0 && length != null && length > 0)
//            sql += " limit " + start + ", " + length;
//        Query query = entityManager.createNativeQuery(sql, UserCollector.class);
//        List<UserCollector> result = query.getResultList();
//        return result;
//    }

    //    public int findUserCollectorCount(UserCollectorExp exp) throws Exception {
//        String sql = "select count(*) from user_collector uc WHERE 1=1";
//        if (exp != null) {
//            if (exp.getUsername() != null && !exp.getUsername().isEmpty()) {
//                sql += " and uc.userID in (SELECT id from user  where  username like '%"
//                        + exp.getUsername() + "%')";
//            }
//            if (exp.getCollectorname() != null
//                    && !exp.getCollectorname().isEmpty()) {
//                sql += " and uc.collectorID in (SELECT collectorID from collector  where  name like '%"
//                        + exp.getCollectorname() + "%')";
//            }
//        }
//        Query query = entityManager.createNativeQuery(sql);
//        BigInteger count = (BigInteger) query.getSingleResult();
//        return count.intValue();
//    }
    @Query("SELECT u FROM UserCollector u WHERE u.collector.setupCode = :setupCode")
    List<UserCollector> findBySetupCode(String setupCode);

}
