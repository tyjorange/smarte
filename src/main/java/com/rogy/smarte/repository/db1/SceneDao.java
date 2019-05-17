package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.Scene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SceneDao extends JpaRepository<Scene, String> {

//    @PersistenceContext
//    private EntityManager entityManager;

//    public void addOrUpdateScene(Scene scene) throws Exception {
//        entityManager.merge(scene);
//    }

    @Query("SELECT s FROM Scene s WHERE s.sceneID = :sceneID")
    List<Scene> findBySceneID(Integer sceneID);

//    public void deleteScene(Scene scene) throws Exception {
//        entityManager.remove(scene);
//    }

    @Query("SELECT s FROM Scene s WHERE s.user.id = :userID")
    List<Scene> findByUserID(String userID);

    @Query("SELECT s FROM Scene s WHERE s.user.id = :userID AND s.name = :name")
    List<Scene> findByUserIDAndName(String userID, String name);


//    public List<Scene> findScene(SceneExp exp, Integer start, Integer length) throws Exception {
//        String sql = "select * from scene s WHERE 1=1";
//        if (exp != null) {
//            if (exp.getUsername() != null && !exp.getUsername().isEmpty()) {
//                sql += " and s.userID in (SELECT id from user  where  username like '%" + exp.getUsername() + "%')";
//            }
//            if (exp.getName() != null && !exp.getName().isEmpty()) {
//                sql += " and s.name like '%" + exp.getName() + "%' ";
//                ;
//            }
//
//        }
//        if (start != null && start >= 0 && length != null && length > 0)
//            sql += " limit " + start + ", " + length;
//        Query query = entityManager.createNativeQuery(sql, Scene.class);
//        List<Scene> result = query.getResultList();
//        return result;
//    }

//    public int findSceneCount(SceneExp exp) throws Exception {
//        String sql = "select count(*) from scene s WHERE 1=1";
//        if (exp != null) {
//            if (exp.getUsername() != null && !exp.getUsername().isEmpty()) {
//                sql += " and s.userID in (SELECT id from user  where  username like '%" + exp.getUsername() + "%')";
//            }
//            if (exp.getName() != null && !exp.getName().isEmpty()) {
//                sql += " and s.name like '%" + exp.getName() + "%' ";
//                ;
//            }
//
//        }
//        Query query = entityManager.createNativeQuery(sql);
//        BigInteger count = (BigInteger) query.getSingleResult();
//        return count.intValue();
//    }

}
