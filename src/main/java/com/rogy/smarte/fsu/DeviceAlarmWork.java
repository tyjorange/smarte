package com.rogy.smarte.fsu;


import com.rogy.smarte.entity.db1.Collector;
import com.rogy.smarte.entity.db1.DeviceAlarm;
import com.rogy.smarte.entity.db1.Switch;
import com.rogy.smarte.entity.db1.UserCollector;
import com.rogy.smarte.util.PushMessage;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 集中器采集服务类。
 */
public class DeviceAlarmWork implements Runnable {

    private volatile boolean stopFlag = false; // 结束标记

    public void stop() {
        stopFlag = true;
    }

    @Override
    public void run() {
        System.out.printf("[%s] Device Alarm start...\n", LocalDateTime.now());
        // 稍微等待
        try {
            Thread.sleep(5 * 1000);
        } catch (InterruptedException ie) {
        }
        List<DeviceAlarm> alarms;
        LocalDateTime newTime = LocalDateTime.now();
        while (!stopFlag && !Thread.currentThread().isInterrupted()) {
            try {
                System.out.println(newTime);
                alarms = VirtualFsuUtil.virtualFsuService.findByTime(Timestamp
                        .valueOf(newTime));
                if (alarms != null && !alarms.isEmpty()) {
                    newTime = alarms.get(0).getTime().toLocalDateTime();
                    String tag;
                    for (DeviceAlarm da : alarms) {
                        if (1 == da.getDeviceType()) { // 集中器告警
                            Collector c = VirtualFsuUtil.virtualFsuService
                                    .findCollectorByID(Integer.valueOf(da.getDeviceID()));
                            if (c == null)
                                continue;
                            UserCollector uc = VirtualFsuUtil.virtualFsuService
                                    .findUserCollectorByCollectorID(c.getCollectorID());
                            if (uc == null)
                                continue;
                            tag = uc.getUser().getUsername();
                        } else if (2 == da.getDeviceType()) { // 断路器告警
                            Switch s = VirtualFsuUtil.virtualFsuService.findSwitchBySwitchID(Integer.valueOf(da.getDeviceID()));
                            if (s == null)
                                continue;
                            UserCollector uc = VirtualFsuUtil.virtualFsuService.findUserCollectorByCollectorID(s.getCollector().getCollectorID());
                            if (uc == null)
                                continue;
                            tag = uc.getUser().getUsername();
                        } else {
                            continue;
                        }
                        System.out.println(tag);
                        PushMessage.buildPushByTag(tag, "asdf", "0");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (!stopFlag)
                    Thread.sleep(1 * 1000);
            } catch (InterruptedException ie) {
            }
        }
        System.out
                .printf("[%s] Controller service stop\n", LocalDateTime.now());
    }

}
