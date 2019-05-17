package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.Condition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConditionDao extends JpaRepository<Condition, String> {
//    @PersistenceContext
//    private EntityManager entityManager;

    /**
     * 新增或修改
     *
     * @param region
     * @return
     * @throws Exception
     */
//    public Condition addOrUpdateCondition(Condition condition) throws Exception {
//        Condition cond = entityManager.merge(condition);
//        return cond;
//    }

    /**
     * 通过conditionsID查找
     *
     * @param conditionsID
     * @return
     * @throws Exception
     */
    @Query("SELECT c FROM Condition c WHERE c.conditionsID=:conditionsID")
    List<Condition> findByIds(String conditionsID);

//    public void deleteCondition(Condition automation) throws Exception {
//        entityManager.remove(automation);
//    }

    /**
     * 2017 12 27
     * 按条件查找
     *
     * @param bean
     * @param start
     * @param length
     * @return
     */
//    public List<Condition> findByCondition(ConditionBean bean, Integer start, Integer length) throws Exception {
//        StringBuilder sql = new StringBuilder("SELECT con.* "
//                + " FROM conditions con,automation a,switch s,collector c,fsu f,subregion sub,region r,signalstype sig "
//                + " where con.automationID=a.automationID "
//                + " and a.switchID=s.switchID "
//                + " and s.collectorID=c.collectorID "
//                + " and c.fsuID=f.fsuID "
//                + " and f.SubRegionID=sub.SubRegionID "
//                + " and sub.RegionID=r.RegionID "
//                + " and con.signalsTypeID=sig.signalsTypeID");
//        if (bean != null) {
//            if (bean.getRegionID() != null && !bean.getRegionID().isEmpty()) {
//                sql.append(" and r.RegionID='" + bean.getRegionID() + "'");
//            } else {
//                sql.append(" and r.RegionID='  '");
//            }
//            if (bean.getSubRegionID() != null && !bean.getSubRegionID().isEmpty()) {
//                sql.append(" and sub.SubRegionID='" + bean.getSubRegionID() + "'");
//            }
//            if (bean.getFsuID() != null && !bean.getFsuID().isEmpty()) {
//                sql.append(" and f.fsuID='" + bean.getFsuID() + "'");
//            }
//            if (bean.getCollectorID() != null && !bean.getCollectorID().isEmpty()) {
//                sql.append(" and c.collectorID='" + bean.getCollectorID() + "'");
//            }
//            if (bean.getSwitchID() != null && !bean.getSwitchID().isEmpty()) {
//                sql.append(" and s.switchID='" + bean.getSwitchID() + "'");
//            }
//            if (bean.getAutomationID() != null && !bean.getAutomationID().isEmpty()) {
//                sql.append(" and a.automationID='" + bean.getAutomationID() + "'");
//            }
//            if (bean.getSignalsTypeID() != null && !bean.getSignalsTypeID().isEmpty()) {
//                sql.append(" and sig.signalsTypeID='" + bean.getSignalsTypeID() + "'");
//            }
//        }
//        if (start != null && length != null && start >= 0 && length > 0)
//            sql.append(" limit " + start + ", " + length);
//        Query query = entityManager.createNativeQuery(sql.toString(),
//                Condition.class);
//        List<Condition> result = query.getResultList();
//        return result;
//    }

    /**
     * 2017 12 27
     * 查找计数
     *
     * @param bean
     * @return
     * @throws Exception
     */
//    public int findByCondition(ConditionBean bean) throws Exception {
//        StringBuilder sql = new StringBuilder("SELECT count(*) "
//                + " FROM conditions con,automation a,switch s,collector c,fsu f,subregion sub,region r,signalstype sig "
//                + " where con.automationID=a.automationID "
//                + " and a.switchID=s.switchID "
//                + " and s.collectorID=c.collectorID "
//                + " and c.fsuID=f.fsuID "
//                + " and f.SubRegionID=sub.SubRegionID "
//                + " and sub.RegionID=r.RegionID "
//                + " and con.signalsTypeID=sig.signalsTypeID");
//        if (bean != null) {
//            if (bean.getRegionID() != null && !bean.getRegionID().isEmpty()) {
//                sql.append(" and r.RegionID='" + bean.getRegionID() + "'");
//            } else {
//                sql.append(" and r.RegionID='  '");
//            }
//            if (bean.getSubRegionID() != null && !bean.getSubRegionID().isEmpty()) {
//                sql.append(" and sub.SubRegionID='" + bean.getSubRegionID() + "'");
//            }
//            if (bean.getFsuID() != null && !bean.getFsuID().isEmpty()) {
//                sql.append(" and f.fsuID='" + bean.getFsuID() + "'");
//            }
//            if (bean.getCollectorID() != null && !bean.getCollectorID().isEmpty()) {
//                sql.append(" and c.collectorID='" + bean.getCollectorID() + "'");
//            }
//            if (bean.getSwitchID() != null && !bean.getSwitchID().isEmpty()) {
//                sql.append(" and s.switchID='" + bean.getSwitchID() + "'");
//            }
//            if (bean.getAutomationID() != null && !bean.getAutomationID().isEmpty()) {
//                sql.append(" and a.automationID='" + bean.getAutomationID() + "'");
//            }
//            if (bean.getSignalsTypeID() != null && !bean.getSignalsTypeID().isEmpty()) {
//                sql.append(" and sig.signalsTypeID='" + bean.getSignalsTypeID() + "'");
//            }
//        }
//        Query query = entityManager.createNativeQuery(sql.toString());
//        BigInteger count = (BigInteger) query.getSingleResult();
//        int countInt = count.intValue();
//        return countInt;
//    }

    /**
     * 2017 12 27
     * 判断是否存在
     *
     * @param automationID
     * @param signalsTypeID
     * @return
     */
//    public boolean checkByID(String automationID, String signalsTypeID) {
//        if (automationID == null || automationID.isEmpty() || signalsTypeID == null || signalsTypeID.isEmpty()) {
//            return false;
//        }
//        String sql = String.format("SELECT c.* FROM conditions c "
//                + " WHERE c.automationID='%s' "
//                + " and c.signalsTypeID='%s' ", automationID, signalsTypeID);
//        Query query = entityManager.createNativeQuery(sql, Condition.class);
//        List<Condition> conditions = query.getResultList();
//        int count = conditions.size();
//        if (count > 0) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    /**
     * 2018 1 2
     *
     * @param automationID
     * @param signalsTypeID
     * @return
     */
//    public List<Condition> trueOrFalse(String automationID, String signalsTypeID) {
//        if (automationID == null || automationID.isEmpty() || signalsTypeID.trim() == null || signalsTypeID.trim().isEmpty()) {
//            return null;
//        }
//        String sql = String.format("SELECT c.* FROM conditions c "
//                + " WHERE c.automationID='%s' "
//                + " and c.signalsTypeID='%s' ", automationID, signalsTypeID);
//        Query query = entityManager.createNativeQuery(sql, Condition.class);
//        List<Condition> conditions = query.getResultList();
//        return conditions;
//    }


}
