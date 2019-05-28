package com.rogy.smarte.fsu;

import com.rogy.smarte.entity.db1.Signalstype;
import com.rogy.smarte.entity.db1.Switch;
import com.rogy.smarte.service.impl.VirtualFsuServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class FsuListener {
    private final VirtualFsuServiceImpl virtualFsuService;
    private final CollectorCommandExecutor collectorCommandExecutor;
    private final VirtualFsuCollector collectorWorker;
    //	private VirtualFsuTimeController timeControllerWorker = new VirtualFsuTimeController();
    //	private DeviceAlarmWork alarmWorker = new DeviceAlarmWork();
    private final VirtualFsuMonitor monitor;

    @Autowired
    public FsuListener(VirtualFsuServiceImpl virtualFsuService,
                       CollectorCommandExecutor collectorCommandExecutor,
                       VirtualFsuCollector collectorWorker,
                       VirtualFsuMonitor monitor) {
        this.virtualFsuService = virtualFsuService;
        this.collectorCommandExecutor = collectorCommandExecutor;
        this.collectorWorker = collectorWorker;
        this.monitor = monitor;
    }

    public void contextInitialized() {
        initResource();

        // 工作线程
//      new Thread(collectorWorker, "PM_CollectorWorker").start();
        collectorWorker.asyncRun("PM_CollectorWorker");
//		new Thread(timeControllerWorker, "PM_TimeControllerWorker").start();
//		new Thread(alarmWorker).start();
//      new Thread(VirtualFsuController.collectorCommandExecutor, "PM_CommandExecutor").start();
        collectorCommandExecutor.asyncRun("PM_CommandExecutor");
        monitor.start();
    }

    public void contextDestroyed() {
        LocalDateTime now = LocalDateTime.now();

        // 获取virtualFsuService资源对象
//        VirtualFsuUtil.virtualFsuService = WebApplicationContextUtils
//                .getWebApplicationContext(arg0.getServletContext()).getBean(VirtualFsuServiceImpl.class);

        try {
            collectorWorker.stop();
//			timeControllerWorker.stop();
//			alarmWorker.stop();
            collectorCommandExecutor.stop();

//            VirtualFsuUtil.shutdownScheduledExcutorService();
//            VirtualFsuUtil.shutdownExcutorService();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // 把所有集中器设置为离线状态
            virtualFsuService.clearAllCollectorActive(now);
            // 添加所有集中器的离线记录
            virtualFsuService.clearAllCollectorOnline(now, -1);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void initResource() {
        LocalDateTime now = LocalDateTime.now();

//        // 获取virtualFsuService资源对象
//        VirtualFsuUtil.virtualFsuService = WebApplicationContextUtils
//                .getWebApplicationContext(arg0.getServletContext()).getBean(
//                        VirtualFsuServiceImpl.class);

        // 读取SIGNALTYPE
        try {
            List<Signalstype> types = virtualFsuService.findAllSignalsTypes();
            for (Signalstype st : types) {
                VirtualFsuUtil.SIGNALTYPES[st.getSignalsTypeID()] = st;
            }
            System.out.printf("%d SIGNALTYPES Loaded.\n", types.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 读取SWITCH
        try {
            List<Switch> switchs = virtualFsuService.findAllSwitch();
            SwitchCache sc;
            for (Switch swt : switchs) {
                sc = SwitchCache.newSwitchCacheFromSwitch(swt);
                VirtualFsuUtil.SWITCHCACHE.put(swt.getCode(), sc);
            }
            System.out.printf("%d SWITCHS Loaded.\n", switchs.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // 把所有集中器设置为离线状态
            virtualFsuService.clearAllCollectorActive(now);
            // 添加所有集中器的离线记录
            virtualFsuService.clearAllCollectorOnline(now, -2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
