package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.CollectorTraffic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectorTrafficDao extends JpaRepository<CollectorTraffic, Long> {

//    @PersistenceContext
//    private EntityManager entityManager;

//    public CollectorTraffic addOrUpdate(CollectorTraffic collectorTraffic) throws Exception {
//        return entityManager.merge(collectorTraffic);
//    }

//    public List<CollectorTraffic> findByCollectorAndDate(String collectorID, String date) throws Exception {
//        String sql = "SELECT * FROM collector_traffic WHERE collectorID='" + collectorID +
//                "' AND DATE(rwtime)=DATE('" + date + "') ORDER BY id";
//        Query query = entityManager.createNativeQuery(sql, CollectorTraffic.class);
//        return query.getResultList();
//    }
}
