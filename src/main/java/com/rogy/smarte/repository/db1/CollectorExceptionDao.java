package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.CollectorException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
public interface CollectorExceptionDao extends JpaRepository<CollectorException, Long> {

//    @PersistenceContext
//    private EntityManager entityManager;

    DateTimeFormatter DATEFORMAT = DateTimeFormatter.ofPattern("yyyy-M-d");

//    public List<CollectorException> findAllByDate(String date, int start, int length) throws Exception {
//        if (date == null || date.trim().isEmpty() || start < 0 || length < 1)
//            return null;
//        String sql = "SELECT ce.* FROM collector_exception ce INNER JOIN collector c ON ce.collectorID = c.collectorID WHERE ce.excpdate = '"
//                + LocalDate.parse(date, DATEFORMAT)
//                + "' ORDER BY c.`code`, ce.excpcount DESC LIMIT "
//                + start
//                + ", " + length;
//        Query query = entityManager.createNativeQuery(sql,
//                CollectorException.class);
//        List<CollectorException> result = query.getResultList();
//        return result;
//    }

//    public int getCountByDate(String date) throws Exception {
//        if (date == null || date.trim().isEmpty())
//            return 0;
//        String sql = "SELECT COUNT(*) FROM collector_exception ce INNER JOIN collector c ON ce.collectorID = c.collectorID WHERE ce.excpdate = '"
//                + LocalDate.parse(date, DATEFORMAT) + "'";
//        Query query = entityManager.createNativeQuery(sql);
//        BigInteger count = (BigInteger) query.getSingleResult();
//        return count.intValue();
//    }

//    public CollectorException addOrUpdate(CollectorException collectorException)
//            throws Exception {
//        return entityManager.merge(collectorException);
//    }

//    public List<CollectorException> findAllByDate(String date) throws Exception {
//        return findAllByDate(LocalDate.parse(date, DATEFORMAT));
//    }

    @Query("SELECT c FROM CollectorException c WHERE c.excpdate = :excpdate ORDER BY c.collector.code")
    List<CollectorException> findAllByDate(Date excpdate);

    @Query("SELECT c FROM CollectorException c WHERE c.collector.collectorID = :collectorID AND c.excpdate = :excpdate")
    List<CollectorException> findByCollectorIDAndDate(Integer collectorID, Date excpdate);

}
