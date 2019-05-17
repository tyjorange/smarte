package com.rogy.smarte.service.impl;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.rogy.smarte.entity.db1.Collector;
import com.rogy.smarte.entity.db1.Switch;
import com.rogy.smarte.entity.db1.TimeController;
import com.rogy.smarte.fsu.VirtualFsuController;
import com.rogy.smarte.repository.db1.SwitchDao;
import com.rogy.smarte.repository.db1.TimeControllerDao;
import com.rogy.smarte.service.ITimeController;

@Service
public class TimeControllerImpl implements ITimeController {

	@PersistenceContext(unitName = "EntityManagerFactoryBean_1")
	private EntityManager entityManager;
    @Resource
    private SwitchDao switchDao;
    @Resource
    private TimeControllerDao timeControllerDao;
    
    @Override
    public TimeController findById(Long id) {
		Optional<TimeController> tco = timeControllerDao.findById(id);
		if(tco.isPresent())
			return tco.get();
		else
			return null;
    }

	@Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public TimeController add(final Switch swt, byte cmdData, Time runTime, byte state, int weekday, byte upload) {
		TimeController timeController = new TimeController();
		timeController.setId(0L);
		timeController.setSwitchs(swt);
		timeController.setCmdData(cmdData);
		timeController.setRunTime(runTime);
		timeController.setState(state);
		timeController.setWeekday(weekday);
		timeController.setUpload(upload);
		timeController = timeControllerDao.saveAndFlush(timeController);
		if(upload != 0) {	// 上传集中器
            // 通知集中器。
			doUpload(swt.getCollector());
		} else {	// 服务端执行
            // 添加对应的服务器定时执行项。
            VirtualFsuController.breakerTimerExecutor.addTimer(timeController);
		}
		return timeController;
	}

	@Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public void deleteByIds(String ids) {
		String[] idArray = ids.split(",");
		if(idArray != null) {
			for(String id : idArray) {
				if(id != null)
					deleteById(Long.valueOf(id));
			}
		}
	}

	@Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public void deleteById(Long id) {
		TimeController timeController = findById(id);
		if(timeController != null) {
			LocalTime oldrunTime = timeController.getRunTime().toLocalTime();
			byte oldupload = timeController.getUpload();
			timeControllerDao.deleteById(id);
			if(oldupload != 0) {	// 上传集中器
	            // 通知集中器。
				doUpload(timeController.getSwitchs().getCollector());
			} else {	// 服务端执行
				// 移除对应的服务器定时执行项
                VirtualFsuController.breakerTimerExecutor.removeTimer(id, oldrunTime.getHour(), oldrunTime.getMinute());
			}
		}
	}

	@Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public TimeController updateById(Long id, Byte cmdData, Time runTime, Byte state, Integer weekday, Byte upload) {
		TimeController timeController = findById(id);
		if(timeController != null) {
			LocalTime oldrunTime = timeController.getRunTime().toLocalTime();
			byte oldupload = timeController.getUpload();
			if(cmdData != null)
				timeController.setCmdData(cmdData);
			if(runTime != null)
				timeController.setRunTime(runTime);
			if(state != null)
				timeController.setState(state);
			if(weekday != null)
				timeController.setWeekday(weekday);
			if(upload != null)
				timeController.setUpload(upload);
			timeController = timeControllerDao.saveAndFlush(timeController);
			if(oldupload != 0 || (upload != null && upload != 0)) {	// 原记录或新记录为上传集中器
	            // 通知集中器。
				doUpload(timeController.getSwitchs().getCollector());
			}
			if(oldupload == 0) {	// 原记录为服务端执行
				// 移除对应的服务器定时执行项
                VirtualFsuController.breakerTimerExecutor.removeTimer(id, oldrunTime.getHour(), oldrunTime.getMinute());
			}
			if(upload != null && upload == 0) {	// 新记录为服务端执行
	            // 添加对应的服务器定时执行项。
	            VirtualFsuController.breakerTimerExecutor.addTimer(timeController);
			}
			return timeController;
		} else {
			return null;
		}
	}
	
	/**
	 * 上传集中器定时操作。
	 * @param collector 集中器对象。
	 */
	private void doUpload(final Collector collector) {
        int result = VirtualFsuController.setCollectorTimer(collector);
        if (result >= 0) {
            System.out.printf("[%s] Notify collector(%s) add timer ok. count=(%d).\n",
                    LocalDateTime.now().toString(), collector.getCode(), result);
        } else {
            System.out.printf("[%s] Notify collector(%s) add timer failed. err=(%d).\n",
                    LocalDateTime.now().toString(), collector.getCode(), result);
        }
	}
	
	@Override
	public List<TimeController> findByIDList(String list) {
		return timeControllerDao.findByIDList(entityManager, list);
	}
}
