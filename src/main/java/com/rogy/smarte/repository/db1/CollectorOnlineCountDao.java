package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.CollectorOnlineCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.format.DateTimeFormatter;

@Repository
public interface CollectorOnlineCountDao extends JpaRepository<CollectorOnlineCount, Long> {
//    @PersistenceContext
//    private EntityManager entityManager;

    DateTimeFormatter DATEFORMAT = DateTimeFormatter.ofPattern("yyyy-M-d");

//    public CollectorOnlineCount addOrUpdateCollectorOnlineCount(CollectorOnlineCount collectorOnlineCount) throws Exception {
//        return entityManager.merge(collectorOnlineCount);
//    }

//    public List<CollectorOnlineCount> findByTime(String time) throws Exception {
//        LocalDate date = LocalDate.parse(time, DATEFORMAT);
//        // 记录按DAYOFYEAR(time)%366进行分区。
////		int partitonId = date.getDayOfYear() % 366; // 这天的记录在哪个分区。
//        String sql = "select * from collector_onlinecount where DATE(ontime) = '" + date + "' order by id asc";
//        Query query = entityManager.createNativeQuery(sql,
//                CollectorOnlineCount.class);
//        List<CollectorOnlineCount> result = query.getResultList();
//        return result;
//    }
}
