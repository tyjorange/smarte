package com.rogy.smarte.repository.db1;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.rogy.smarte.entity.db1.ApexDay;

import java.sql.Date;
import java.util.List;

@Repository
public interface ApexDayDao extends JpaRepository<ApexDay, String> {

//	@PersistenceContext
//	private EntityManager entityManager;

//	public ApexDay addOrUpdateApexDay(ApexDay apex) throws Exception {
//		ApexDay ax = entityManager.merge(apex);
//		return ax;
//	}

    @Query("SELECT a FROM ApexDay a WHERE a.switchs.switchID = :switchID AND a.signalsType.signalsTypeID = :signalsTypeID AND a.time = :time")
    List<ApexDay> findBySwitchAndSignalsTypeAndTime(Integer switchID, Short signalsTypeID, Date time);


    @Query(nativeQuery = true,
            value = "SELECT * FROM apex_day WHERE switchID = :switchID" +
                    " AND signalsTypeID = :signalsTypeID  " +
                    " AND time BETWEEN :startDay AND :endDay " +
                    " ORDER BY time ASC")
    List<ApexDay> findByMonth(Integer switchID, Short signalsTypeID, String startDay, String endDay);

    @Modifying
    @Query(nativeQuery = true,
            value = "UPDATE apex_day SET maxDay=:mMax,minDay=:mMin WHERE id=:mId AND switchID=:switchID AND signalsTypeID=:signalsTypeID")
    int updateApexDay(Long mId, Integer switchID, Short signalsTypeID, double mMax, double mMin);
    //"UPDATE apex_day SET maxDay=%f,minDay=%f WHERE id=%d AND switchID=%d AND signalsTypeID=%d"
}
