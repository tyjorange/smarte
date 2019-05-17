package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.CollectorShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public interface CollectorShareDao extends JpaRepository<CollectorShare, Long> {

//    @PersistenceContext
//    private EntityManager entityManager;

//    public void deleteCollectorShare(CollectorShare cs) throws Exception {
//        if (cs == null)
//            return;
//        entityManager.remove(cs);
//    }

//    public void addOrUpdateCollectorShare(CollectorShare cs) throws Exception {
//        entityManager.merge(cs);
//    }

    @Query("SELECT c FROM CollectorShare c WHERE c.collector.collectorID = :collectorID AND c.user.id = :userID")
    List<CollectorShare> findByCollectorAndUser(Integer collectorID, String userID);

    @Query("SELECT c FROM CollectorShare c WHERE c.user.id = :userID")
    List<CollectorShare> findByUserID(String userID);

    @Query("SELECT c FROM CollectorShare c WHERE c.collector.collectorID = :collectorID")
    List<CollectorShare> findByCollectorID(Integer collectorID);
}
