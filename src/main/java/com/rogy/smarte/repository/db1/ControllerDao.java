package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.Controller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ControllerDao extends JpaRepository<Controller, String> {

//    @PersistenceContext
//    private EntityManager entityManager;

    @Query(nativeQuery = true,
            value = "SELECT c.* FROM controller c INNER JOIN switch s ON c.targetID = s.switchID " +
                    " INNER JOIN user_collector uc ON uc.collectorID = s.collectorID " +
                    " WHERE c.targetType = 0 AND uc.userID = :userID" +
                    " UNION SELECT c.* FROM controller c " +
                    " INNER JOIN scene se ON c.targetID = se.sceneID " +
                    " WHERE c.targetType = 1 AND se.userID = :userID" +
                    " ORDER BY runTime DESC LIMIT :start , :mLength")
    List<Controller> findControllerByUser(String userID, int start, int mLength);

//    public List<Controller> findOperateSwitchByUser(String userID, int start, int length) throws Exception {
//        if (userID == null || userID.trim().isEmpty() || start < 0
//                || length < 1)
//            return null;
//        String sql = "SELECT c.* FROM controller c INNER JOIN switch s ON s.switchID = c.targetID INNER JOIN collector cr ON cr.collectorID = s.collectorID INNER JOIN user_collector uc ON uc.collectorID = cr.collectorID WHERE uc.userID = '"
//                + userID
//                + "' AND c.targetType = 0 AND c.runTime IS NOT NULL ORDER BY runTime DESC LIMIT "
//                + start + ", " + length;
//        Query query = entityManager.createNativeQuery(sql, Controller.class);
//        List<Controller> result = query.getResultList();
//        return result;
//    }

//    public List<Controller> findOperateSceneByUser(String userID, int start, int length) throws Exception {
//        if (userID == null || userID.trim().isEmpty() || start < 0
//                || length < 1)
//            return null;
//        String sql = "SELECT c.* FROM controller c INNER JOIN scene s ON c.targetID = s.sceneID WHERE s.userID = '"
//                + userID
//                + "' AND c.targetType = 1 AND runTime IS NOT NULL ORDER BY runTime DESC LIMIT "
//                + start + ", " + length;
//        Query query = entityManager.createNativeQuery(sql, Controller.class);
//        List<Controller> result = query.getResultList();
//        return result;
//    }

//    public void deleteController(Controller cl) throws Exception {
//        entityManager.remove(cl);
//    }

//    public List<Controller> findController(ControllerExp exp, Integer start, Integer length) throws Exception {
//        String sql = "select c.* from controller c inner join Switch s on c.SwitchID = s.SwitchID";
//        if (exp != null) {
//            if (exp.getSwitchID() != null && !exp.getSwitchID().isEmpty()) {
//                sql += " and s.SwitchID = '" + exp.getSwitchID() + "' ";
//            }
//        }
//        sql += " order by c.genTime desc ";
//        if (start != null && start >= 0 && length != null && length > 0)
//            sql += " limit " + start + ", " + length;
//        Query query = entityManager.createNativeQuery(sql, Controller.class);
//        List<Controller> result = query.getResultList();
//        return result;
//    }

    //    public int findControllerCount(ControllerExp exp) throws Exception {
//        String sql = "select count(*) from controller c inner join Switch s on c.SwitchID = s.SwitchID";
//        if (exp != null) {
//            if (exp.getSwitchID() != null && !exp.getSwitchID().isEmpty()) {
//                sql += " and s.SwitchID = '" + exp.getSwitchID() + "' ";
//            }
//        }
//        Query query = entityManager.createNativeQuery(sql);
//        BigInteger count = (BigInteger) query.getSingleResult();
//        return count.intValue();
//    }
    @Query("SELECT c FROM Controller c WHERE c.controllerID = :controllerID")
    List<Controller> findByControllerID(Long controllerID);

    @Query("SELECT c FROM Controller c WHERE c.controllerID = :controllerID")
    List<Controller> findByControllerIDWithClear(Long controllerID);

//    public Controller addOrUpdateController(Controller controller)
//            throws Exception {
//        return addOrUpdateController(entityManager, controller);
//    }

//    public static Controller addOrUpdateController(EntityManager entityManager,
//                                                   Controller controller) throws Exception {
//        Controller c = entityManager.merge(controller);
//        return c;
//    }

    @Query("SELECT c FROM Controller c WHERE c.runCode = NULL order by c.genTime")
    List<Controller> findNotRun();

    @Query("SELECT c FROM Controller c WHERE c.runId = :runId AND c.runNo = :runNo")
    List<Controller> findRun(int runId, int runNo);
}
