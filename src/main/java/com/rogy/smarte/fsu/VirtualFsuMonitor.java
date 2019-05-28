package com.rogy.smarte.fsu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 系统任务。
 */
@Component
public class VirtualFsuMonitor {
    private final static Logger logger = LoggerFactory.getLogger(VirtualFsuMonitor.class);

//    private ScheduledExecutorService scheduledExecutorService = VirtualFsuUtil.scheduledExecutorService;

    private int onlineCountRateMins = 5;    // 在线统计频率(分钟)
    private int trafficRateMins = 10;    // 流量统计频率(分钟)
    private int timerUploadDelayMins = 1;    // 集中器定时操作上传结果检测频率(分钟)
    private final CollectorTimeUploadResultRunnable collectorTimeUploadResultRunnable;
    private final CollectorTrafficRunnable collectorTrafficRunnable;
    private final CollectorOnlineCountRunnable collectorOnlineCountRunnable;

    @Autowired
    public VirtualFsuMonitor(CollectorTimeUploadResultRunnable collectorTimeUploadResultRunnable,
                             CollectorTrafficRunnable collectorTrafficRunnable,
                             CollectorOnlineCountRunnable collectorOnlineCountRunnable) {
        this.collectorTimeUploadResultRunnable = collectorTimeUploadResultRunnable;
        this.collectorTrafficRunnable = collectorTrafficRunnable;
        this.collectorOnlineCountRunnable = collectorOnlineCountRunnable;
    }

    public void setOnlineCountRateMins(int onlineCountRateMins) {
        this.onlineCountRateMins = onlineCountRateMins;
    }

    public int getOnlineCountRateMins() {
        return onlineCountRateMins;
    }

    public void setTrafficRateMins(int trafficRateMins) {
        this.trafficRateMins = trafficRateMins;
    }

    public int getTrafficRateMins() {
        return trafficRateMins;
    }

    public int getTimerUploadDelayMins() {
        return timerUploadDelayMins;
    }

    public void setTimerUploadDelayMins(int timerUploadDelayMins) {
        this.timerUploadDelayMins = timerUploadDelayMins;
    }

//    public VirtualFsuMonitor(ScheduledExecutorService scheduledExecutorService) {
//        this.scheduledExecutorService = scheduledExecutorService;
//    }

    @Async
    public void start() {
//        System.out.printf("[%s] VirtualFsuMonitor service start...\n", LocalDateTime.now());
        logger.info(" VirtualFsuMonitor service start...");
        // 集中器在线统计
//        scheduledExecutorService.scheduleAtFixedRate(    // 每N分钟运行一次
//                new CollectorOnlineCountRunnable(),
//                onlineCountRateMins / 2,
//                onlineCountRateMins,
//                TimeUnit.MINUTES);
        collectorOnlineCountRunnable.doSchedule();

        // 集中器流量统计
//        scheduledExecutorService.scheduleAtFixedRate(    // 每N分钟运行一次
//                new CollectorTrafficRunnable(),
//                trafficRateMins / 2,
//                trafficRateMins,
//                TimeUnit.MINUTES);
        collectorTrafficRunnable.doSchedule();

        // 集中器流量统计
//        scheduledExecutorService.scheduleWithFixedDelay(    // 每隔N分钟运行一次
//                new CollectorTimeUploadResultRunnable(),
//                timerUploadDelayMins,
//                timerUploadDelayMins,
//                TimeUnit.MINUTES);
        collectorTimeUploadResultRunnable.doSchedule();
    }

}
