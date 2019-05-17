package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.TimeController;
import com.rogy.smarte.entity.db1.ZData;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface TimeControllerDao extends JpaRepository<TimeController, Long> {

//    @PersistenceContext
//    private EntityManager entityManager;

    @Query(nativeQuery = true,
            value = "SELECT tc.* FROM time_controller tc " +
                    "INNER JOIN switch s ON tc.switchID = s.switchID " +
                    "INNER JOIN user_collector uc ON uc.collectorID = s.collectorID " +
                    "WHERE uc.userID = :userID")
    List<TimeController> findByUser(String userID);

//	public TimeController addOrUpdateTimeController(TimeController timeController)
//			throws Exception {
//		return entityManager.merge(timeController);
//	}

//	public void deleteTimeController(TimeController timeController)
//			throws Exception {
//		entityManager.remove(timeController);
//	}

//	public List<TimeController> findAll() throws Exception {
//		TypedQuery<TimeController> query = entityManager.createNamedQuery(
//				"TimeController.findAll", TimeController.class);
//		List<TimeController> result = query.getResultList();
//		return result;
//	}

    @Query("SELECT t FROM TimeController t WHERE t.state <> 0")
    List<TimeController> findAllEnabled();

    @Query("SELECT t FROM TimeController t WHERE t.id = :timeControllerID")
    List<TimeController> findByTimeControllerID(Long timeControllerID);

    @Query("SELECT t FROM TimeController t WHERE t.switchs.switchID = :switchID")
    List<TimeController> findBySwitchID(Integer switchID);

    @Query("SELECT t FROM TimeController t WHERE t.switchs.collector.collectorID = :collectorID")
    List<TimeController> findByCollectorID(Integer collectorID);
    
    @SuppressWarnings("unchecked")
	default List<TimeController> findByIDList(EntityManager entityManager, String list) {
    	String sql = String.format("SELECT * FROM time_controller WHERE id IN %s", list);
    	return entityManager.createNativeQuery(sql, TimeController.class).getResultList();
    }

    /**
     * 通过switchid和开关状态和是否有效，查找定时控制记录
     *
     * @param tc
     * @return
     * @throws Exception
     */
//    public List<TimeController> findBySwitchIDAndOthers(TimeController tc,    int start, int length) throws Exception {
//        StringBuilder sql = new StringBuilder(
//                "select * from time_controller where id is not null ");
//        List<TimeController> list = new ArrayList<TimeController>();
//        if (tc.getState() != -1) {
//            sql.append(" and state=" + tc.getState());
//        }
//        if (tc.getCmdData() != -1) {
//            sql.append(" and cmdData=" + tc.getCmdData());
//        }
//
//        if (tc != null && tc.getSwitchs() != null
//                && tc.getSwitchs().getSwitchID() != null
//                && !tc.getSwitchs().getSwitchID().isEmpty()) {
//            sql.append(" and switchID='" + tc.getSwitchs().getSwitchID()
//                    + "'  ");
//        }
//        sql.append(" order by runTime");
//        sql.append(" limit " + start + "," + length);
//        Query query = entityManager.createNativeQuery(sql.toString(),
//                TimeController.class);
//        list = query.getResultList();
//        return list;
//    }

    /**
     * 通过switchid和开关状态和是否有效，查找定时控制记录
     *
     * @param tc
     * @return
     * @throws Exception
     */
//    public int findBySwitchIDAndOthers(TimeController tc) throws Exception {
//        StringBuilder sql = new StringBuilder(
//                "select * from time_controller where id is not null ");
//        List<TimeController> list = new ArrayList<TimeController>();
//        if (tc.getState() != -1) {
//            sql.append(" and state=" + tc.getState());
//        }
//        if (tc.getCmdData() != -1) {
//            sql.append(" and cmdData=" + tc.getCmdData());
//        }
//
//        if (tc != null && tc.getSwitchs() != null
//                && tc.getSwitchs().getSwitchID() != null
//                && !tc.getSwitchs().getSwitchID().isEmpty()) {
//            sql.append(" and switchID='" + tc.getSwitchs().getSwitchID()
//                    + "'  ");
//        }
//        sql.append(" order by runTime");
//        Query query = entityManager.createNativeQuery(sql.toString(),
//                TimeController.class);
//        list = query.getResultList();
//        return list.size();
//    }

    /**
     * 除主键外，查找是否存在同样其他内容的定时控制记录
     */
    @Query("SELECT t FROM TimeController t WHERE t.switchs.switchID= :switchID and t.state= :state and t.cmdData= :cmdData and t.weekday= :weekday and t.runTime = :runTime")
    List<TimeController> findByOthers(String switchID, String state, String cmdData, String weekday, String runTime);
}
