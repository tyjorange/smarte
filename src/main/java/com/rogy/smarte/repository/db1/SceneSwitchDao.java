package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.SceneSwitch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.List;

@Repository
public interface SceneSwitchDao extends JpaRepository<SceneSwitch, String> {

//    @PersistenceContext
//    private EntityManager entityManager;

//    public void addOrUpdateSceneSwitch(SceneSwitch sceneSwitch)
//            throws Exception {
//        entityManager.merge(sceneSwitch);
//    }

    @Query("SELECT s FROM SceneSwitch s WHERE s.id = :sceneSwitchID")
    List<SceneSwitch> findBySceneSwitchID(String sceneSwitchID);

//    public void deleteSceneSwitch(SceneSwitch sceneSwitch) throws Exception {
//        entityManager.remove(sceneSwitch);
//    }

    @Query("SELECT s FROM SceneSwitch s WHERE s.scene.sceneID = :sceneID ORDER BY s.switchs.collector.collectorID")
    List<SceneSwitch> findBySceneID(Integer sceneID);

    @Query("SELECT s FROM SceneSwitch s WHERE s.scene.sceneID = :sceneID AND s.switchs.switchID = :switchID")
    List<SceneSwitch> findBySceneIDAndSwitchID(Integer sceneID, Integer switchID);

    @Modifying
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Exception.class})
    @Query(value = "DELETE FROM SceneSwitch s WHERE s.scene.sceneID = :sceneID")
    void deleteAllSwitchsOfScene(Integer sceneID);

//    public List<SceneSwitch> findSceneSwitch(SceneSwitchExp exp, Integer start, Integer length) throws Exception {
//        String sql = "select * from scene_switch ss WHERE 1=1";
//        if (exp != null) {
//            if (exp.getSwitchname() != null && !exp.getSwitchname().isEmpty()) {
//                sql += " and ss.switchID in (SELECT switchID from switch  where  name like '%" + exp.getSwitchname() + "%')";
//            }
//            if (exp.getScenename() != null && !exp.getScenename().isEmpty()) {
//                sql += " and ss.sceneID in (SELECT sceneID  from scene  where  name like '%" + exp.getScenename() + "%')";
//            }
//
//        }
//        if (start != null && start >= 0 && length != null && length > 0)
//            sql += " limit " + start + ", " + length;
//        Query query = entityManager.createNativeQuery(sql, SceneSwitch.class);
//        List<SceneSwitch> result = query.getResultList();
//        return result;
//    }

//    public int findSceneSwitchCount(SceneSwitchExp exp) throws Exception {
//        String sql = "select count(*) from scene_switch ss WHERE 1=1";
//        if (exp != null) {
//            if (exp.getSwitchname() != null && !exp.getSwitchname().isEmpty()) {
//                sql += " and ss.switchID in (SELECT switchID from switch  where  name like '%" + exp.getSwitchname() + "%')";
//            }
//            if (exp.getScenename() != null && !exp.getScenename().isEmpty()) {
//                sql += " and ss.sceneID in (SELECT sceneID  from scene  where  name like '%" + exp.getScenename() + "%')";
//            }
//        }
//        Query query = entityManager.createNativeQuery(sql);
//        BigInteger count = (BigInteger) query.getSingleResult();
//        return count.intValue();
//    }

}
