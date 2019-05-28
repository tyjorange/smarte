package com.rogy.smarte.fsu;

import com.rogy.smarte.entity.db1.TimeController;
import com.rogy.smarte.service.impl.VirtualFsuServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
public class BreakerTimerExecutor {

    private volatile boolean stopFlag = false;    // 结束标记

    public void stop() {
        stopFlag = true;
    }

    final
    VirtualFsuServiceImpl virtualFsuService;
    /**
     * 一天内每一分钟的定时集合。
     * 每分钟的定时集合为一个ConcurrentHashMa，key为定时记录的ID，value未使用(固定为0)。
     */
    private Object[][] timers = new Object[24][60];

    @Autowired
    public BreakerTimerExecutor(VirtualFsuServiceImpl virtualFsuService) {
        this.virtualFsuService = virtualFsuService;
        // 创建时间轮数组
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 60; j++) {
                timers[i][j] = new AtomicReference<ConcurrentHashMap<Long, Byte>>();
            }
        }

        LocalDateTime now = LocalDateTime.now();
        // 加载系统已有定时。
        int count = loadTimers();
        System.out.printf("[%s] BreakerTimerExecutor - %d Times loaded.\n", now, count);
        // 启动时间轮。
//        int secs = now.getSecond();
//		ScheduledExecutorService scheduledExecutorService = VirtualFsuUtil.scheduledExecutorService;
//        scheduledExecutorService.scheduleAtFixedRate(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.currentThread().setName("PM_BreakerTimerExecutor");
//                            if (!stopFlag && !Thread.currentThread().isInterrupted()) {
//                                LocalTime time = LocalTime.now();
//                                // 获取当前分钟的所有定时的列表。
//                                ConcurrentHashMap<Long, Byte> minuteTimers = getMinuteTimers(time.getHour(), time.getMinute());
//                                if (minuteTimers != null) {
//                                    String ids = minuteTimers.entrySet().stream().map(e -> e.getKey().toString()).collect(Collectors.joining(",", "(", ")"));
//                                    if (ids.length() > 2) {    // "()"表示没有定时记录。
//                                        List<TimeController> tcs = virtualFsuService.findTimeControllerByIDList(ids);
//                                        if (tcs != null && !tcs.isEmpty())
//                                            tcs.forEach(this::doTimer);
//                                    }
//                                }
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                60 - secs,    // 下一分钟开始
//                60,    // 每分钟
//                TimeUnit.SECONDS);
        this.myScheduled();
    }

    @Scheduled(cron = "0 0/1 * * * ? ")
    private void myScheduled() {
        try {
            Thread.currentThread().setName("PM_BreakerTimerExecutor");
            if (!stopFlag && !Thread.currentThread().isInterrupted()) {
                LocalTime time = LocalTime.now();
                // 获取当前分钟的所有定时的列表。
                ConcurrentHashMap<Long, Byte> minuteTimers = getMinuteTimers(time.getHour(), time.getMinute());
                if (minuteTimers != null) {
                    String ids = minuteTimers.entrySet().stream().map(e -> e.getKey().toString()).collect(Collectors.joining(",", "(", ")"));
                    if (ids.length() > 2) {    // "()"表示没有定时记录。
                        List<TimeController> tcs = virtualFsuService.findTimeControllerByIDList(ids);
                        if (tcs != null && !tcs.isEmpty())
                            tcs.forEach(this::doTimer);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载所有的服务端执行的定时控制记录。
     *
     * @return 成功加载的定时控制记录总数。
     */
    private int loadTimers() {
        int count = 0;
        try {
            List<TimeController> timeControllers = virtualFsuService.findAllEnabledTimeController();
            for (TimeController timeController : timeControllers) {
                if (timeController.getState() != 0 &&    // 有效
                        timeController.getUpload() == 0) {    // 服务端执行
                    addTimer(timeController);
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 执行一项定时控制。
     *
     * @param timeController 定时控制对象。
     */
    private void doTimer(TimeController timeController) {
        try {
            LocalDateTime now = LocalDateTime.now();
            if (timeController.getState() == 0) {
                System.out.printf("[%s] BreakerTimerExecutor - doTimer(%s) failed, state == 0.\n",
                        now,
                        timeController.getId());
                return;
            }
            if (timeController.getUpload() != 0) {
                System.out.printf("[%s] BreakerTimerExecutor - doTimer(%s) failed, upload != 0.\n",
                        now,
                        timeController.getId());
                return;
            }
            int weekDay = timeController.getWeekday();
            if ((weekDay == 0) ||
                    (((1 << (now.get(ChronoField.DAY_OF_WEEK) - 1)) & weekDay) > 0)) {
                System.out.printf("[%s] BreakerTimerExecutor - TimeController%s active.\n",
                        now,
                        timeController.toString());
                virtualFsuService.doTimeBreakerController(timeController);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 新增一个定时控制。
     *
     * @param timeController 定时控制记录。
     */
    @SuppressWarnings("unchecked")
    public void addTimer(TimeController timeController) {
        try {
            if (timeController.getState() != 0 &&    // 有效
                    timeController.getUpload() == 0) {    // 服务器执行
                LocalTime time = timeController.getRunTime().toLocalTime();
                int hour = time.getHour();
                int minute = time.getMinute();
                ConcurrentHashMap<Long, Byte> minuteTimers = getMinuteTimers(hour, minute);
                if (minuteTimers == null) {
                    AtomicReference<ConcurrentHashMap<Long, Byte>> ar = (AtomicReference<ConcurrentHashMap<Long, Byte>>) timers[hour][minute];
                    minuteTimers = new ConcurrentHashMap<Long, Byte>();
                    if (!ar.compareAndSet(null, minuteTimers))
                        minuteTimers = ar.get();
                }
                if (minuteTimers.put(timeController.getId(), (byte) 0) == null) {
//					System.out.printf("[%s] BreakerTimerExecutor - TimeController%s added.\n",
//							LocalDateTime.now(),
//							timeController.toString());
                } else {
//					System.out.printf("[%s] BreakerTimerExecutor - TimeController%s replaced.\n",
//							LocalDateTime.now(),
//							timeController.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除一个定时控制。
     *
     * @param timerID 定时控制记录ID。
     * @param hour    定时控制小时。
     * @param minute  定时控制分钟。
     * @return true=指定的定时时间原来存在该ID的定时,并成功删除。false=指定的定时时间原来不存在该ID的定时，或者删除失败。
     */
    public boolean removeTimer(Long timerID, int hour, int minute) {
        try {
            LocalDateTime now = LocalDateTime.now();
            ConcurrentHashMap<Long, Byte> minuteTimers = getMinuteTimers(hour, minute);
            if (minuteTimers != null) {
                if (minuteTimers.remove(timerID, (byte) 0)) {
                    System.out.printf("[%s] BreakerTimerExecutor - TimeController(id=%s, hour=%d, minute=%d) removed.\n",
                            now,
                            timerID,
                            hour,
                            minute);
                    return true;
                } else {
                    System.out.printf("[%s] BreakerTimerExecutor - remove TimeController(id=%s, hour=%d, minute=%d) not found.\n",
                            now,
                            timerID,
                            hour,
                            minute);
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private ConcurrentHashMap<Long, Byte> getMinuteTimers(int hour, int minute) {
        return ((AtomicReference<ConcurrentHashMap<Long, Byte>>) timers[hour][minute]).get();
    }
}
