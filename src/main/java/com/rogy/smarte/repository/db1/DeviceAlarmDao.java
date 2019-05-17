package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.DeviceAlarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface DeviceAlarmDao extends JpaRepository<DeviceAlarm, Long> {

//    @PersistenceContext
//    private EntityManager entityManager;

    @Query(nativeQuery = true,
            value = "SELECT da.* FROM device_alarm da " +
                    " INNER JOIN switch s ON da.deviceID = s.switchID " +
                    " INNER JOIN user_collector uc ON uc.collectorID = s.collectorID " +
                    " WHERE uc.userID = :userID" +
                    " AND da.deviceType = 2 ORDER BY da.time " +
                    " DESC LIMIT :start , :mLength")
    List<DeviceAlarm> findSwitchAlarmByUser(String userID, int start, int mLength);

    @Query(nativeQuery = true,
            value = "SELECT da.* FROM device_alarm AS da" +
                    " INNER JOIN user_collector uc ON da.deviceID = uc.collectorID " +
                    " WHERE uc.userID = :userID" +
                    " AND da.deviceType = 1 ORDER BY da.time DESC LIMIT " +
                    ":start , :mLength")
    List<DeviceAlarm> findCollectorAlarmByUser(String userID, int start, int mLength);

    @Query("SELECT d FROM DeviceAlarm d WHERE d.time > :time order by d.time DESC")
    List<DeviceAlarm> findByTime(Timestamp time);

    /**
     * 查询所有记录。
     *
     * @return 记录列表。
     * @throws Exception
     */
//    public List<DeviceAlarm> findAlls() throws Exception {
//        TypedQuery<DeviceAlarm> query = entityManager.createNamedQuery(
//                "DeviceAlarm.findAll", DeviceAlarm.class);
//        List<DeviceAlarm> result = query.getResultList();
//        return result;
//    }

//    public DeviceAlarm addOrUpdateDeviceAlarm(DeviceAlarm deviceAlarm)
//            throws Exception {
//        return entityManager.merge(deviceAlarm);
//    }

    /**
     * 查询指定的设备告警记录。
     *
     * @return 设备告警记录。
     * @throws Exception
     */
    @Query("SELECT d FROM DeviceAlarm d WHERE d.deviceID = :deviceID AND d.deviceType = :deviceType")
    List<DeviceAlarm> findByDevice(String deviceID, int deviceType);
}
