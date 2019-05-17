package com.rogy.smarte.controller.app;

import com.rogy.smarte.service.IAppClientService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

@RestController
public class AppClientAction extends BaseWS {
    @Autowired
    private IAppClientService appClientService;
    @Autowired
    private HttpServletRequest request;

    private String getNewUrl(String ip, HttpServletRequest request) {
        if (ip == null || ip.trim().isEmpty() || request == null)
            return null;
        try {
            String param = request.getQueryString();
            StringBuffer newUrl = new StringBuffer();
            newUrl.append("http://");
            newUrl.append(ip);
            int port = 8080;
            newUrl.append(":" + port);
            newUrl.append(param);
            return newUrl.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 注册
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_regist.do")
    public String regist(String username, String password) {
        try {
            WSCode result = appClientService.regist(username, password);
            return getResultJsonString(result);
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 获取指定断路器的信息
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_getSwitch.do")
    public String getSwitch(String switchID) {
        try {
            JSONObject result = appClientService.getSwitch(switchID);
            if (result == null || result.isEmpty()) {
                return getResultJsonString(WSCode.EMPTY_RESULT);
            }
            return getResultJsonString(WSCode.SUCCESS, result);
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 获取指定集中器下所有断路器信息
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_getSwitchByCollector.do")
    public String getSwitchByCollector(String collectorCode) {
        try {
            JSONArray result = appClientService.findSwitchByCollectorCode(collectorCode);
            if (result == null || result.isEmpty()) {
                return getResultJsonString(WSCode.EMPTY_RESULT);
            }
            return getResultJsonString(WSCode.SUCCESS, result);
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 获取用户所有断路器信息
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_getSwitchByUser.do")
    public String getSwitchByUser(String userkey) {
        try {
            String userID = appClientService.checkUserkey(userkey);
            if (userID == null || userID.trim().isEmpty()) {
                return getResultJsonString(WSCode.AUTH_FAILED);
            }
            // findSwitchByUser
            JSONArray result = appClientService.findSwitchByUser(userID);
            if (result == null || result.isEmpty()) {
                return getResultJsonString(WSCode.EMPTY_RESULT);
            }
            return getResultJsonString(WSCode.SUCCESS, result);
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 获取用户绑定的控制器信息
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_getCollector.do")
    public String getCollector(String userkey) {
        try {
            String userID = appClientService.checkUserkey(userkey);
            if (userID == null || userID.trim().isEmpty()) {
                return getResultJsonString(WSCode.AUTH_FAILED);
            }
            // findCollector1
            JSONArray result = appClientService.findCollector1(userID);
            if (result == null || result.isEmpty()) {
                return getResultJsonString(WSCode.EMPTY_RESULT);
            }
            return getResultJsonString(WSCode.SUCCESS, result);
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 获取指定集中器信息
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_getCollectorByCollectorID.do")
    public String getCollectorByCollectorID(String collectorID) {
        try {
            JSONObject result = appClientService.findCollectorByCollectorID(collectorID);
            if (result == null || result.isEmpty()) {
                return getResultJsonString(WSCode.EMPTY_RESULT);
            }
            return getResultJsonString(WSCode.SUCCESS, result);
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 获取指定断路器所有最新数据
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_getSignals.do")
    public String getSignals(String switchID) {
        // TODO
        try {
            JSONArray result = appClientService.findSignalsBySwitchID(switchID);
            if (result == null || result.isEmpty()) {
                return getResultJsonString(WSCode.EMPTY_RESULT);
            }
            return getResultJsonString(WSCode.SUCCESS, result);
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

//    /**
//     * 获取指定断路器的指定信号类型的某一天的平均值(按小时算)
//     */
//    @CrossOrigin
//    @RequestMapping(value = "/AppClientAction_getAverageValueByDay.do")
//    public String getAverageValueByDay(String switchID, String signalsTypeID, String time) {
//        // TODO
//        try {
//            JSONArray result = appClientService.getAverageByDay(switchID, signalsTypeID, time);
//            if (result == null || result.isEmpty()) {
//                return getResultJsonString(WSCode.EMPTY_RESULT);
//            }
//            return getResultJsonString(WSCode.SUCCESS, result);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return getResultJsonString(WSCode.FAILED);
//        }
//    }

//    /**
//     * 获取指定断路器的指定信号类型的某一天的平均值(按十分钟算)
//     */
//    @CrossOrigin
//    @RequestMapping(value = "/AppClientAction_getAverageValueByTenMinute.do")
//    public String getAverageValueByTenMinute(String switchID, String signalsTypeID, String time) {
//        // TODO
//        try {
//            JSONArray result = appClientService.getAverageByTenMinute(switchID, signalsTypeID, time);
//            if (result == null || result.isEmpty()) {
//                return getResultJsonString(WSCode.EMPTY_RESULT);
//            }
//            return getResultJsonString(WSCode.SUCCESS, result);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return getResultJsonString(WSCode.FAILED);
//        }
//    }

//    /**
//     * 获取指定断路器的指定信号类型的某一个月的平均值(按天算)
//     */
//    @CrossOrigin
//    @RequestMapping(value = "/AppClientAction_getAverageValueByMonth.do")
//    public String getAverageValueByMonth(String switchID, String signalsTypeID, String time) {
//        // TODO
//        try {
//            JSONArray result = appClientService.getAverageByMonth(switchID, signalsTypeID, time);
//            if (result == null || result.isEmpty()) {
//                return getResultJsonString(WSCode.EMPTY_RESULT);
//            }
//            return getResultJsonString(WSCode.SUCCESS, result);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return getResultJsonString(WSCode.FAILED);
//        }
//    }

    /**
     * 修改指定断路器名称和图片资源type
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_updateSwitch.do")
    public String updateSwitch(String userkey, String switchID, String name, String iconType) {
        try {
            WSCode result = appClientService.updateSwitch(userkey, switchID, name, iconType);
            return getResultJsonString(result);
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 获取所有信号类型
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_findSignalsType.do")
    public String findSignalsType(String type) {
        try {
            JSONArray result = appClientService.findSignalsType(type);
            if (result == null || result.isEmpty()) {
                return getResultJsonString(WSCode.EMPTY_RESULT);
            }
            return getResultJsonString(WSCode.SUCCESS, result);
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 对指定断路器进行开关操作
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_operateSwitch.do")
    public String operateSwitch(String userkey, String switchID, String cmdData) {
        try {
            JSONObject result = appClientService.operateSwitch(userkey, switchID, cmdData);
            if (result == null || result.isEmpty()) {
                return getResultJsonString(WSCode.EMPTY_RESULT);
            }
            return getResultJsonString(WSCode.SUCCESS, result);
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

//    /**
//     * 获取指定控制器下所有断路器某个月的用电总量
//     */
//    @CrossOrigin
//    @RequestMapping(value = "/AppClientAction_getTotalPowerByMonth.do")
//    public String getTotalPowerByMonth(String collectorCode, String time) {
//        try {
//            JSONArray result = appClientService.getTotalPowerByMonth(collectorCode, time);
//            if (result == null || result.isEmpty()) {
//                return getResultJsonString(WSCode.EMPTY_RESULT);
//            }
//            return getResultJsonString(WSCode.SUCCESS, result);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return getResultJsonString(WSCode.FAILED);
//        }
//    }

//    /**
//     * 获取指定控制器下所有断路器某一天的用电总量
//     */
//    @CrossOrigin
//    @RequestMapping(value = "/AppClientAction_getTotalPowerByDay.do")
//    public String getTotalPowerByDay(String collectorCode, String time) {
//        try {
//            JSONArray result = appClientService.getTotalPowerByDay(collectorCode, time);
//            if (result == null || result.isEmpty()) {
//                return getResultJsonString(WSCode.EMPTY_RESULT);
//            }
//            return getResultJsonString(WSCode.SUCCESS, result);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return getResultJsonString(WSCode.FAILED);
//        }
//    }

    /**
     * 获取指定控制器下所有断路器某个月的用电总量
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_getTotalPowerByMonth_v1.do")
    public String getTotalPowerByMonth_v1(String userkey, String collectorCode, String time) {
        try {
            String userID = appClientService.checkUserkey(userkey);
            if (userID == null || userID.trim().isEmpty()) {
                return getResultJsonString(WSCode.AUTH_FAILED);
            }
            // getTotalPowerByMonth
            JSONArray result = appClientService.getTotalPowerByMonth(userID, collectorCode, time);
            if (result == null || result.isEmpty()) {
                return getResultJsonString(WSCode.EMPTY_RESULT);
            }
            return getResultJsonString(WSCode.SUCCESS, result);
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 获取指定控制器下所有断路器某一天的用电总量
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_getTotalPowerByDay_v1.do")
    public String getTotalPowerByDay_v1(String userkey, String collectorCode, String time) {
        try {
            String userID = appClientService.checkUserkey(userkey);
            if (userID == null || userID.trim().isEmpty()) {
                return getResultJsonString(WSCode.AUTH_FAILED);
            }
            // getTotalPowerByDay
            JSONArray result = appClientService.getTotalPowerByDay(userID, collectorCode, time);
            if (result == null || result.isEmpty()) {
                return getResultJsonString(WSCode.EMPTY_RESULT);
            }
            return getResultJsonString(WSCode.SUCCESS, result);
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 用户绑定控制器
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_bindingWithCollector.do")
    public String bindingWithCollector(String userkey, String collectorCode) {
        try {
            WSCode result = appClientService.bindingWithCollector(userkey, collectorCode);
            return getResultJsonString(Objects.requireNonNullElse(result, WSCode.FAILED));
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 用户解除绑定集中器
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_unbindingWithCollector.do")
    public String unbindingWithCollector(String userkey, String collectorCode) {
        try {
            WSCode result = appClientService.unbindingWithCollector(userkey, collectorCode);
            return getResultJsonString(Objects.requireNonNullElse(result, WSCode.FAILED));
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 新增或修改定时控制器信息
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_addOrUpdateTimeController.do")
    public String addOrUpdateTimeController(String timeControllerID, String switchID, String time, String weekday,
                                            String state, String cmdData, String upload) {
        try {
            WSCode result = appClientService.addOrUpdateTimeController(timeControllerID, switchID, time, weekday, state,
                    cmdData, upload);
            return getResultJsonString(Objects.requireNonNullElse(result, WSCode.EMPTY_RESULT));
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 获取指定控制器下所有断路器的定时任务信息
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_findTimeControllerByCollector.do")
    public String findTimeControllerByCollector(String collectorID) {
        try {
            JSONArray result = appClientService.findTimeControllerByCollectorID(collectorID);
            if (result == null || result.isEmpty()) {
                return getResultJsonString(WSCode.EMPTY_RESULT);
            }
            return getResultJsonString(WSCode.SUCCESS, result);
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 获取用户所有定时任务
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_findTimeControllerByUser.do")
    public String findTimeControllerByUser(String userkey) {
        try {
            String userID = appClientService.checkUserkey(userkey);
            if (userID == null || userID.trim().isEmpty()) {
                return getResultJsonString(WSCode.AUTH_FAILED);
            }
            JSONArray result = appClientService.findTimeControllerByUser(userID);
            if (result == null || result.isEmpty()) {
                return getResultJsonString(WSCode.EMPTY_RESULT);
            }
            return getResultJsonString(WSCode.SUCCESS, result);
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 删除指定的定时任务
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_deleteTimeController.do")
    public String deleteTimeController(String timeControllerID) {
        try {
            WSCode result = appClientService.deleteTimeController(timeControllerID);
            return getResultJsonString(Objects.requireNonNullElse(result, WSCode.FAILED));
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 发送查询阈值命令
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_sendGetThreadValueCommand.do")
    public String sendGetThreadValueCommand(String type, String switchCode, String index) {
        try {
            boolean flag = appClientService.sendGetThreadValueCommand(type, switchCode, index);
            if (!flag) {
                return getResultJsonString(WSCode.FAILED);
            }
            return getResultJsonString(WSCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 发送设置阈值命令
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_sendSetThreadValueCommand.do")
    public String sendSetThreadValueCommand(String type, String switchCode, String index, String value) {
        try {
            boolean flag = appClientService.sendSetThreadValueCommand(type, switchCode, index, value);
            if (!flag) {
                return getResultJsonString(WSCode.FAILED);
            }
            return getResultJsonString(WSCode.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 获取指定断路器的某种属性
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_findSwitchParamBySwitch.do")
    public String findSwitchParamBySwitch(String switchID, String paramID) {
        try {
            JSONObject result = appClientService.findSwitchParamBySwitchIDAndParamID(switchID, paramID);
            if (result == null) {
                return getResultJsonString(WSCode.EMPTY_RESULT);
            }
            return getResultJsonString(WSCode.SUCCESS, result);
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 新增或修改场景
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_addOrUpdateScene.do")
    public String addOrUpdateScene(String scenes) {
        try {
            WSCode result = appClientService.addOrUpdateScene(scenes);
            return getResultJsonString(Objects.requireNonNullElse(result, WSCode.FAILED));
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 删除场景信息
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_deleteScene.do")
    public String deleteScene(String sceneID) {
        try {
            WSCode result = appClientService.deleteScene(sceneID);
            return getResultJsonString(Objects.requireNonNullElse(result, WSCode.FAILED));
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 获取控用户所有场景信息
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_findSceneByUser.do")
    public String findSceneByUser(String userkey) {
        try {
            String userID = appClientService.checkUserkey(userkey);
            if (userID == null || userID.trim().isEmpty()) {
                return getResultJsonString(WSCode.AUTH_FAILED);
            }
            // findSceneByUser
            JSONArray result = appClientService.findSceneByUser(userID);
            if (result == null || result.isEmpty()) {
                return getResultJsonString(WSCode.EMPTY_RESULT);
            }
            return getResultJsonString(WSCode.SUCCESS, result);
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 获取场景下所有断路器信息
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_findSceneSwitchByScene.do")
    public String findSceneSwitchByScene(String sceneID) {
        try {
            JSONArray result = appClientService.findSceneSwitchByScene(sceneID);
            if (result == null || result.isEmpty()) {
                return getResultJsonString(WSCode.EMPTY_RESULT);
            }
            return getResultJsonString(WSCode.SUCCESS, result);
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 执行场景
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_doScene.do")
    public String doScene(String sceneID) {
        Set<String> ips = appClientService.getIpBySceneID(sceneID);
        for (String ip : ips) {
            String newUrl = getNewUrl(ip, request);
        }
        try {
            WSCode result = appClientService.doScene(sceneID);
            return getResultJsonString(Objects.requireNonNullElse(result, WSCode.FAILED));
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 查询开关操作的结果
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_getResultOfController.do")
    public String getResultOfController(String controllerID) {
        try {
            boolean flag = appClientService.getResultOfController(controllerID);
            if (!flag) {
                return (getResultJsonString(WSCode.FAILED));
            }
            return (getResultJsonString(WSCode.SUCCESS));
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 更新集中器的波特率,数据上报频率,数据上报变化幅度
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_updateCollectorParam.do")
    public String updateCollectorParam(String collectorID, String name, String baud, String freq, String ranges,
                                       String HBFreq) {
        try {
            WSCode result = appClientService.updateCollectorParam(collectorID, name, baud, freq, ranges, HBFreq);
            return getResultJsonString(Objects.requireNonNullElse(result, WSCode.FAILED));
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 新增/修改断路器信息
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_addOrUpdateSwitch.do")
    public String addOrUpdateSwitch(String userkey, String switchID, String collectorID, String name, String switchCode,
                                    String freq, String iconType, String state) {
        try {
            WSCode result = appClientService.addOrUpdateSwitch(userkey, switchID, collectorID, name, switchCode, freq,
                    iconType, state);
            return getResultJsonString(Objects.requireNonNullElse(result, WSCode.FAILED));
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 删除断路器
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_deleteSwitch.do")
    public String deleteSwitch(String switchID) {
        try {
            WSCode result = appClientService.deleteSwitch(switchID);
            return getResultJsonString(Objects.requireNonNullElse(result, WSCode.FAILED));
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 通知断路器提交数据
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_refreshSwitch.do")
    public String refreshSwitch(String switchCode) {
        try {
            boolean flag = appClientService.refreshSwitchData(switchCode);
            if (!flag) {
                return (getResultJsonString(WSCode.FAILED));
            }
            return (getResultJsonString(WSCode.SUCCESS));
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 发送更新集中器状态的命令
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_sendRefreshCollector.do")
    public String sendRefreshCollector(String collectorID) {
        try {
            JSONArray result = appClientService.sendRefreshCollector(collectorID);
            if (result == null || result.isEmpty()) {
                return (getResultJsonString(WSCode.EMPTY_RESULT));
            }
            return (getResultJsonString(WSCode.SUCCESS, result));
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 新增/修改电费
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_addOrUpdatePowerPrice.do")
    public String addOrUpdatePowerPrice(String userkey, String prices) {
        try {
            boolean flag = appClientService.addOrUpdatePrice(userkey, prices);
            if (!flag) {
                return (getResultJsonString(WSCode.FAILED));
            }
            return (getResultJsonString(WSCode.SUCCESS));
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 获取电费
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_getPowerPrice.do")
    public String getPowerPrice(String userkey) {
        try {
            String userID = appClientService.checkUserkey(userkey);
            if (userID == null || userID.trim().isEmpty()) {
                return (getResultJsonString(WSCode.AUTH_FAILED));
            }
            JSONArray result = appClientService.getPowerPrice(userID);
            if (result == null || result.isEmpty()) {
                return (getResultJsonString(WSCode.EMPTY_RESULT));
            }
            return (getResultJsonString(WSCode.SUCCESS, result));
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 获取用户个人信息
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_getUserMsg.do")
    public String getUserMsg(String userkey) {
        try {
            String userID = appClientService.checkUserkey(userkey);
            if (userID == null || userID.trim().isEmpty()) {
                return (getResultJsonString(WSCode.AUTH_FAILED));
            }
            JSONObject result = appClientService.getUserMsg(userID);
            if (result == null) {
                return (getResultJsonString(WSCode.FAILED));
            }
            return (getResultJsonString(WSCode.SUCCESS, result));
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 更新用户信息(昵称,头像,电话号码)
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_updateUserMsg.do")
    public String updateUserMsg(String userkey, String nickName, String headImg, String phone, String oldHeadImg) {
        try {
            String userID = appClientService.checkUserkey(userkey);
            if (userID == null || userID.trim().isEmpty()) {
                return (getResultJsonString(WSCode.AUTH_FAILED));
            }
            // updateUserMsg
            boolean flag = appClientService.updateUserMsg(userID, nickName, headImg, phone, oldHeadImg, host_user,
                    prefix_user);
            if (!flag) {
                return (getResultJsonString(WSCode.FAILED));
            }
            return (getResultJsonString(WSCode.SUCCESS));
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 获取指定断路器某一天的数据
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_getSignalByDay.do")
    public String getSignalByDay(String time, String switchID, String signalsTypeID) {
        try {
            JSONArray result = appClientService.getSignalByDay(time, (switchID), (signalsTypeID));
            if (result == null || result.isEmpty()) {
                return (getResultJsonString(WSCode.EMPTY_RESULT));
            }
            return (getResultJsonString(WSCode.SUCCESS, result));
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

//    /**
//     * 获取指定断路器某个月的数据
//     */
//    @CrossOrigin
//    @RequestMapping(value = "/AppClientAction_getSignalByMonth.do")
//    public String getSignalByMonth(String time, String switchID, String signalsTypeID) {
//        try {
//            JSONArray result = appClientService.getSignalByMonth(time, (switchID), (signalsTypeID));
//            if (result == null || result.isEmpty()) {
//                return (getResultJsonString(WSCode.EMPTY_RESULT));
//            }
//            return (getResultJsonString(WSCode.SUCCESS, result));
//        } catch (Exception e) {
//            e.printStackTrace();
//            return getResultJsonString(WSCode.FAILED);
//        }
//    }

    @Value("${prefix_user}")
    private String prefix_user;
    @Value("${host_user}")
    private String host_user;

    /**
     * 更新头像
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_uploadHeadImg.do")
    public String uploadHeadImg(@RequestParam("photo") MultipartFile photo) {
        try {
            JSONObject result = appClientService.uploadImg(prefix_user, photo, host_user);
            if (result == null) {
                return (getResultJsonString(WSCode.FAILED));
            }
            return (getResultJsonString(WSCode.SUCCESS, result));
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 设置集中器下所有断路器的排序
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_setSwitchSequence.do")
    public String setSwitchSequence(String userkey, String collectorID, String switchIDs) {
        try {
            String userID = appClientService.checkUserkey(userkey);
            if (userID == null || userID.trim().isEmpty()) {
                return (getResultJsonString(WSCode.AUTH_FAILED));
            }
            // setSwitchSequence
            boolean flag = appClientService.setSwitchSequence(collectorID, switchIDs);
            if (!flag) {
                return (getResultJsonString(WSCode.FAILED));
            }
            return (getResultJsonString(WSCode.SUCCESS));
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 获取指定断路器某个时间的数据(某年/某月)
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_getSignalByTime.do")
    public String getSignalByTime(String switchID, String time, String signalsTypeID, String type) {
        try {
            JSONArray result = appClientService.getSignalByTime((switchID), time, (signalsTypeID), type);
            if (result == null || result.isEmpty()) {
                return (getResultJsonString(WSCode.EMPTY_RESULT));
            }
            return (getResultJsonString(WSCode.SUCCESS, result));
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 获取用户所有的断路器信息
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_getAllSwitchMsg.do")
    public String getAllSwitchMsg(String userkey) {
        try {
            String userID = appClientService.checkUserkey(userkey);
            if (userID == null || userID.trim().isEmpty()) {
                return (getResultJsonString(WSCode.AUTH_FAILED));
            }
            // getAllSwitchMsg
            JSONArray result = appClientService.getAllSwitchMsg(userID);
            if (result == null || result.isEmpty()) {
                return (getResultJsonString(WSCode.EMPTY_RESULT));
            }
            return (getResultJsonString(WSCode.SUCCESS, result));
        } catch (Exception e) {
            e.printStackTrace();
            return getResultJsonString(WSCode.FAILED);
        }
    }

    /**
     * 获取用户所有电器信息
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_getAllEE.do")
    public String getAllEE(String userkey) {
        try {
            String userID = appClientService.checkUserkey(userkey);
            if (userID == null || userID.trim().isEmpty()) {
                return (getResultJsonString(WSCode.AUTH_FAILED));
            }
            // getAllEE
            JSONArray result = appClientService.getAllEE(userID);
            if (result == null || result.isEmpty()) {
                return (getResultJsonString(WSCode.EMPTY_RESULT));
            }
            return (getResultJsonString(WSCode.SUCCESS, result));
        } catch (Exception e) {
            e.printStackTrace();
            return (getResultJsonString(WSCode.FAILED));
        }
    }

    /**
     * 新增或修改电器信息
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_addOrUpdateEE.do")
    public String addOrUpdateEE(String electricalEquipmentID, String switchID, String name, Double gonglv) {
        try {
            boolean flag = appClientService.addOrUpdateEE(electricalEquipmentID, (switchID), name, gonglv);
            if (!flag) {
                return (getResultJsonString(WSCode.FAILED));
            }
            return (getResultJsonString(WSCode.SUCCESS));
        } catch (Exception e) {
            e.printStackTrace();
            return (getResultJsonString(WSCode.FAILED));
        }
    }

    /**
     * 删除电器信息
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_deleteEE.do")
    public String deleteEE(String electricalEquipmentIDs) {
        try {
            boolean flag = appClientService.deleteEE(electricalEquipmentIDs);
            if (!flag) {
                return (getResultJsonString(WSCode.FAILED));
            }
            return (getResultJsonString(WSCode.SUCCESS));
        } catch (Exception e) {
            e.printStackTrace();
            return (getResultJsonString(WSCode.FAILED));
        }
    }

    /**
     * 获取验证码
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_getCode.do")
    public String getCode(String phone) {
        try {
            JSONObject result = appClientService.getCode(phone);
            if (result == null) {
                return (getResultJsonString(WSCode.FAILED));
            }
            return (getResultJsonString(WSCode.SUCCESS, result));
        } catch (Exception e) {
            e.printStackTrace();
            return (getResultJsonString(WSCode.FAILED));
        }
    }

    /**
     * 验证手机号码是否已注册
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_validatePhone.do")
    public String validatePhone(String phone) {
        try {
            WSCode result = appClientService.validatePhone(phone);
            return getResultJsonString(Objects.requireNonNullElse(result, WSCode.FAILED));
        } catch (Exception e) {
            e.printStackTrace();
            return (getResultJsonString(WSCode.FAILED));
        }
    }

    /**
     * 通过手机号码注册
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_registByPhone.do")
    public String registByPhone(String username, String phone, String password, String code) {
        try {
            WSCode result = appClientService.registByPhone(username, phone, password, code);
            return getResultJsonString(Objects.requireNonNullElse(result, WSCode.FAILED));
        } catch (Exception e) {
            e.printStackTrace();
            return (getResultJsonString(WSCode.FAILED));
        }
    }

    /**
     * 修改密码
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_updatePwd.do")
    public String updatePwd(String userkey, String newPwd, String password) {
        try {
            WSCode result = appClientService.updatePwd(userkey, newPwd, password);
            return getResultJsonString(Objects.requireNonNullElse(result, WSCode.FAILED));
        } catch (Exception e) {
            e.printStackTrace();
            return (getResultJsonString(WSCode.FAILED));
        }
    }

    /**
     * 重置密码
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_resetPwd.do")
    public String resetPwd(String phone, String code, String password) {
        try {
            WSCode result = appClientService.resetPwd(phone, code, password);
            return getResultJsonString(Objects.requireNonNullElse(result, WSCode.FAILED));
        } catch (Exception e) {
            e.printStackTrace();
            return (getResultJsonString(WSCode.FAILED));
        }
    }

    /**
     * 通过手机号码登录
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_login.do")
    public String login(String username, String phone, String password, Integer version) {
        try {
            JSONObject result = appClientService.login(username, phone, password, version);
            if (result == null) {
                return (getResultJsonString(WSCode.WRONG_PARAMETER_VALUE));
            }
            return (getResultJsonString(WSCode.SUCCESS, result));
        } catch (Exception e) {
            e.printStackTrace();
            return (getResultJsonString(WSCode.FAILED));
        }
    }

    /**
     * 分享集中器给其他用户
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_shareCollectorToUser.do")
    public String shareCollectorToUser(String userkey, String collectorID, String username, Integer enable) {
        try {
            boolean flag = appClientService.shareCollectorToUser(userkey, collectorID, username, enable);
            if (!flag) {
                return (getResultJsonString(WSCode.FAILED));
            }
            return (getResultJsonString(WSCode.SUCCESS));
        } catch (Exception e) {
            e.printStackTrace();
            return (getResultJsonString(WSCode.FAILED));
        }
    }

    /**
     * 删除分享集中器
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_deleteShareCollector.do")
    public String deleteShareCollector(String userkey, String collectorID, String username) {
        try {
            boolean flag = appClientService.deleteShareCollector(userkey, collectorID, username);
            if (!flag) {
                return (getResultJsonString(WSCode.FAILED));
            }
            return (getResultJsonString(WSCode.SUCCESS));
        } catch (Exception e) {
            e.printStackTrace();
            return (getResultJsonString(WSCode.FAILED));
        }
    }

    /**
     * 获取集中器的分享列表
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_getShareList.do")
    public String getShareList(String userkey, String collectorID) {
        try {
            String userID = appClientService.checkUserkey(userkey);
            if (userID == null || userID.trim().isEmpty()) {
                return (getResultJsonString(WSCode.AUTH_FAILED));
            }
            // getShareList
            JSONArray result = appClientService.getShareList(userID, collectorID);
            if (result == null || result.isEmpty()) {
                return (getResultJsonString(WSCode.EMPTY_RESULT));
            }
            return (getResultJsonString(WSCode.SUCCESS, result));
        } catch (Exception e) {
            return (getResultJsonString(WSCode.FAILED));
        }
    }

    /**
     * 微信登录
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_wechatLogin.do")
    public String wechatLogin(String access_token, String openid) {
        try {
            JSONObject result = appClientService.wechatLogin(access_token, openid, prefix_user, host_user);
            if (result == null) {
                return (getResultJsonString(WSCode.WRONG_PARAMETER_VALUE));
            }
            return (getResultJsonString(WSCode.SUCCESS, result));
        } catch (Exception e) {
            e.printStackTrace();
            return (getResultJsonString(WSCode.FAILED));
        }
    }

    /**
     * qq登录
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_qqLogin.do")
    public String qqLogin(String nickName, String headImg, String openid) {
        try {
            JSONObject result = appClientService.qqLogin(nickName, headImg, openid, prefix_user, host_user);
            if (result == null) {
                return (getResultJsonString(WSCode.WRONG_PARAMETER_VALUE));
            }
            return (getResultJsonString(WSCode.SUCCESS, result));
        } catch (Exception e) {
            e.printStackTrace();
            return (getResultJsonString(WSCode.FAILED));
        }
    }

    /**
     * 获取操作记录列表
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_getOperateRecord.do")
    public String getOperateRecord(String userkey, Integer start, Integer length) {
        try {
            String userID = appClientService.checkUserkey(userkey);
            if (userID == null || userID.trim().isEmpty()) {
                return (getResultJsonString(WSCode.AUTH_FAILED));
            }
            // getOperateRecord
            JSONArray result = appClientService.getOperateRecord(userID, start, length);
            if (result == null || result.isEmpty()) {
                return (getResultJsonString(WSCode.EMPTY_RESULT));
            }
            return (getResultJsonString(WSCode.SUCCESS, result));
        } catch (Exception e) {
            e.printStackTrace();
            return (getResultJsonString(WSCode.FAILED));
        }
    }

    /**
     * 获取故障列表
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_getAlarmList.do")
    public String getAlarmList(String userkey, Integer start, Integer length) {
        try {
            String userID = appClientService.checkUserkey(userkey);
            if (userID == null || userID.trim().isEmpty()) {
                return (getResultJsonString(WSCode.AUTH_FAILED));
            }
            // getAlarmList
            JSONArray result = appClientService.getAlarmList(userID, start, length);
            if (result == null || result.isEmpty()) {
                return (getResultJsonString(WSCode.EMPTY_RESULT));
            }
            return (getResultJsonString(WSCode.SUCCESS, result));
        } catch (Exception e) {
            e.printStackTrace();
            return (getResultJsonString(WSCode.FAILED));
        }
    }

    /**
     * 重启集中器
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_rebootCollector.do")
    public String rebootCollector(String userkey, String collectorID) {
        try {
            String userID = appClientService.checkUserkey(userkey);
            if (userID == null || userID.trim().isEmpty()) {
                return (getResultJsonString(WSCode.AUTH_FAILED));
            }
            // rebootCollector
            boolean flag = appClientService.rebootCollector(userID, collectorID);
            if (!flag) {
                return (getResultJsonString(WSCode.FAILED));
            }
            return (getResultJsonString(WSCode.SUCCESS));
        } catch (Exception e) {
            e.printStackTrace();
            return (getResultJsonString(WSCode.FAILED));
        }
    }

    /**
     * 重置集中器
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_resetCollector.do")
    public String resetCollector(String userkey, String collectorID) {
        try {
            String userID = appClientService.checkUserkey(userkey);
            if (userID == null || userID.trim().isEmpty()) {
                return (getResultJsonString(WSCode.AUTH_FAILED));
            }
            /* rebootCollector */
            boolean flag1 = appClientService.resetCollector(userID, collectorID);
            if (!flag1) {
                return (getResultJsonString(WSCode.FAILED));
            }
            return (getResultJsonString(WSCode.SUCCESS));
        } catch (Exception e1) {
            e1.printStackTrace();
            return (getResultJsonString(WSCode.FAILED));
        }
    }

    /**
     * 获取二维码
     */
    @CrossOrigin
    @RequestMapping(value = "/AppClientAction_getQRCode.do")
    public void getQRCode(String content, String title, HttpServletResponse response) {
        try {
            appClientService.getQRCode(content, title, response);
//			response.getWriter().write(getResultJsonString(WSCode.SUCCESS));
        } catch (Exception e) {
            try {
                response.getWriter().write(getResultJsonString(WSCode.FAILED));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
//            return (getResultJsonString(WSCode.FAILED));
        }
    }
}
