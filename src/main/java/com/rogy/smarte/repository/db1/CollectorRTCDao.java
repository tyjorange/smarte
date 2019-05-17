package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.CollectorRTC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectorRTCDao extends JpaRepository<CollectorRTC, Long> {

//    @PersistenceContext
//    private EntityManager entityManager;

//    public CollectorRTC addOrUpdateCollectorRTC(CollectorRTC collectorRTC) throws Exception {
//        return entityManager.merge(collectorRTC);
//    }

    @Query("SELECT c FROM CollectorRTC c WHERE c.collector.collectorID = :collectorID ORDER BY c.id DESC")
    List<CollectorRTC> findByCollectorID(Integer collectorID);

    @Query("SELECT c FROM CollectorRTC c WHERE c.collector.code = :code ORDER BY c.id DESC")
    List<CollectorRTC> findByCollectorCode(String code);
}
