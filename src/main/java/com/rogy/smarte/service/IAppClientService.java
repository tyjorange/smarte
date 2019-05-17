package com.rogy.smarte.service;

import com.rogy.smarte.controller.app.BaseWS;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public interface IAppClientService {
    DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    String checkUserkey(String userkey) throws Exception;

    JSONArray findSwitchByUser(String userID) throws Exception;

    BaseWS.WSCode bindingWithCollector(String userkey, String collectorCode) throws Exception;

    BaseWS.WSCode unbindingWithCollector(String userkey, String collectorCode) throws Exception;

    BaseWS.WSCode regist(String username, String password) throws Exception;

    JSONObject getSwitch(String switchID) throws Exception;

    JSONArray findSwitchByCollectorCode(String code) throws Exception;

    JSONArray findCollectorByFsuCode(String fsuCode) throws Exception;

    JSONArray findCollector1(String userID) throws Exception;

    JSONArray findCollector(String userID) throws Exception;

    JSONArray findSignalsBySwitchID(String switchID) throws Exception;

//    JSONArray getAverageByDay(String switchID, String signalsTypeID, String time) throws Exception;

//    JSONArray getAverageByTenMinute(String switchID, String signalsTypeID, String time) throws Exception;

//    JSONArray getAverageByMonth(String switchID, String signalsTypeID, String time) throws Exception;

    BaseWS.WSCode updateSwitch(String userkey, String switchID, String name, String iconType) throws Exception;

    JSONArray findSignalsType(String type) throws Exception;

    JSONObject operateSwitch(String userkey, String switchID, String cmdData) throws Exception;

//    JSONArray getTotalPowerByMonth(String collectorCode, String time) throws Exception;
//
//    JSONArray getTotalPowerByDay(String collectorCode, String time) throws Exception;

    JSONArray getTotalPowerByMonth(String userID, String collectorCode, String time) throws Exception;

    JSONArray getTotalPowerByDay(String userID, String collectorCode, String time) throws Exception;

    BaseWS.WSCode addOrUpdateTimeController(String timeControllerID, String switchID, String runTime, String weekday,
                                            String state, String cmdData, String upload) throws Exception;

    JSONArray findTimeControllerByCollectorID(String collectorID) throws Exception;

    JSONArray findTimeControllerByUser(String userID) throws Exception;

    BaseWS.WSCode deleteTimeController(String timeControllerID) throws Exception;

    boolean sendGetThreadValueCommand(String type, String switchCode, String index) throws Exception;

    boolean sendSetThreadValueCommand(String type, String switchCode, String index, String value)
            throws Exception;

    JSONObject findSwitchParamBySwitchIDAndParamID(String switchID, String paramID) throws Exception;

    BaseWS.WSCode addOrUpdateSceneSwitch(String sceneSwitchID, String sceneID, String switchID, String cmdData)
            throws Exception;

    BaseWS.WSCode deleteScene(String sceneID) throws Exception;

    BaseWS.WSCode deleteSceneSwitch(String sceneSwitchID) throws Exception;

    JSONArray findSceneByUser(String userID) throws Exception;

    JSONArray findSceneSwitchByScene(String sceneID) throws Exception;

    BaseWS.WSCode doScene(String sceneID) throws Exception;

    boolean getResultOfController(String controllerID) throws Exception;

    BaseWS.WSCode addOrUpdateScene(String scenes) throws Exception;

    BaseWS.WSCode updateCollectorParam(String collectorID, String name, String baud, String freq, String ranges,
                                       String HBFreq) throws Exception;

    BaseWS.WSCode addOrUpdateSwitch(String userkey, String switchID, String collectorID, String name, String switchCode,
                                    String freq, String iconType, String state) throws Exception;

    BaseWS.WSCode deleteSwitch(String switchID) throws Exception;

    JSONObject findCollectorByCollectorID(String collectorID) throws Exception;

    boolean refreshSwitchData(String switchCode) throws Exception;

    JSONArray sendRefreshCollector(String collectorID) throws Exception;

    JSONArray sendRefreshSwitch(String switchIDs) throws Exception;

    boolean addOrUpdatePrice(String userkey, String prices) throws Exception;

    JSONArray getPowerPrice(String userID) throws Exception;

    boolean updateUserMsg(String userID, String nickName, String headImg, String phone, String oldHeadImg,
                          String host_user, String prefix_user) throws Exception;

    JSONObject uploadImg(String prefix_user, MultipartFile photo, String host_user) throws Exception;

    JSONObject getUserMsg(String userID) throws Exception;

    JSONArray getSignalByDay(String time, String switchID, String signalsTypeID) throws Exception;

//    JSONArray getSignalByMonth(String time, String switchID, String signalsTypeID) throws Exception;

    boolean setSwitchSequence(String collectorID, String switchIDs) throws Exception;

    JSONArray getSignalByTime(String switchID, String time, String signalsTypeID, String type) throws Exception;

    @Transactional("TransactionManager_1")
    JSONArray getAllSwitchMsg(String userID) throws Exception;

    JSONArray getAllEE(String userID) throws Exception;

    boolean addOrUpdateEE(String id, String switchID, String name, Double gonglv) throws Exception;

    boolean deleteEE(String ids) throws Exception;

    JSONObject getCode(String phone) throws Exception;

    BaseWS.WSCode validatePhone(String phone) throws Exception;

    BaseWS.WSCode registByPhone(String username, String phone, String password, String code) throws Exception;

    BaseWS.WSCode updatePwd(String userkey, String newPwd, String oldPwd) throws Exception;

    BaseWS.WSCode resetPwd(String phone, String code, String password) throws Exception;

    JSONObject login(String username, String phone, String password, Integer version) throws Exception;

    boolean shareCollectorToUser(String userkey, String collectorID, String username, Integer enable)
            throws Exception;

    boolean deleteShareCollector(String userkey, String collectorID, String username) throws Exception;

    JSONObject wechatLogin(String token, String openid, String prefix_user, String host_user) throws Exception;

    JSONArray getShareList(String userID, String collectorID) throws Exception;

    JSONObject qqLogin(String nickName, String headImg, String openid, String prefix_user, String host_user)
            throws Exception;

    JSONArray getOperateRecord(String userID, int start, int length) throws Exception;

    JSONArray getAlarmList(String userID, int start, int length) throws Exception;

    boolean rebootCollector(String userID, String collectorID) throws Exception;

    boolean resetCollector(String userID, String collectorID) throws Exception;

    void getQRCode(String content, String title, HttpServletResponse resp) throws Exception;

	String getIpBySwitchID(String switchID);

	Set<String> getIpBySceneID(String sceneID);
}
