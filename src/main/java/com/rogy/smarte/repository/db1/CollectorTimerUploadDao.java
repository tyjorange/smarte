package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.CollectorTimerUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface CollectorTimerUploadDao extends JpaRepository<CollectorTimerUpload, Long> {

//    @PersistenceContext
//    private EntityManager entityManager;

//    public CollectorTimerUpload addOrUpdateCollectorTimerUpload(CollectorTimerUpload collectorTimerUpload) throws Exception {
//        return entityManager.merge(collectorTimerUpload);
//    }

    @Query("SELECT c FROM CollectorTimerUpload c WHERE c.collector.code = :code AND c.msgId = :msgId AND c.msgNo = :msgNo ORDER BY c.id DESC")
    List<CollectorTimerUpload> findByCollectorCodeAndMsg(String code, int msgId, int msgNo);

    @Query("SELECT c FROM CollectorTimerUpload c WHERE c.collector.collectorID = :collectorID ORDER BY c.id DESC")
    List<CollectorTimerUpload> findByCollectorID(Integer collectorID);

    @Query("SELECT c FROM CollectorTimerUpload c WHERE c.resultTime IS NULL AND c.uploadTime <= :beforeTime ORDER BY c.id")
    public List<CollectorTimerUpload> findNoResultBefore(Timestamp beforeTime);
}
