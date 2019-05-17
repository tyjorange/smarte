package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.SwitchParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public interface SwitchParamDao extends JpaRepository<SwitchParam, String> {

//	@PersistenceContext
//	 private EntityManager entityManager;

//    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
//            RuntimeException.class, Exception.class})
//    public void addOrUpdateSwitchParam(SwitchParam switchParam) throws Exception {
//        entityManager.merge(switchParam);
//    }

//    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
//            RuntimeException.class, Exception.class})
//    public static SwitchParam addOrUpdateSwitchParam(EntityManager entityManager, SwitchParam switchParam) throws Exception {
//        return entityManager.merge(switchParam);
//    }

//    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
//            RuntimeException.class, Exception.class})
//    public void deleteSwitchParam(SwitchParam sp) throws Exception {
//        entityManager.remove(sp);
//    }

//    public List<SwitchParam> findSwitchParam(SwitchParamExp exp, Integer start, Integer length) throws Exception {
//        String sql = "select sp.* from switch_param sp inner join Switch s on sp.SwitchID = s.SwitchID";
//        if (exp != null) {
//            if (exp.getSwitchID() != null && !exp.getSwitchID().isEmpty()) {
//                sql += " and s.SwitchID = '" + exp.getSwitchID() + "' ";
//            }
//        }
//        sql += " order by sp.paramID asc ";
//        if (start != null && start >= 0 && length != null && length > 0)
//            sql += " limit " + start + ", " + length;
//        Query query = entityManager.createNativeQuery(sql, SwitchParam.class);
//        List<SwitchParam> result = query.getResultList();
//        return result;
//    }

//    public int findSwitchParamCount(SwitchParamExp exp) throws Exception {
//        String sql = "select count(*) from switch_param sp inner join Switch s on sp.SwitchID = s.SwitchID";
//        if (exp != null) {
//            if (exp.getSwitchID() != null && !exp.getSwitchID().isEmpty()) {
//                sql += " and s.SwitchID = '" + exp.getSwitchID() + "' ";
//            }
//        }
//        Query query = entityManager.createNativeQuery(sql);
//        BigInteger count = (BigInteger) query.getSingleResult();
//        return count.intValue();
//    }

    @Query("SELECT s FROM SwitchParam s WHERE s.id = :id")
    List<SwitchParam> findBySwitchParamID(String id);

//    public SwitchParam findBySwitchIDParamID(String switchID, Integer paramID) {
//        return findBySwitchIDParamID(entityManager, switchID, paramID);
//    }

    @Query("SELECT s FROM SwitchParam s WHERE s.switchs.switchID = :switchID AND s.paramID = :paramID")
    List<SwitchParam> findBySwitchIDParamID(Integer switchID, Integer paramID);
}
