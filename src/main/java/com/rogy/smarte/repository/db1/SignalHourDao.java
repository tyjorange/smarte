package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.SignalHour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SignalHourDao extends JpaRepository<SignalHour, Long> {

//    @PersistenceContext
//    private EntityManager entityManager;

//    public void addOrUpdateSignalHour(SignalHour sh) throws Exception {
//        entityManager.merge(sh);
//    }

//    @Query("SELECT s FROM SignalHour s WHERE s.switchs.switchID = :switchID AND s.signalsType.signalsTypeID = :signalsTypeID AND s.time = :time AND s.hour = :hour")
//    List<SignalHour> findBySwitchAndHour(Integer switchID, Short signalsTypeID, Date time, int hour);
//
//    @Query("SELECT s FROM SignalHour s WHERE s.switchs.switchID = :switchID AND s.signalsType.signalsTypeID = :signalsTypeID AND s.time = :time ORDER BY s.hour ASC")
//    List<SignalHour> findBySwitchAndDay(Integer switchID, Short signalsTypeID, Date time);

    @Query(nativeQuery = true,
            value = "SELECT * FROM signal_hour WHERE switchID = :switchID" +
                    " AND signalsTypeID = :signalsTypeID" +
                    " AND time between :startDay AND :endDay" +
                    " ORDER BY time ASC, `hour` ASC")
    List<SignalHour> findBySwitchAndTypeAndMonth(Integer switchID, Short signalsTypeID, String startDay, String endDay);

    @Query("SELECT s FROM SignalHour s WHERE s.switchs.switchID = :switchID AND s.signalsType.signalsTypeID = :signalsTypeID AND s.time = :mTime AND s.hour = :hour")
    List<SignalHour> findBySwitchAndTypeAndHour(Integer switchID, Short signalsTypeID, Date mTime, int hour);

    @Query("SELECT s FROM SignalHour s WHERE s.switchs.switchID = :switchID AND s.signalsType.signalsTypeID = :signalsTypeID AND s.time = :mTime ORDER BY s.hour ASC")
    List<SignalHour> findBySwitchAndTypeAndDay(Integer switchID, Short signalsTypeID, Date mTime);

    @Modifying
    @Query(nativeQuery = true,
            value = "INSERT INTO signal_hour(switchID,signalsTypeID,time,hour,value,statistik) "
                    + "VALUES(:switchID ,:signalsTypeID,:date,:hour,:value,:statistik) "
                    + "ON DUPLICATE KEY UPDATE "
                    + "statistik=statistik+:statistik,value=:value")
    int addOrUpdateSignalHour(Integer switchID, Short signalsTypeID, LocalDate date, int hour, double statistik, double value);

    @Query(nativeQuery = true,
            value = "SELECT * FROM signal_hour "
                    + " WHERE switchID=:switchID AND signalsTypeID=:signalsTypeID"
                    + " ORDER BY time DESC, hour DESC LIMIT 1")
    SignalHour findLatestBySwitchAndType(Integer switchID, Short signalsTypeID);

//    @Query(nativeQuery = true,
//            value = "SELECT SUM(statistik) FROM signal_hour WHERE switchID = :switchID" +
//                    " AND signalsTypeID in (8,10)" +
//                    " AND time BETWEEN :mTime-01 AND :mTime-31 ")
//    double getYgdlAndWgdlByTimeMonth(Integer switchID, String mTime);
//
//    @Query(nativeQuery = true,
//            value = "SELECT SUM(statistik) FROM signal_hour WHERE switchID = :switchID" +
//                    " AND signalsTypeID in (8,10)" +
//                    " AND time BETWEEN :mTime-01-01 AND :mTime-12-31 ")
//    double getYgdlAndWgdlByTimeYear(Integer switchID, String mTime);

    /**
     * YGDL或WGDL 按天进行求和。
     * @param switchID
     * @param signalsTypeID
     * @param startDay 起始日
     * @param endDay 结束日
     * @return
     */
    @Query(nativeQuery = true,
            value = "SELECT SUM(statistik), DATE_FORMAT(time,'%Y-%m-%d') AS dt" +
                    " FROM signal_hour WHERE switchID = :switchID" +
                    " AND signalsTypeID = :signalsTypeID" +
                    " AND time between :startDay AND :endDay" +
                    " GROUP BY dt" +
                    " ORDER BY dt ASC")
    List<Object> getYgdlAndWgdlByMonth(Integer switchID, Short signalsTypeID, String startDay, String endDay);

    /**
     * YGDL或WGDL 按月进行求和。
     * @param switchID
     * @param signalsTypeID
     * @param startDay 起始日
     * @param endDay 结束日
     * @return
     */
    @Query(nativeQuery = true,
            value = "SELECT SUM(statistik), DATE_FORMAT(time,'%Y-%m') AS dt" +
            		" FROM signal_hour WHERE switchID = :switchID" +
                    " AND signalsTypeID = :signalsTypeID" +
                    " AND time between :startDay AND :endDay" +
                    " GROUP BY dt" +
                    " ORDER BY dt ASC")
    List<Object> getYgdlAndWgdlByYear(Integer switchID, Short signalsTypeID, String startDay, String endDay);

    /**
     * @param switchID
     * @param startDay 起始日
     * @param endDay 结束日
     * @param type 查询类型
     * @return
     * @throws Exception
     */
    default double getYgdlAndWgdlByTime(EntityManager entityManager, Integer switchID, String startDay, String endDay, String type)
            throws Exception {
        if (switchID == null || 
        		startDay == null || startDay.trim().isEmpty() ||
                endDay == null || endDay.trim().isEmpty() ||
        		type == null || type.trim().isEmpty())
            return 0;
        String sql = "";
        if (type.equals("day"))
            sql = "SELECT SUM(statistik) FROM signal_hour WHERE switchID = "
                    + switchID
                    + " AND signalsTypeID in (8,10) "
                    + " AND time = '" + startDay + "'";
        else if (type.equals("month"))
            sql = "SELECT SUM(statistik) FROM signal_hour WHERE switchID = "
                    + switchID
                    + " AND signalsTypeID in (8,10) "
                    + " AND time BETWEEN '"
                    + startDay
                    + "' AND '"
                    + endDay
                    + "'";
        else if (type.equals("year"))
            sql = "SELECT SUM(statistik) FROM signal_hour WHERE switchID = "
                    + switchID
                    + " AND signalsTypeID in (8,10) "
                    + " AND time BETWEEN '"
                    + startDay
                    + "' AND '"
                    + endDay
                    + "'";
        else
            return 0;
        Object result = entityManager.createNativeQuery(sql).getSingleResult();
        if (result == null)
            return 0;
        else
            return Double.valueOf(result.toString());
    }

//    @Query(nativeQuery = true,
//            value = "SELECT SUM(statistik) FROM signal_hour" +
//                    " WHERE switchID = :switchID" +
//                    " AND signalsTypeID in (8,10)" +
//                    " AND time = :mTime")
//    double getYgdlAndWgdlByTimeDay(Integer switchID, String mTime);
}
