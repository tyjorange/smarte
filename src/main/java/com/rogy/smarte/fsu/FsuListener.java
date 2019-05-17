package com.rogy.smarte.fsu;

import com.rogy.smarte.entity.db1.Signalstype;
import com.rogy.smarte.entity.db1.Switch;
import com.rogy.smarte.service.impl.VirtualFsuServiceImpl;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.time.LocalDateTime;
import java.util.List;

@WebListener
public class FsuListener implements ServletContextListener {

    private VirtualFsuCollector collectorWorker = new VirtualFsuCollector();
    //	private VirtualFsuTimeController timeControllerWorker = new VirtualFsuTimeController();
//	private DeviceAlarmWork alarmWorker = new DeviceAlarmWork();
    private VirtualFsuMonitor monitor = new VirtualFsuMonitor(VirtualFsuUtil.scheduledExecutorService);

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        initResource(arg0);

        // 工作线程
        new Thread(collectorWorker, "PM_CollectorWorker").start();
//		new Thread(timeControllerWorker, "PM_TimeControllerWorker").start();
//		new Thread(alarmWorker).start();
        new Thread(VirtualFsuController.collectorCommandExecutor, "PM_CommandExecutor").start();

        monitor.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        LocalDateTime now = LocalDateTime.now();

        // 获取virtualFsuService资源对象
        VirtualFsuUtil.virtualFsuService = WebApplicationContextUtils
                .getWebApplicationContext(arg0.getServletContext()).getBean(VirtualFsuServiceImpl.class);

        try {
            collectorWorker.stop();
//			timeControllerWorker.stop();
//			alarmWorker.stop();
            VirtualFsuController.collectorCommandExecutor.stop();

            VirtualFsuUtil.shutdownScheduledExcutorService();
            VirtualFsuUtil.shutdownExcutorService();
        } catch (Exception e) {
        }

		try {
			// 把所有集中器设置为离线状态
			VirtualFsuUtil.virtualFsuService.clearAllCollectorActive(now);
			// 添加所有集中器的离线记录
			VirtualFsuUtil.virtualFsuService.clearAllCollectorOnline(now, -1);
		} catch(Exception e) {
			//e.printStackTrace();
		}
	}

	private void initResource(ServletContextEvent arg0) {
		LocalDateTime now = LocalDateTime.now();

		// 获取virtualFsuService资源对象
		VirtualFsuUtil.virtualFsuService = WebApplicationContextUtils
				.getWebApplicationContext(arg0.getServletContext()).getBean(
						VirtualFsuServiceImpl.class);

		// 读取SIGNALTYPE
		try {
			List<Signalstype> types = VirtualFsuUtil.virtualFsuService.findAllSignalsTypes();
			for(Signalstype st : types) {
				VirtualFsuUtil.SIGNALTYPES[st.getSignalsTypeID()] = st;
			}
			System.out.printf("%d SIGNALTYPES Loaded.\n", types.size());
		} catch(Exception e) {
			e.printStackTrace();
		}

		// 读取SWITCH
		try {
			List<Switch> switchs = VirtualFsuUtil.virtualFsuService.findAllSwitch();
			SwitchCache sc;
			for(Switch swt : switchs) {
				sc = SwitchCache.newSwitchCacheFromSwitch(swt);
				VirtualFsuUtil.SWITCHCACHE.put(swt.getCode(), sc);
			}
			System.out.printf("%d SWITCHS Loaded.\n", switchs.size());
		} catch(Exception e) {
			e.printStackTrace();
		}

        try {
            // 把所有集中器设置为离线状态
            VirtualFsuUtil.virtualFsuService.clearAllCollectorActive(now);
            // 添加所有集中器的离线记录
            VirtualFsuUtil.virtualFsuService.clearAllCollectorOnline(now, -2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
