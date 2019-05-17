package com.rogy.smarte.repository.db1;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.rogy.smarte.entity.db1.Admin;
import com.rogy.smarte.entity.db1.Collector;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Administrator
 */
@Repository
public interface CollectorDao extends JpaRepository<Collector, String> {

//	@PersistenceContext
//	private EntityManager entityManager;

//	public void deleteCollector(Collector ct) throws Exception {
//		entityManager.remove(ct);
//	}

//	public Collector addOrUpdateCollector(Collector collector) throws Exception {
//		Collector ct = entityManager.merge(collector);
//		return ct;
//	}

    @Query("SELECT c FROM Collector c WHERE c.fsu.FSUCode = :code")
    List<Collector> findByFsuCode(String code);

    @Query("SELECT c FROM Collector c WHERE c.fsu.fsuid = :fsuid")
    List<Collector> findByFsuID(String fsuid);

    @Query("SELECT c FROM Collector c WHERE c.collectorID = :collectorID")
    List<Collector> findByCollectorID(Integer collectorID);

    @Query("SELECT c FROM Collector c WHERE c.code = :code")
    List<Collector> findByCollectorCode(String code);

    @Query("SELECT c FROM Collector c WHERE c.setupCode = :setupCode")
    List<Collector> findBySetupCode(String setupCode);

//    public List<Collector> findCollector(CollectorExp exp, Integer start, Integer length) throws Exception {
//        String sql = "select c.* from Collector c inner join Fsu f on c.fsuid = f.fsuid";
//        if (exp != null) {
//            if (exp.getCode() != null && !exp.getCode().isEmpty()) {
//                sql += " and c.code like '%" + exp.getCode() + "%' ";
//            }
//            if (exp.getName() != null && !exp.getName().isEmpty()) {
//                sql += " and c.name like '%" + exp.getName() + "%' ";
//            }
//            if (exp.getFsuid() != null && !exp.getFsuid().isEmpty()) {
//                sql += " and c.fsuid = '" + exp.getFsuid() + "' ";
//            }
//
//        }
//        sql += " order by c.code asc ";
//        if (start != null && start >= 0 && length != null && length > 0)
//            sql += "limit " + start + ", " + length;
//        Query query = entityManager.createNativeQuery(sql, Collector.class);
//        List<Collector> result = query.getResultList();
//        return result;
//    }

//    public int findCollectorCount(CollectorExp exp) throws Exception {
//        String sql = "select count(*) from Collector c inner join Fsu f on c.fsuid = f.fsuid";
//        if (exp != null) {
//            if (exp.getCode() != null && !exp.getCode().isEmpty()) {
//                sql += " and c.code like '%" + exp.getCode() + "%' ";
//            }
//            if (exp.getName() != null && !exp.getName().isEmpty()) {
//                sql += " and c.name like '%" + exp.getName() + "%' ";
//            }
//            if (exp.getFsuid() != null && !exp.getFsuid().isEmpty()) {
//                sql += " and c.fsuid = '" + exp.getFsuid() + "' ";
//            }
//
//        }
//        Query query = entityManager.createNativeQuery(sql);
//        BigInteger count = (BigInteger) query.getSingleResult();
//        return count.intValue();
//    }

    /**
     * 修改Collector记录的IP。
     *
     * @param collectorID 记录ID。
     * @return 修改的记录数。
     * @throws Exception
     */
    @Modifying
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Exception.class})
    @Query(value = "UPDATE Collector c SET c.ip = :ip WHERE c.collectorID = :collectorID", nativeQuery = true)
    int updateIpByCollectorID(Integer collectorID, String ip);

    /**
     * 修改Collector记录的活跃状态。
     *
     * @param collectorID 记录ID。
     * @param active      活跃状态。
     * @param activeTime  活跃时间。
     * @return 修改的记录数。
     * @throws Exception
     */
    @Modifying
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Exception.class})
    @Query(value = "UPDATE Collector c SET c.active=:active, c.activeTime = :activeTime WHERE c.collectorID = :collectorID AND (c.activeTime is null OR c.activeTime < :activeTime)", nativeQuery = true)
    void updateActiveByCollectorID(Integer collectorID, int active, LocalDateTime activeTime);

    /**
     * 修改所有Collector记录为非活跃状态。
     *
     * @return 修改的记录数。
     * @throws Exception
     */
    @Modifying
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Exception.class})
    @Query(value = "UPDATE Collector c SET c.active=0, c.activeTime = :activeTime ", nativeQuery = true)
    int clearAllActive(LocalDateTime activeTime);

    /**
     * 修改Collector记录的错误状态。
     *
     * @param collectorID 记录ID。
     * @param faultState  错误状态。
     * @param faultTime   错误时间。
     * @return 修改的记录数。
     * @throws Exception
     */
    @Modifying
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Exception.class})
    @Query(value = "UPDATE Collector c SET c.faultState=:faultState, c.faultTime = :faultTime WHERE c.collectorID = :collectorID", nativeQuery = true)
    int updateFaultByCollectorID(Integer collectorID, int faultState, LocalDateTime faultTime);

    /**
     * 页面修改集中器信息
     *
     * @param c
     * @return
     * @throws Exception
     */
//    public int updateCollector(Collector c) throws Exception {
//        String hsql = "UPDATE Collector c SET c.name = :name, c.baud = :baud, c.freq = :freq, c.ranges = :ranges, c.HBFreq = :HBFreq, c.ioType = :ioType"
//                + " WHERE c.collectorID = :collectorID";
//        Query query = entityManager.createQuery(hsql);
//        query.setParameter("name", c.getName());
//        query.setParameter("baud", c.getBaud());
//        query.setParameter("freq", c.getFreq());
//        query.setParameter("ranges", c.getRanges());
//        query.setParameter("HBFreq", c.getHBFreq());
//        query.setParameter("ioType", c.getIoType());
//        query.setParameter("collectorID", c.getCollectorID());
//        return query.executeUpdate();
//    }

}
