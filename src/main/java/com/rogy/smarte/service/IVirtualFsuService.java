package com.rogy.smarte.service;

import com.rogy.smarte.entity.db1.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface IVirtualFsuService {
    UserCollector findUserCollectorByCollectorID(String collectorID);

    List<DeviceAlarm> findByTime(Timestamp time);

    List<SceneSwitch> findSceneSwitchsBySceneID(String sceneID);

    SignalHour findSignalHourBySwitchAndHour(String switchID, Date time, int hour, String signalsTypeID);

    List<SignalsNew> findSignalsNewWithDL();

    List<TimeController> findAllEnabledTimeController();

    List<TimeController> findCollectorTimeController(String collectorID);

    TimeController findTimeControllerByID(String timeControllerID);

    Controller findController(String controllerID);

    Controller findControllerWithClear(String controllerID);

    Switch findSwitchBySwitchID(String switchID);

    Switch findSwitchBySwitchCode(String switchCode);

    List<Switch> findSwitchByCollectorID(String collectorID);

    List<Switch> findSwitchByCollectorIDOrderByCode(String collectorID);

    Signalstype findSignalsTypeBySignalsTypeCode(String signalsTypeCode);

    List<Signalstype> findAllSignalsTypes();

    SignalsNew findSignalsNewBySwitchIDAndSignalsTypeID(String switchID, String signalsTypeID);

    SwitchParam findSwitchParamBySwitchIDParamID(String switchID, Integer paramID);

    void addOrUpdateSwitchParam(SwitchParam switchParam);

    Controller findRun(int runId, int runNo);

    Collector findCollectorByID(String collectorID);

    Collector findCollectorByCode(String collectorCode);

    void updateCollectorActive(String collectorID, int active, LocalDateTime activeTime);

    int clearAllCollectorActive(LocalDateTime activeTime);

    void updateCollectorIp(Collector collector, String ip);

    int updateCollectorFault(Collector collector, int faultState, LocalDateTime faultTime);

    CollectorTimerUpload findTimerUploadByCollectorCodeAndMsg(String code, int msgId, int msgNo);

    CollectorTimerUpload findTimerUploadByCollectorID(String collectorID);

    CollectorRTC findRTCByCollectorCode(String code);

    CollectorRTC findRTCByCollectorID(String collectorID);

    List<CollectorTimerUpload> findNoResultBefore(Timestamp beforeTime);

    CollectorTimerUpload addOrUpdateCollectorTimerUpload(CollectorTimerUpload collectorTimerUpload);

    CollectorRTC addOrUpdateCollectorRTC(CollectorRTC collectorRTC);

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
    CollectorTraffic addOrUpdateCollectorTraffic(CollectorTraffic collectorTraffic);

//    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
//            RuntimeException.class, Exception.class})
//    void addOrUpdateSingalAndNew(Signal signal, Switch switchs, Signalstype signalstype);

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
    void doTimeBreakerController(TimeController timeController);

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
    void doTimeSceneController(TimeController timeController);

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Exception.class})
    Controller addOrUpdateControllerResult(Controller controller);

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Exception.class})
    CollectorOnlineCount addOrUpdateCollectorOnlineCount(CollectorOnlineCount collectorOnlineCount);

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Exception.class})
    CollectorOnline addOrUpdateCollectorOnline(CollectorOnline collectorOnline);

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Exception.class})
    int clearAllCollectorOnline(LocalDateTime activeTime, int reason);

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Exception.class})
    CollectorException incCollectorException(Collector collector);

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
    CollectorException incCollectorException(String collectorID);

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
    void switchFault(Switch swt, int faultCode);
}
