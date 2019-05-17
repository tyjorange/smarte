package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.CollectorOnline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Repository
public interface CollectorOnlineDao extends JpaRepository<CollectorOnline, Long> {

//    @PersistenceContext
//    private EntityManager entityManager;

    DateTimeFormatter DATEFORMAT = DateTimeFormatter.ofPattern("yyyy-M-d");

//    public List<CollectorOnline> findByCollectorAndTime(String collectorID, String time) throws Exception {
//        if (collectorID == null || collectorID.trim().isEmpty() || time == null
//                || time.trim().isEmpty())
//            return null;
//        LocalDate date = LocalDate.parse(time, DATEFORMAT);
//        // 记录按DAYOFYEAR(time)%366进行分区。
//        int partitonId = date.getDayOfYear() % 366; // 这天的记录在哪个分区。
//        String sql = "select *,time_to_sec(ontime) as seconds from collector_online PARTITION (p" + partitonId
//                + ") where collectorID = '" + collectorID
//                + "' and YEAR(ontime) = " + date.getYear() + " order by id asc";
//        Query query = entityManager.createNativeQuery(sql, CollectorOnline.class);
//        List<CollectorOnline> result = query.getResultList();
//        return result;
//    }

//    public CollectorOnline addOrUpdateCollectorOnline(CollectorOnline collectorOnline) throws Exception {
//        return entityManager.merge(collectorOnline);
//    }

    /**
     * 增加所有Collector离线记录。
     *
     * @return 修改的记录数。
     * @throws Exception
     */
    @Modifying
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Exception.class})
    @Query(value = "INSERT INTO collector_online(id,collectorID,ontime,onstatus,reason) SELECT 0,collectorID,:activeTime,0,:reason FROM collector", nativeQuery = true)
    int clearAllOnline(LocalDateTime activeTime, int reason);
}
