package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.SignalsNew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SignalsNewDao extends JpaRepository<SignalsNew, String> {

//	@PersistenceContext
//	private EntityManager entityManager;

    @Query("SELECT s FROM SignalsNew s WHERE s.signalstype.signalsTypeID = 1 AND s.value > 0")
    List<SignalsNew> findSignalsNewWithDL();

    @Query("SELECT sn FROM SignalsNew sn WHERE sn.switchs.switchID = :switchID")
    List<SignalsNew> findBySwitch(Integer switchID);

//    public void addOrUpdateSignalsNew(SignalsNew signalsNew) throws Exception {
//        addOrUpdateSignalsNew(entityManager, signalsNew);
//    }
//
//    public static void addOrUpdateSignalsNew(EntityManager entityManager, SignalsNew signalsNew) throws Exception {
//        entityManager.merge(signalsNew);
//    }

    @Modifying
    @Query(nativeQuery = true,
            value = "UPDATE signals_new set time=:timeFormat,value=:dvalue WHERE id=:id AND switchID=:switchID AND signalsTypeID=:signalsTypeID")
    public int updateSignalsNew(Long id, Integer switchID, Short signalsTypeID, LocalDateTime timeFormat, double dvalue);
//    public SignalsNew findBySwitchIDAndSignalsTypeID(String switchID,
//                                                     String signalsTypeID) throws Exception {
//        return findBySwitchIDAndSignalsTypeID(entityManager, switchID, signalsTypeID);
//    }

    @Query("SELECT s FROM SignalsNew s WHERE s.switchs.switchID = :switchID AND s.signalstype.signalsTypeID = :signalsTypeID")
    List<SignalsNew> findBySwitchIDAndSignalsTypeID(Integer switchID, Short signalsTypeID);

//    public List<SignalsNew> findSignalsNew(SignalsNewExp exp, Integer start, Integer length) throws Exception {
//        String sql = "select * from signals_new sn WHERE 1=1";
//        if (exp != null) {
//            if (exp.getSwitchid() != null && !exp.getSwitchid().isEmpty()) {
//                sql += " and sn.switchID  = '" + exp.getSwitchid() + "' ";
//            }
//            if (exp.getSignalstypeid() != null && !exp.getSignalstypeid().isEmpty()) {
//                sql += " and sn.signalsTypeID  = '" + exp.getSignalstypeid() + "' ";
//            }
//
//        }
//        if (start != null && start >= 0 && length != null && length > 0)
//            sql += " limit " + start + ", " + length;
//        Query query = entityManager.createNativeQuery(sql, SignalsNew.class);
//        List<SignalsNew> result = query.getResultList();
//        return result;
//    }

//    public int findSignalsNewCount(SignalsNewExp exp) throws Exception {
//        String sql = "select count(*) from signals_new sn WHERE 1=1";
//        if (exp != null) {
//            if (exp.getSwitchid() != null && !exp.getSwitchid().isEmpty()) {
//                sql += " and sn.switchID  = '" + exp.getSwitchid() + "' ";
//            }
//            if (exp.getSignalstypeid() != null && !exp.getSignalstypeid().isEmpty()) {
//                sql += " and sn.signalsTypeID  = '" + exp.getSignalstypeid() + "' ";
//            }
//
//        }
//        Query query = entityManager.createNativeQuery(sql);
//        BigInteger count = (BigInteger) query.getSingleResult();
//        return count.intValue();
//    }

}
