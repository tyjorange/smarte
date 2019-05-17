package com.rogy.smarte.service.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.rogy.smarte.entity.db1.*;
import com.rogy.smarte.fsu.VirtualFsuController;
import com.rogy.smarte.fsu.VirtualFsuUtil;
import com.rogy.smarte.fsu.ZDataValue;
import com.rogy.smarte.repository.db1.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.rogy.smarte.repository.db1.ZDataDao.TIMEFORMAT;


@Service
public class VirtualFsuServiceImpl {

    @Resource
    private CollectorDao collectorDao;
    @Resource
    private CollectorOnlineDao collectorOnlineDao;
    @Resource
    private CollectorOnlineCountDao collectorOnlineCountDao;
    @Resource
    private CollectorExceptionDao collectorExceptionDao;
    @Resource
    private CollectorTimerUploadDao collectorTimerUploadDao;
    @Resource
    private CollectorTrafficDao collectorTrafficDao;
    @Resource
    private CollectorRTCDao collectorRTCDao;
    @Resource
    private ControllerDao controllerDao;
    //	@Resource
//	private SignalDao signalDao;
    @Resource
    private ZDataDao zDataDao;
    @Resource
    private SignalsNewDao signalsNewDao;
    @Resource
    private SwitchDao switchDao;
    @Resource
    private SwitchParamDao switchParamDao;
    @Resource
    private TimeControllerDao timeControllerDao;
    @Resource
    private TimeControllerImpl timeControllerImpl;
    @Resource
    private SignalsTypeDao signalsTypeDao;
    @Resource
    private SignalHourDao signalHourDao;
    @Resource
    private ApexDayDao apexDayDao;
    @Resource
    private ApexMonthDao apexMonthDao;
    @Resource
    private SceneSwitchDao sceneSwitchDao;
    @Resource
    private DeviceAlarmDao deviceAlarmDao;
    @Resource
    private UserCollectorDao userCollectorDao;

    public UserCollector findUserCollectorByCollectorID(Integer collectorID) throws Exception {
    	List<UserCollector> ucs = userCollectorDao.findByCollectorID(collectorID);
    	if(ucs.isEmpty())
    		return null;
    	else
    		return ucs.get(0);
    }

    public List<DeviceAlarm> findByTime(Timestamp time) throws Exception {
        return deviceAlarmDao.findByTime(time);
    }

    public List<SceneSwitch> findSceneSwitchsBySceneID(Integer sceneID) throws Exception {
        return sceneSwitchDao.findBySceneID(sceneID);
    }

    public SignalHour findSignalHourBySwitchAndTypeAndHour(Integer switchID, Date time,
                                                           int hour, Short signalsTypeID) throws Exception {
    	List<SignalHour> shs = signalHourDao.findBySwitchAndTypeAndHour(switchID, signalsTypeID, time, hour);
    	if(shs.isEmpty())
    		return null;
    	else
    		return shs.get(0);
    }

    public int addOrUpdateSignalHour(Integer switchID, Short signalsTypeID, LocalDate date, int hour, double statistik, double value)
            throws Exception {
        return signalHourDao.addOrUpdateSignalHour(switchID, signalsTypeID, date, hour, statistik, value);
    }

    public SignalHour findLatestSignalHourBySwitchAndType(Integer switchID, Short signalsTypeID) throws Exception {
        return signalHourDao.findLatestBySwitchAndType(switchID, signalsTypeID);
    }

    public List<TimeController> findAllEnabledTimeController() throws Exception {
        return timeControllerDao.findAllEnabled();
    }

    public List<TimeController> findCollectorTimeController(Integer collectorID) throws Exception {
        return timeControllerDao.findByCollectorID(collectorID);
    }

    public TimeController findTimeControllerByID(Long timeControllerID) throws Exception {
    	List<TimeController> tcs = timeControllerDao.findByTimeControllerID(timeControllerID);
    	if(tcs.isEmpty())
    		return null;
    	else
    		return tcs.get(0);
    }
    
    public List<TimeController> findTimeControllerByIDList(String list) throws Exception {
    	return timeControllerImpl.findByIDList(list);
    }

    public Controller findController(Long controllerID) throws Exception {
    	List<Controller> cs = controllerDao.findByControllerID(controllerID);
    	if(cs.isEmpty())
    		return null;
    	else
    		return cs.get(0);
    }

    public Controller findControllerWithClear(Long controllerID) throws Exception {
    	List<Controller> cs = controllerDao.findByControllerIDWithClear(controllerID);
    	if(cs.isEmpty())
    		return null;
    	else
    		return cs.get(0);
    }

    public List<Switch> findAllSwitch() throws Exception {
        return switchDao.findAll();
    }

    public Switch findSwitchBySwitchID(Integer switchID) throws Exception {
    	List<Switch> ss = switchDao.findBySwitchID(switchID);
    	if(ss.isEmpty())
    		return null;
    	else
    		return ss.get(0);
    }

    public Switch findSwitchBySwitchCode(String switchCode) throws Exception {
    	List<Switch> ss = switchDao.findByCode(switchCode);
    	if(ss.isEmpty())
    		return null;
    	else
    		return ss.get(0);
    }

    public List<Switch> findSwitchByCollectorID(Integer collectorID)
            throws Exception {
        return switchDao.findByCollectorID(collectorID);
    }

    public List<Switch> findSwitchByCollectorIDOrderByCode(Integer collectorID)
            throws Exception {
        return switchDao.findByCollectorIDOrderByCode(collectorID);
    }

//    public Signalstype findSignalsTypeBySignalsTypeCode(String signalsTypeCode)
//            throws Exception {
//    	List<Signalstype> sts = signalsTypeDao.findByCode(signalsTypeCode);
//    	if(sts.isEmpty())
//    		return null;
//    	else
//    		return sts.get(0);
//    }

    public List<Signalstype> findAllSignalsTypes() throws Exception {
        return signalsTypeDao.findAll();
    }

    public SignalsNew findSignalsNewBySwitchIDAndSignalsTypeID(Integer switchID,
                                                               Short signalsTypeID) throws Exception {
    	List<SignalsNew> sns = signalsNewDao.findBySwitchIDAndSignalsTypeID(switchID,signalsTypeID);
    	if(sns.isEmpty())
    		return null;
    	else
    		return sns.get(0);
    }

    public SwitchParam findSwitchParamBySwitchIDParamID(Integer switchID,
                                                        Integer paramID) throws Exception {
    	List<SwitchParam> sps = switchParamDao.findBySwitchIDParamID(switchID, paramID);
    	if(sps.isEmpty())
    		return null;
    	else
    		return sps.get(0);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
    public void addOrUpdateSwitchParam(SwitchParam switchParam)
            throws Exception {
        switchParamDao.saveAndFlush(switchParam);
    }

    public Controller findRun(int runId, int runNo) throws Exception {
    	List<Controller> cs = controllerDao.findRun(runId, runNo);
    	if (cs == null || cs.isEmpty())
    		return null;
        return cs.get(0);
    }

    public Collector findCollectorByID(Integer collectorID) throws Exception {
    	List<Collector> cs = collectorDao.findByCollectorID(collectorID);
    	if(cs.isEmpty())
    		return null;
    	else
    		return cs.get(0);
    }

    public Collector findCollectorByCode(String collectorCode) throws Exception {
    	List<Collector> cs = collectorDao.findByCollectorCode(collectorCode);
    	if(cs.isEmpty())
    		return null;
    	else
    		return cs.get(0);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
    public void updateCollectorActive(Integer collectorID, int active, LocalDateTime activeTime) throws Exception {
        collectorDao.updateActiveByCollectorID(collectorID, active, activeTime);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
    public int clearAllCollectorActive(LocalDateTime activeTime) throws Exception {
        return collectorDao.clearAllActive(activeTime);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
    public void updateCollectorIp(Collector collector, String ip) throws Exception {
        collectorDao.updateIpByCollectorID(collector.getCollectorID(), ip);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
    public int updateCollectorFault(Collector collector, int faultState, LocalDateTime faultTime) throws Exception {
        return collectorDao.updateFaultByCollectorID(collector.getCollectorID(), faultState, faultTime);
    }

    public CollectorTimerUpload findTimerUploadByCollectorCodeAndMsg(String code, int msgId, int msgNo) throws Exception {
    	List<CollectorTimerUpload> ctus = collectorTimerUploadDao.findByCollectorCodeAndMsg(code, msgId, msgNo);
    	if(ctus.isEmpty())
    		return null;
    	else
    		return ctus.get(0);
    }

    public CollectorTimerUpload findTimerUploadByCollectorID(Integer collectorID) throws Exception {
    	List<CollectorTimerUpload> ctus = collectorTimerUploadDao.findByCollectorID(collectorID);
    	if(ctus.isEmpty())
    		return null;
    	else
    		return ctus.get(0);
    }

    public CollectorRTC findRTCByCollectorCode(String code) throws Exception {
    	List<CollectorRTC> crtcs = collectorRTCDao.findByCollectorCode(code);
    	if(crtcs.isEmpty())
    		return null;
    	else
    		return crtcs.get(0);
    }

    public CollectorRTC findRTCByCollectorID(Integer collectorID) throws Exception {
    	List<CollectorRTC> crtcs = collectorRTCDao.findByCollectorID(collectorID);
    	if(crtcs.isEmpty())
    		return null;
    	else
    		return crtcs.get(0);
    }

    public List<CollectorTimerUpload> findNoResultBefore(Timestamp beforeTime) throws Exception {
        return collectorTimerUploadDao.findNoResultBefore(beforeTime);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
    public CollectorTimerUpload addOrUpdateCollectorTimerUpload(CollectorTimerUpload collectorTimerUpload) throws Exception {
        return collectorTimerUploadDao.saveAndFlush(collectorTimerUpload);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
    public CollectorRTC addOrUpdateCollectorRTC(CollectorRTC collectorRTC) throws Exception {
        return collectorRTCDao.saveAndFlush(collectorRTC);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
    public CollectorTraffic addOrUpdateCollectorTraffic(CollectorTraffic collectorTraffic) throws Exception {
        return collectorTrafficDao.saveAndFlush(collectorTraffic);
    }

    /**
     * 计算数据，并更新对应的SignalNew记录，更新apex记录
     *
     * @param dt            数据时间
     * @param value         数据值
     * @param switchID      对应的Switch对象
     * @param signalsTypeID 对应的Signalstype对象
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
    public boolean newDataAndCalc(final LocalDateTime dt, double value, Integer switchID, Short signalsTypeID, ZDataValue zDataValue) throws Exception {
        Switch switchs = new Switch();
        switchs.setSwitchID(switchID);
        Signalstype signalstype = new Signalstype();
        signalstype.setSignalsTypeID(signalsTypeID);
        LocalDate dtLocaldate = dt.toLocalDate();
        Date dtDate = Date.valueOf(dtLocaldate);

        // 最新采集值。
        SignalsNew sn;
        if (zDataValue.newId <= 0) {    // 该断路器的该SignalType的最新值数据在Cache中不存在。
            // 从数据库中加载。
            sn = findSignalsNewBySwitchIDAndSignalsTypeID(switchID, signalsTypeID);
            if (sn != null) {    // 存在上一次记录。
                zDataValue.newId = sn.getId();
                zDataValue.newTimeMs = sn.getTime().getTime();
                zDataValue.newValue = sn.getValue();
            }
        }
        LocalDateTime newTime = null;
        if (zDataValue.newId > 0) {    // 从毫秒值得到对象。
            newTime = new Timestamp(zDataValue.newTimeMs).toLocalDateTime();
        }
        if (zDataValue.newId > 0 &&    // 存在上一记录。
                dt.isBefore(newTime)) {    // 新数据的时间不在上次数据之后。
            System.out.printf("[%s] Breaker(id=%d)'s Data(typeid=%d) time error. current(%s), last(%s)\n",
                    LocalDateTime.now(),
                    switchID,
                    signalsTypeID,
                    dt,
                    newTime);
            return false;
        }

        // 根据最新累计值计算分段值。
        //SignalHour sh;
        //Date day;
        //int hour;
        if (zDataValue.newId > 0 &&    // 存在前一条数值。
                value > 0 && // 有累计值。
                (signalsTypeID == 10 || signalsTypeID == 8) &&    // 有功电量、无功电量。
                dt.isAfter(newTime)) {    // 本次时间比前一时间要后。
            // 首先根据2次signalNew的差值，来统计这个期间所经过的每个小时的电量数值。
            Map<LocalDateTime, Double> dlGroupByHour = VirtualFsuUtil.calcGroupByHour(newTime, zDataValue.newValue, dt, value);
            if (dlGroupByHour != null && !dlGroupByHour.isEmpty()) {
                double currentValue = zDataValue.newValue;
                double statistik;
                for (Map.Entry<LocalDateTime, Double> entry : dlGroupByHour.entrySet()) {
                    //day = Date.valueOf(entry.getKey().toLocalDate());
                    //hour = entry.getKey().getHour();
                    statistik = entry.getValue();
                    currentValue += statistik;
                    VirtualFsuUtil.virtualFsuService.addOrUpdateSignalHour(
                            switchID, signalsTypeID,
                            entry.getKey().toLocalDate(), entry.getKey().getHour(),
                            statistik, currentValue);
//					sh = VirtualFsuUtil.virtualFsuService
//							.findSignalHourBySwitchAndTypeAndHour(
//									switchID, day, hour,
//									signalsTypeID);
//					if (sh == null) {
//						// 这个小时的首次统计值
//						sh = new SignalHour();
//						sh.setId(0L);
//						sh.setSwitchs(switchs);
//						sh.setSignalsType(signalstype);
//						sh.setTime(day);
//						sh.setHour(hour);
//						sh.setValue(value);
//						sh.setStatistik(entry.getValue());
//					} else {
//						// 累加到这个小时的原有统计值中去
//						sh.setValue(value);
//						sh.setStatistik(entry.getValue() + sh.getStatistik());
//					}
//					sh = signalHourDao.addOrUpdateSignalHour(sh);
                }
            }
        }

        // 变动范围。
        if (value > 0 ||    // 变动范围只记录0以上值
//				signalstype.getSignalsTypeID().equals("wd")) {	// 但温度可以有负值
                signalsTypeID == 7) {    // 但温度可以有负值
            // 更新每一天的最大最小值
            ApexDay ad;
            if (zDataValue.dayId <= 0) {    // 该断路器的该SignalType的日最大最小数据在Cache中不存在。
                // 查找该日的记录。
            	List<ApexDay> ads = apexDayDao.findBySwitchAndSignalsTypeAndTime(switchID, signalsTypeID, dtDate);
            	if(!ads.isEmpty()) {
	                ad = ads.get(0);
                    zDataValue.dayId = ad.getId();
                    zDataValue.dayTimeMs = ad.getTime().getTime();
                    zDataValue.dayMax = ad.getMaxDay();
                    zDataValue.dayMin = ad.getMinDay();
            	}
            }
            LocalDate dayTime = null;
            if (zDataValue.dayId > 0) {    // 从毫秒值得到对象。
                dayTime = new Timestamp(zDataValue.dayTimeMs).toLocalDateTime().toLocalDate();
            }
            if (zDataValue.dayId <= 0 ||    // Cache数据不存在。
                    !dtLocaldate.isEqual(dayTime)) {    // Cache数据不是这一天的。
                // 生成这一天的新记录。
                //apexDayDao.addApexDay(switchID, signalsTypeID, localdate, value, value);
                ad = new ApexDay();
                ad.setId(0L);
                ad.setSignalsType(signalstype);
                ad.setSwitchs(switchs);
                ad.setTime(dtDate);
                ad.setMaxDay(value);
                ad.setMinDay(value);
                ad = apexDayDao.saveAndFlush(ad);
                // 新记录进行Cache。
                zDataValue.dayId = ad.getId();
                zDataValue.dayTimeMs = dtDate.getTime();
                zDataValue.dayMax = zDataValue.dayMin = value;
            } else {    // Cache数据就是这一天的。
                if (value > zDataValue.dayMax) {
                    zDataValue.dayMax = value;
                    if (apexDayDao.updateApexDay(zDataValue.dayId, switchID, signalsTypeID, value, zDataValue.dayMin) != 1) {
                        System.out.printf("[%s] Breaker(id=%d)'s Data(typeid=%d) update ApexDay(id=%d)Max failed.\n",
                                LocalDateTime.now(),
                                switchID,
                                signalsTypeID,
                                zDataValue.dayId);
                    }
                } else if (value < zDataValue.dayMin) {
                    zDataValue.dayMin = value;
                    if (apexDayDao.updateApexDay(zDataValue.dayId, switchID, signalsTypeID, zDataValue.dayMax, value) != 1) {
                        System.out.printf("[%s] Breaker(id=%d)'s Data(typeid=%d) update ApexDay(id=%d)Min failed.\n",
                                LocalDateTime.now(),
                                switchID,
                                signalsTypeID,
                                zDataValue.dayId);
                    }
                }
            }
            // 更新每个月的最大最小值
            int year = dt.getYear();
            int month = dt.getMonthValue();
            ApexMonth am;
            if (zDataValue.monthId <= 0) {    // 该断路器的该SignalType的月最大最小数据在Cache中不存在。
                // 查找该月的记录。
            	List<ApexMonth> ams = apexMonthDao.findBySwitchAndSignalsTypeAndTime(switchID, signalsTypeID, year, month);
                if (!ams.isEmpty()) {
                    am = ams.get(0);
                    zDataValue.monthId = am.getId();
                    zDataValue.monthYear = year;
                    zDataValue.monthMonth = month;
                    zDataValue.monthMax = am.getMaxMonth();
                    zDataValue.monthMin = am.getMinMonth();
                }
            }
            if (zDataValue.monthId <= 0 ||    // Cache数据不存在。
                    year != zDataValue.monthYear || month != zDataValue.monthMonth) {    // Cache数据不是这一月的。
                // 生成这一月的新记录。
                //apexMonthDao.addApexMonth(switchID, signalsTypeID, year, month, value, value);
                am = new ApexMonth();
                am.setId(0L);
                am.setMaxMonth(value);
                am.setMinMonth(value);
                am.setSignalsType(signalstype);
                am.setSwitchs(switchs);
                am.setTimeMonth(month);
                am.setTimeYear(year);
                am = apexMonthDao.saveAndFlush(am);
                // 新记录进行Cache。
                zDataValue.monthId = am.getId();
                zDataValue.monthYear = year;
                zDataValue.monthMonth = month;
                zDataValue.monthMax = zDataValue.monthMin = value;
            } else {    // Cache数据就是这一月的。
                if (value > zDataValue.monthMax) {
                    zDataValue.monthMax = value;
                    if (apexMonthDao.updateApexMonth(zDataValue.monthId, switchID, signalsTypeID, value, zDataValue.monthMin) != 1) {
                        System.out.printf("[%s] Breaker(id=%d)'s Data(typeid=%d) update ApexMonth(id=%d)Max failed.\n",
                                LocalDateTime.now(),
                                switchID,
                                signalsTypeID,
                                zDataValue.monthId);
                    }
                } else if (value < zDataValue.monthMin) {
                    zDataValue.monthMin = value;
                    if (apexMonthDao.updateApexMonth(zDataValue.monthId, switchID, signalsTypeID, zDataValue.monthMax, value) != 1) {
                        System.out.printf("[%s] Breaker(id=%d)'s Data(typeid=%d) update ApexMonth(id=%d)Min failed.\n",
                                LocalDateTime.now(),
                                switchID,
                                signalsTypeID,
                                zDataValue.monthId);
                    }
                }
            }
        }

        // 保存新记录。
        if (zDataValue.newId <= 0) {
            // 保存新记录
//			if(signalsNewDao.addSignalsNew(switchID, dt, signalsTypeID, value) != 1) {
//				System.out.printf("[%s] Breaker(id=%d)'s Data(typeid=%d) insert SignalsNew failed.\n",
//						LocalDateTime.now(),
//						switchID,
//						signalsTypeID);
//			}
            sn = new SignalsNew();
            sn.setId(0L);
            sn.setSwitchs(switchs);
            sn.setSignalstype(signalstype);
            sn.setTime(Timestamp.valueOf(dt));
            sn.setValue(value);
            sn = signalsNewDao.saveAndFlush(sn);
            // 新记录进行Cache。
            zDataValue.newId = sn.getId();
            zDataValue.newTimeMs = sn.getTime().getTime();
            zDataValue.newValue = sn.getValue();
        } else {
            if (signalsNewDao.updateSignalsNew(zDataValue.newId, switchID, signalsTypeID, dt, value) != 1) {
                System.out.printf("[%s] Breaker(id=%d)'s Data(typeid=%d) update SignalsNew(id=%d) failed.\n",
                        LocalDateTime.now(),
                        switchID,
                        signalsTypeID,
                        zDataValue.newId);
            }
            zDataValue.newTimeMs = Timestamp.valueOf(dt).getTime();
            zDataValue.newValue = value;
        }

        return true;
    }

//    /**
//     * 保存数据记录。
//     *
//     * @param dt            数据时间
//     * @param switchID      switchID
//     * @param signalsTypeID signalsTypeID
//     * @param value         数据值
//     * @throws Exception
//     */
//    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Exception.class})
//    public void newDataSave(final LocalDateTime dt, Integer switchID, Short signalsTypeID, double value) throws Exception {
//        zDataDao.addData(dt, switchID, signalsTypeID, value);
//    }


    @PersistenceContext(unitName = "EntityManagerFactoryBean_1")
    private EntityManager entityManager;

    /**
     * 保存多个数据记录。
     *
     * @param dt             数据时间
     * @param switchID       switchID
     * @param signalsTypeIDs 各signalsTypeID
     * @param values         各对应的数据值
     * @param count          数据个数
     * @throws Exception
     */
    @Transactional(transactionManager = "TransactionManager_1")
    public void newDataSave(final LocalDateTime dt, int switchID, short[] signalsTypeIDs, double[] values, int count) throws Exception {
//        String tableName = ZDataDao.getZDataTableName(dt);
//        for (int a = 0; a <= count; a++) {
        zDataDao.addData(entityManager, dt, switchID, signalsTypeIDs, values, count);
//        }
    }

    /**
     * 根据定时控制对象生成对应的集中器控制命令记录。
     *
     * @param timeController 定时控制对象。
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
    public void doTimeBreakerController(TimeController timeController) throws Exception {
        LocalTime time = timeController.getRunTime().toLocalTime();
        int hour = time.getHour();
        int minute = time.getMinute();
        // 发起控制
        Controller controller = VirtualFsuController.switchControl(timeController.getSwitchs(), timeController.getCmdData(), (byte) 1);
        if (controller != null) {    // 控制发送成功
            if (timeController.getWeekday() == 0) { // 非重复
                timeController.setState((byte) 0);
                timeControllerDao.saveAndFlush(timeController);
                // 停止服务器定时执行项。
                VirtualFsuController.breakerTimerExecutor.removeTimer(timeController.getId(), hour, minute);
            }
        } else {    // 控制发送失败
            System.out.printf("[%s] doTimeBreakerController(%s) failed.",
                    LocalDateTime.now(),
                    timeController.getId());
        }
    }

    /**
     * 根据定时控制对象生成对应的场景执行命令记录。
     *
     * @param timeController 定时控制对象。
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
    public void doTimeSceneController(TimeController timeController) throws Exception {
    }

    /**
     * 修改控制记录的执行结果。
     *
     * @param controller 包含执行结果的控制对象。
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
    public Controller addOrUpdateControllerResult(Controller controller)
            throws Exception {
        return controllerDao.saveAndFlush(controller);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
    public CollectorOnlineCount addOrUpdateCollectorOnlineCount(CollectorOnlineCount collectorOnlineCount) throws Exception {
        return collectorOnlineCountDao.saveAndFlush(collectorOnlineCount);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
    public CollectorOnline addOrUpdateCollectorOnline(CollectorOnline collectorOnline) throws Exception {
        return collectorOnlineDao.saveAndFlush(collectorOnline);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
    public int clearAllCollectorOnline(LocalDateTime activeTime, int reason) throws Exception {
        return collectorOnlineDao.clearAllOnline(activeTime, reason);
    }

    /**
     * 增加集中器Exception计数。
     *
     * @param collector 集中器对象。
     * @return CollectorException对象。
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
    public CollectorException incCollectorException(Collector collector) throws Exception {
        Date dateNow = Date.valueOf(LocalDate.now());
        List<CollectorException> ces = collectorExceptionDao.findByCollectorIDAndDate(collector.getCollectorID(), dateNow);
        CollectorException collectorException;
        if (ces.isEmpty()) {
            collectorException = new CollectorException();
            collectorException.setId(0L);
            collectorException.setCollector(collector);
            collectorException.setExcpdate(dateNow);
            collectorException.setExcpcount(1);
        } else {
        	collectorException = ces.get(0);
            collectorException.setExcpcount(collectorException.getExcpcount() + 1);
        }
        return collectorExceptionDao.saveAndFlush(collectorException);
    }

    /**
     * 增加集中器Exception计数。
     *
     * @param collectorID 集中器ID。
     * @return CollectorException对象。
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
    public CollectorException incCollectorException(Integer collectorID) throws Exception {
        Date dateNow = Date.valueOf(LocalDate.now());
        List<CollectorException> ces = collectorExceptionDao.findByCollectorIDAndDate(collectorID, dateNow);
        CollectorException collectorException;
        if (ces.isEmpty()) {
            collectorException = new CollectorException();
            collectorException.setId(0L);
            Collector collector = new Collector();
            collector.setCollectorID(collectorID);
            collectorException.setCollector(collector);
            collectorException.setExcpdate(dateNow);
            collectorException.setExcpcount(1);
        } else {
        	collectorException = ces.get(0);
            collectorException.setExcpcount(collectorException.getExcpcount() + 1);
        }
        return collectorExceptionDao.saveAndFlush(collectorException);
    }

    /**
     * 设置断路器错误状态。
     *
     * @param swt       断路器。
     * @param faultCode 错误状态。
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
    public void switchFault(Switch swt, int faultCode)
            throws Exception {
        swt.setFaultTime(Timestamp.valueOf(LocalDateTime.now()));
        swt.setFaultState(2);
        swt.setFault(faultCode);
        if (faultCode == 0 ||
                (faultCode & 0b100000000) > 0) { // 第8位:手动合闸
//			System.out.println("setState=1");
            swt.setState(1);    // 线路合上状态
        } else {
//			System.out.println("setState=0");
            swt.setState(0);    // 线路断开状态
        }
        // 设备告警检测：
        // 除 远程拉闸(认为是APP操作) 其它情况都产生设备告警
        if ((faultCode & 0b1111011111) > 0) {
            DeviceAlarm deviceAlarm = new DeviceAlarm();
            deviceAlarm.setId(0L);
            deviceAlarm.setDeviceID(swt.getSwitchID());
            deviceAlarm.setDeviceType(2);    // 断路器
            deviceAlarm.setTime(Timestamp.valueOf(LocalDateTime.now()));
            deviceAlarm.setCode(faultCode);
            deviceAlarmDao.saveAndFlush(deviceAlarm);
        }
        switchDao.saveAndFlush(swt);
    }

}
