package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.TimeOfUsePricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeOfUsePricingDao extends JpaRepository<TimeOfUsePricing, String> {

//	@PersistenceContext
//	private EntityManager entityManager;

    //	public void addOrUpdateTimeOfUsePricing(TimeOfUsePricing toup)
//			throws Exception {
//		entityManager.merge(toup);
//	}
    @Query("SELECT t FROM TimeOfUsePricing t WHERE t.user.id = :userID AND t.timePoint = :timePoint")
    List<TimeOfUsePricing> findByUserIdAndTimePoint(String userID, Integer timePoint);

    @Query("SELECT t FROM TimeOfUsePricing t WHERE t.user.id = :userID ORDER BY t.timePoint ASC")
    List<TimeOfUsePricing> findByUserId(String userID);
}
