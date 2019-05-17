package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.Switch;
import com.rogy.smarte.entity.db1.SwitchWithTimer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.List;

@Repository
public interface SwitchDao extends JpaRepository<Switch, String> {

//	@PersistenceContext
//	private EntityManager entityManager;

//    public Switch addOrUpdateSwitch(Switch switchs) throws Exception {
//        return addOrUpdateSwitch(entityManager, switchs);
//    }
//
//    public static Switch addOrUpdateSwitch(EntityManager entityManager, Switch switchs) throws Exception {
//        return entityManager.merge(switchs);
//    }

//    public void deleteSwitch(Switch sc) throws Exception {
//        entityManager.remove(sc);
//    }

//    public List<Switch> findSwitch(SwitchExp exp, Integer start, Integer length)
//            throws Exception {
//        String sql = "select r.* from Switch r inner join Collector c on r.CollectorID = c.CollectorID";
//        if (exp != null) {
//            if (exp.getSwitchCode() != null && !exp.getSwitchCode().isEmpty()) {
//                sql += " and r.code like '%" + exp.getSwitchCode() + "%' ";
//            }
//            if (exp.getSwitchName() != null && !exp.getSwitchName().isEmpty()) {
//                sql += " and r.name like '%" + exp.getSwitchName() + "%' ";
//            }
//            if (exp.getCollectorID() != null && !exp.getCollectorID().isEmpty()) {
//                sql += " and c.CollectorID = '" + exp.getCollectorID() + "' ";
//            }
//        }
//        sql += " order by r.code asc ";
//        if (start != null && start >= 0 && length != null && length > 0)
//            sql += "limit " + start + ", " + length;
//        Query query = entityManager.createNativeQuery(sql, Switch.class);
//        List<Switch> result = query.getResultList();
//        return result;
//    }

//    public int findSwitchCount(SwitchExp exp) throws Exception {
//        String sql = "select count(*) from Switch r inner join Collector c on r.CollectorID = c.CollectorID";
//        if (exp != null) {
//            if (exp.getSwitchCode() != null && !exp.getSwitchCode().isEmpty()) {
//                sql += " and r.code like '%" + exp.getSwitchCode() + "%' ";
//            }
//            if (exp.getSwitchName() != null && !exp.getSwitchName().isEmpty()) {
//                sql += " and r.name like '%" + exp.getSwitchName() + "%' ";
//            }
//            if (exp.getCollectorID() != null && !exp.getCollectorID().isEmpty()) {
//                sql += " and c.CollectorID = '" + exp.getCollectorID() + "' ";
//            }
//        }
//        Query query = entityManager.createNativeQuery(sql);
//        BigInteger count = (BigInteger) query.getSingleResult();
//        return count.intValue();
//    }

    @Query("SELECT s FROM Switch s WHERE s.collector.collectorID = :collectorID ORDER BY s.sequence")
    List<Switch> findSwitchByCollectorID(Integer collectorID);

    //    @SuppressWarnings("unchecked")
//    public List<SwitchWithTimer> findSwitchWithTimerByCollectorID(String collectorid) {
//        String sql = "SELECT s.switchID, s.code, s.name, s.iconType, s.state, s.sequence, s.fault, s.faultState, count(CASE WHEN tc.state = 1 then 1 else null end) as timercount "
//                + "FROM Switch s left join Time_Controller tc on  s.switchID = tc.switchID "
//                + "WHERE s.collectorID = '" + collectorid + "' "
//                + "group by s.switchID "
//                + "ORDER BY s.sequence";
//        Query query = entityManager.createNativeQuery(sql, SwitchWithTimer.class);
//        List<SwitchWithTimer> switchs = query.getResultList();
//        return switchs;
//    }
//
//    @Query(nativeQuery = true,
//            value = "SELECT s.switchID, s.code, s.name, s.iconType, s.state, s.sequence, s.fault, s.faultState, " +
//                    "COUNT(CASE WHEN tc.state = 1 then 1 else null end) as timercount " +
//                    "FROM collector c, Switch s left join Time_Controller tc on  s.switchID = tc.switchID " +
//                    "WHERE c.collectorID = s.collectorID " +
//                    "AND c.code = :collectorcode " +
//                    "group by s.switchID " +
//                    "ORDER BY s.sequence")
//    List<SwitchWithTimer> findSwitchWithTimerByCollectorCode(String collectorcode);
    
    @SuppressWarnings("unchecked")
	default List<SwitchWithTimer> findSwitchWithTimerByCollectorCode(EntityManager entityManager, String collectorcode) {
    	String sql = "SELECT s.switchID, s.code, s.name, s.iconType, s.state, s.sequence, s.fault, s.faultState, count(CASE WHEN tc.state = 1 then 1 else null end) as timercount "
				+ "FROM collector c, Switch s left join Time_Controller tc on  s.switchID = tc.switchID "
				+ "WHERE c.collectorID = s.collectorID and c.`code` = '" + collectorcode + "' "
				+ "group by s.switchID "
				+ "ORDER BY s.sequence";
		return entityManager.createNativeQuery(sql, SwitchWithTimer.class).getResultList();
    }

    @Query("SELECT s FROM Switch s WHERE s.collector.collectorID = :collectorID ORDER BY s.sequence")
    List<Switch> findByCollectorID(Integer collectorID);

    @Query("SELECT s FROM Switch s WHERE s.collector.collectorID = :collectorID ORDER BY s.code")
    List<Switch> findByCollectorIDOrderByCode(Integer collectorID);

    @Query("SELECT s FROM Switch s WHERE s.collector.code = :code")
    List<Switch> findByCollectorCode(String code);

    @Query("SELECT s FROM Switch s WHERE s.collector.code = :code ORDER BY s.code")
    List<Switch> findByCollectorCodeOrderByCode(String code);

    @Query("SELECT s FROM Switch s WHERE s.code = :code")
    List<Switch> findByCode(String code);

//    public Switch findByCode(EntityManager entityManager, String code) {
//        if (code == null || code.trim().isEmpty())
//            return null;
//        TypedQuery<Switch> query = entityManager.createNamedQuery(
//                "Switch.findByCode", Switch.class);
//        query.setParameter("code", code);
//        List<Switch> result = query.getResultList();
//        if (result == null || result.isEmpty())
//            return null;
//        else
//            return result.get(0);
//    }

    @Query("SELECT s FROM Switch s WHERE s.switchID = :switchID")
    List<Switch> findBySwitchID(Integer switchID);

}
