package com.rogy.smarte.repository.db1;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.rogy.smarte.entity.db1.ApexMonth;

import java.util.List;

@Repository
public interface ApexMonthDao extends JpaRepository<ApexMonth, String> {

//	@PersistenceContext
//	private EntityManager entityManager;

    /*
     * public ApexMonth addOrUpdateApexMonth(ApexMonth apex) throws Exception {
     * ApexMonth ax = entityManager.merge(apex); return ax; }
     */
    @Query("SELECT a FROM ApexMonth a WHERE a.switchs.switchID = :switchID AND a.signalsType.signalsTypeID = :signalsTypeID AND a.timeYear = :timeYear AND a.timeMonth = :timeMonth")
    List<ApexMonth> findBySwitchAndSignalsTypeAndTime(Integer switchID, Short signalsTypeID, Integer timeYear, Integer timeMonth);


    @Query(nativeQuery = true,
            value = "SELECT * FROM apex_Month WHERE switchID = :switchID" +
                    " AND signalsTypeID = :signalsTypeID + " +
                    " AND time_year = :mYear " +
                    " ORDER BY time_month ASC")
    List<ApexMonth> findByYear(Integer switchID, Short signalsTypeID, int mYear);


    @Modifying
    @Query(nativeQuery = true, value = "UPDATE apex_month SET maxMonth=:mMax,minMonth=:mMin WHERE id=:mId AND switchID=:switchID AND signalsTypeID=:signalsTypeID")
    int updateApexMonth(Long mId, Integer switchID, Short signalsTypeID, double mMax, double mMin);
    //"UPDATE apex_month SET maxMonth=%f,minMonth=%f WHERE id=%d AND switchID=%d AND signalsTypeID=%d"
}
