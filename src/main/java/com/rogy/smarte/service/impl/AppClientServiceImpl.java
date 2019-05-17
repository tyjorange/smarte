package com.rogy.smarte.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.rogy.smarte.controller.app.BaseWS.WSCode;
import com.rogy.smarte.entity.db1.*;
import com.rogy.smarte.fsu.VirtualFsuController;
import com.rogy.smarte.repository.db1.*;
import com.rogy.smarte.service.IAppClientService;
import com.rogy.smarte.util.CreateQrCode;
import com.rogy.smarte.util.PowerManagerUtil;
import com.rogy.smarte.util.SendMessage;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AppClientServiceImpl implements IAppClientService {

	// 验证码有效的时间
	private long timeout = 1000 * 60 * 5;
	private int version = 10;
	@PersistenceContext(unitName = "EntityManagerFactoryBean_1")
	private EntityManager entityManager;

	@Resource
	private HttpServletRequest request;
	@Resource
	private SwitchDao switchDao;
	@Resource
	private CollectorDao collectorDao;
	@Resource
	private SignalsTypeDao signalsTypeDao;
	@Resource
	private ControllerDao controllerDao;
	@Resource
	private UserDao userDao;
	@Resource
	private UserCollectorDao userCollectorDao;
	@Resource
	private TimeControllerDao timeControllerDao;
	@Resource
	private TimeControllerImpl timeControllerImpl;
	@Resource
	private SwitchParamDao switchParamDao;
	@Resource
	private SignalsNewDao signalsNewDao;
	@Resource
	private SceneDao sceneDao;
	@Resource
	private SceneSwitchDao sceneSwitchDao;
	@Resource
	private SwitchServiceImpl switchService;
	@Resource
	private CollectorServiceImpl collectorService;
	@Resource
	private TimeOfUsePricingDao timeOfUsePricingDao;
	@Resource
	private SignalHourDao signalHourDao;
	@Resource
	private ApexDayDao apexDayDao;
	@Resource
	private ApexMonthDao apexMonthDao;
	@Resource
	private ElectricalEquipmentDao electricalEquipmentDao;
	@Resource
	private OperateRecordDao operateRecordDao;
	@Resource
	private CollectorShareDao collectorShareDao;
	@Resource
	private DeviceAlarmDao deviceAlarmDao;
	@Resource
	private SwitchParamSettingDao switchParamSettingDao;
	@Resource
	private ZDataDao zDataDao;

//    /**
//     * 新增一条操作记录
//     *
//     * @param userID
//     * @param desc
//     * @throws Exception
//     */
	// private void insertRecord(String userID, String desc) throws Exception {
	// User u = userDao.findById(userID);
	// OperateRecord or = new OperateRecord();
	// or.setId(0L);
	// if (u != null)
	// or.setUser(u);
	// or.setTime(Timestamp.from(Instant.now()));
	// or.setDesc(desc);
	// operateRecordDao.addOrUpdateOperateRecord(or);
	// }

	/**
	 * 用户校验
	 *
	 * @param userkey 代表用户身份的key字符串(用户通过登录WS调用获得)
	 * @return 用户合法时返回该用户对象;否则返回null
	 */
	private User checkLogin(String userkey) throws Exception {
		UserKey userKey = new UserKey(userkey);
		if (!userKey.isValid())
			return null;
		else {
			List<User> users;
			User user;
			if (userKey.getPassword() == null || userKey.getPassword().trim().isEmpty())
				users = userDao.findByUsername(userKey.getUserLoginID());
			else
				users = userDao.findByUsernameAndPassword(userKey.getUserLoginID(), userKey.getPassword());
			if (users == null || users.isEmpty())
				return null;
			else
				user = users.get(0);
			if (user != null && user.getId().equals(userKey.getUserID()))
				return user;
			else
				return null;
		}
	}

	@Override
	public String checkUserkey(String userkey) throws Exception {
		if (userkey == null || userkey.trim().isEmpty())
			return null;
		User user = checkLogin(userkey);
		if (user != null)
			return user.getId();
		return null;
	}

	@Override
	public JSONArray findSwitchByUser(String userID) throws Exception {
		if (userID == null || userID.trim().isEmpty())
			return null;
		List<UserCollector> userCollectors = userCollectorDao.findByUserID(userID);
		if (userCollectors == null || userCollectors.isEmpty())
			return null;
		JSONArray ja = new JSONArray();
		for (UserCollector uc : userCollectors) {
			List<Switch> switchs = switchDao.findSwitchByCollectorID(uc.getCollector().getCollectorID());
			if (switchs == null || switchs.isEmpty())
				continue;
			for (Switch s : switchs) {
				JSONObject jo_s = JSONObject.fromObject(s);
				JSONObject jo_c = jo_s.getJSONObject("collector");
				// LocalDateTime now = LocalDateTime.now();
				// if (s.getCollector().getHBTime() != null
				// && (now.minusSeconds(s.getCollector().getHBFreq() * 2)
				// .isBefore(s.getCollector().getHBTime()
				// .toLocalDateTime())))
				// jo_c.put("online", s.getCollector().getActive());
				// else
				// jo_c.put("online", 0);
				jo_c.put("online", s.getCollector().getActive());
				jo_c.remove("fsu");
				ja.add(jo_s);
			}
		}
		return ja;
	}

	/**
	 * 用户绑定控制器
	 *
	 * @param userkey       登录成功后服务区返回的字符串
	 * @param collectorCode 控制器id
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public WSCode bindingWithCollector(String userkey, String collectorCode) throws Exception {
		if (userkey == null || userkey.trim().isEmpty() || collectorCode == null || collectorCode.trim().isEmpty())
			return WSCode.WRONG_PARAMETER_FORMAT;
		User user = checkLogin(userkey);
		if (user == null)
			return WSCode.AUTH_FAILED;
		// Collector collector =
		// collectorDao.findByCollectorCode(collectorCode);
		List<Collector> collector = collectorDao.findBySetupCode(collectorCode);
		if (collector == null || collector.isEmpty())
			return WSCode.WRONG_PARAMETER_VALUE;
		List<UserCollector> uc = userCollectorDao.findBySetupCode(collectorCode);
		// UserCollector uc = userCollectorDao.findByUsernameAndCollectorID(
		// user.getUsername(), collector.getCollectorID());
		if (uc != null && !uc.isEmpty())
			return WSCode.FAILED;
		UserCollector userCollector = new UserCollector();
		userCollector.setId(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
		userCollector.setUser(user);
		userCollector.setCollector(collector.get(0));
		userCollectorDao.saveAndFlush(userCollector);
		return WSCode.SUCCESS;
	}

	/**
	 * 用户接触绑定集中器
	 *
	 * @param userkey
	 * @param collectorCode
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public WSCode unbindingWithCollector(String userkey, String collectorCode) throws Exception {
		if (userkey == null || userkey.trim().isEmpty() || collectorCode == null || collectorCode.trim().isEmpty())
			return WSCode.WRONG_PARAMETER_FORMAT;
		User user = checkLogin(userkey);
		if (user == null)
			return WSCode.AUTH_FAILED;
		List<Collector> collectors = collectorDao.findByCollectorCode(collectorCode);
		if (collectors == null || collectors.isEmpty())
			return WSCode.WRONG_PARAMETER_VALUE;
		List<UserCollector> ucs = userCollectorDao.findByUsernameAndCollectorID(user.getUsername(),
				collectors.get(0).getCollectorID());
		if (ucs == null || ucs.isEmpty())
			return WSCode.SUCCESS;
		userCollectorDao.delete(ucs.get(0));
		List<CollectorShare> css = collectorShareDao.findByCollectorID(collectors.get(0).getCollectorID());
		if (css != null && !css.isEmpty()) {
			for (CollectorShare cs : css) {
				collectorShareDao.delete(cs);;
			}
		}
		return WSCode.SUCCESS;
	}

	/**
	 * 注册
	 *
	 * @param username 用户名
	 * @param password 密码
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public WSCode regist(String username, String password) throws Exception {
		if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty())
			return WSCode.WRONG_PARAMETER_FORMAT;
		List<User> results = userDao.findByUsername(username);
		if (results != null && !results.isEmpty())
			return WSCode.EXIST;
		User user = new User();
		user.setId(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
		user.setUsername(username);
		user.setPassword(password);
		userDao.saveAndFlush(user);
		return WSCode.SUCCESS;
	}

	// /**
	// * 登录
	// *
	// * @param username
	// * 用户名
	// * @param password
	// * 密码
	// * @return
	// * @throws Exception
	// */
	// public JSONObject doLogin(String username, String password)
	// throws Exception {
	// if (username == null || username.trim().isEmpty() || password == null
	// || password.trim().isEmpty())
	// return null;
	// User user = userDao.findByUsername(username);
	// if (user == null)
	// return null;
	// if (!user.getPassword().equals(password))
	// return null;
	// user.setTimeMills(BigInteger.valueOf(System.currentTimeMillis()));
	// userDao.addOrUpdateUser(user);
	// JSONObject jo = new JSONObject();
	// UserKey userKey = new UserKey();
	// userKey.setUserID(user.getId());
	// // userKey.setUserLoginID(username);
	// userKey.setPassword(password);
	// userKey.setTimeMillis(user.getTimeMills().longValue());
	// userKey.Valid(true);
	// jo.put("userkey", userKey.toUserKeyString());
	// return jo;
	// }

	@Override
	public JSONObject getSwitch(String switchID) throws Exception {
		if (switchID == null || switchID.trim().isEmpty())
			return null;
		List<Switch> ss = switchDao.findBySwitchID(Integer.valueOf(switchID));
		if (ss == null || ss.isEmpty())
			return null;
		Switch s = ss.get(0);
		JSONObject jo = JSONObject.fromObject(s);
		jo.remove("collector");
		jo.put("addTime", PowerManagerUtil.timestampToString(s.getAddTime()));
		jo.put("faultTime", PowerManagerUtil.timestampToString(s.getFaultTime()));
		return jo;
	}

	/**
	 * 根据指定控制器获取其所有断路器信息
	 *
	 * @param code 控制器唯一编码
	 * @return
	 * @throws Exception
	 */
	@Override
	public JSONArray findSwitchByCollectorCode(String code) throws Exception {
		if (code == null || code.trim().isEmpty())
			return null;
		List<SwitchWithTimer> switchs = switchDao.findSwitchWithTimerByCollectorCode(entityManager, code);
		JSONArray ja = new JSONArray();
		for (SwitchWithTimer s : switchs) {
			JSONObject jo_switch = JSONObject.fromObject(s);
			ja.add(jo_switch);
		}
		// List<Switch> switchs = switchDao.findByCollectorCode(code);
		// if (switchs == null || switchs.isEmpty())
		// return null;
		// JSONArray ja = new JSONArray();
		// for (Switch s : switchs) {
		// JSONObject jo_switch = JSONObject.fromObject(s);
		// jo_switch.remove("collector");
		// jo_switch.put("addTime",
		// PowerManagerUtil.timestampToString(s.getAddTime()));
		// jo_switch.put("faultTime",
		// PowerManagerUtil.timestampToString(s.getFaultTime()));
		// ja.add(jo_switch);
		// }
		return ja;
	}

	/**
	 * 根据指定fsu获取其所有控制器信息
	 *
	 * @param fsuCode fsu唯一编码
	 * @return
	 * @throws Exception
	 */
	@Override
	public JSONArray findCollectorByFsuCode(String fsuCode) throws Exception {
		if (fsuCode == null || fsuCode.trim().isEmpty())
			return null;
		List<Collector> collectors = collectorDao.findByFsuCode(fsuCode);
		if (collectors == null || collectors.isEmpty())
			return null;
		JSONArray ja = new JSONArray();
		for (Collector c : collectors) {
			JSONObject jo_collector = JSONObject.fromObject(c);
			jo_collector.remove("fsu");
			ja.add(jo_collector);
		}
		return ja;
	}

	@Override
	public JSONArray findCollector1(String userID) throws Exception {
		if (userID == null || userID.trim().isEmpty())
			return null;
		List<UserCollector> ucs = userCollectorDao.findByUserID(userID);
		List<CollectorShare> css = collectorShareDao.findByUserID(userID);
		JSONArray ja_collector = new JSONArray();
		// LocalDateTime now = LocalDateTime.now();
		if (ucs != null && !ucs.isEmpty()) { // 用户自己绑定的集中器
			for (UserCollector uc : ucs) {
				Collector c = uc.getCollector();
				JSONObject jo_collector = JSONObject.fromObject(c);
				jo_collector.remove("fsu");
				jo_collector.put("faultTime", PowerManagerUtil.timestampToString(c.getFaultTime()));
				// jo_collector.put("HBTime",
				// PowerManagerUtil.timestampToString(c.getHBTime()));
				// if (c.getHBTime() != null
				// && (now.minusSeconds(c.getHBFreq() * 2).isBefore(c
				// .getHBTime().toLocalDateTime())))
				// jo_collector.put("online", 1);
				// else
				// jo_collector.put("online", 0);
				jo_collector.put("online", c.getActive());
				jo_collector.put("beShared", 0);
				List<CollectorShare> shares = collectorShareDao.findByCollectorID(c.getCollectorID());
				if (shares == null || shares.isEmpty())
					jo_collector.put("share", 0);
				else {
					JSONArray ja_user = new JSONArray();
					for (CollectorShare cs : shares) {
						User u = cs.getUser();
						JSONObject jo_user = JSONObject.fromObject(u);
						jo_user.remove("password");
						ja_user.add(jo_user);
					}
					jo_collector.put("share", shares.size());
					jo_collector.put("shareUsers", ja_user);
				}
				ja_collector.add(jo_collector);
			}
		}
		if (css != null && !css.isEmpty()) { // 用户被别人分享的集中器
			for (CollectorShare cs : css) {
				Collector c = cs.getCollector();
				JSONObject jo_collector = JSONObject.fromObject(c);
				jo_collector.remove("fsu");
				jo_collector.put("enable", cs.getEnable());
				jo_collector.put("faultTime", PowerManagerUtil.timestampToString(c.getFaultTime()));
				// jo_collector.put("HBTime",
				// PowerManagerUtil.timestampToString(c.getHBTime()));
				// if (c.getHBTime() != null
				// && (now.minusSeconds(c.getHBFreq() * 2).isBefore(c
				// .getHBTime().toLocalDateTime())))
				// jo_collector.put("online", 1);
				// else
				// jo_collector.put("online", 0);
				jo_collector.put("online", c.getActive());
				jo_collector.put("beShared", 1);
				List<UserCollector> ucss = userCollectorDao.findByCollectorID(c.getCollectorID());
				if (ucss == null || ucss.isEmpty())
					continue;
				UserCollector uc = ucss.get(0);
				JSONObject jo_user = JSONObject.fromObject(uc.getUser());
				jo_user.remove("password");
				jo_collector.put("ownerUser", jo_user);
				ja_collector.add(jo_collector);
			}
		}
		return ja_collector;
	}

	/**
	 * 获取用户绑定的所有控制器信息
	 *
	 * @param userID 用户登录id
	 * @return
	 * @throws Exception
	 */
	@Override
	public JSONArray findCollector(String userID) throws Exception {
		if (userID == null || userID.trim().isEmpty())
			return null;
		List<UserCollector> ucs = userCollectorDao.findByUserID(userID);
		if (ucs == null || ucs.isEmpty())
			return null;
		JSONArray ja = new JSONArray();
		// LocalDateTime now = LocalDateTime.now();
		for (UserCollector uc : ucs) {
			Collector c = uc.getCollector();
			JSONObject jo_collector = JSONObject.fromObject(c);
			jo_collector.remove("fsu");
			// jo_collector.put("HBTime",
			// PowerManagerUtil.timestampToString(c.getHBTime()));
			// if (c.getHBTime() != null
			// && (now.minusSeconds(c.getHBFreq() * 2).isBefore(c
			// .getHBTime().toLocalDateTime())))
			// jo_collector.put("online", 1);
			// else
			// jo_collector.put("online", 0);
			jo_collector.put("online", c.getActive());
			ja.add(jo_collector);
		}
		return ja;
	}

	/**
	 * 获取指定断路器所有最新数据
	 *
	 * @param switchID 断路器id
	 * @return
	 * @throws Exception
	 */
	@Override
	public JSONArray findSignalsBySwitchID(String switchID) throws Exception {
		if (switchID == null || switchID.trim().isEmpty())
			return null;
		List<SignalsNew> sns = signalsNewDao.findBySwitch(Integer.valueOf(switchID));
		if (sns == null || sns.isEmpty())
			return null;
		JSONArray ja = new JSONArray();
		for (SignalsNew sn : sns) {
			sn.setSwitchs(null);
			JSONObject jo = JSONObject.fromObject(sn);
			jo.remove("switchs");
			jo.put("time", PowerManagerUtil.timestampToString(sn.getTime()));
			ja.add(jo);
		}
		return ja;
	}

//	/**
//	 * 获取指定断路器的指定信号类型的某一天的平均值(按小时算)
//	 *
//	 * @param switchID      断路器id
//	 * @param signalsTypeID 信号类型id
//	 * @param time          指定的日期,格式yyyy-MM-dd
//	 * @return
//	 * @throws Exception
//	 */
//	@Override
//	public JSONArray getAverageByDay(String switchID, String signalsTypeID, String time) throws Exception {
//		if (switchID == null || switchID.trim().isEmpty() || signalsTypeID == null || signalsTypeID.trim().isEmpty()
//				|| time == null || time.trim().isEmpty())
//			return null;
//		// SignalDao signalDao = new SignalDao();
//		List<Object> objects = signalDao.getAverageByDay(Integer.valueOf(switchID), Short.valueOf(signalsTypeID), time);
//		// signalDao.close();
//		if (objects == null || objects.isEmpty())
//			return null;
//		JSONArray ja = new JSONArray();
//		for (Object obj : objects) {
//			Object[] objs = (Object[]) obj;
//			JSONObject jo = new JSONObject();
//			jo.put("time", objs[0] + ":00");
//			jo.put("value", objs[1]);
//			ja.add(jo);
//		}
//		return ja;
//	}

//	@Override
//	public JSONArray getAverageByTenMinute(String switchID, String signalsTypeID, String time) throws Exception {
//		if (switchID == null || switchID.trim().isEmpty() || signalsTypeID == null || signalsTypeID.trim().isEmpty()
//				|| time == null || time.trim().isEmpty())
//			return null;
//		// SignalDao signalDao = new SignalDao();
//		List<Object> objects = signalDao.getAverageByDayWithTenMinute(Integer.valueOf(switchID),
//				Short.valueOf(signalsTypeID), time);
//		// signalDao.close();
//		if (objects == null || objects.isEmpty())
//			return null;
//		JSONArray ja = new JSONArray();
//		for (Object obj : objects) {
//			Object[] objs = (Object[]) obj;
//			JSONObject jo = new JSONObject();
//			jo.put("time", String.format("%02d", objs[0]) + ":" + objs[1] + "0");
//			jo.put("value", objs[2]);
//			ja.add(jo);
//		}
//		return ja;
//	}

//	/**
//	 * 获取指定断路器的指定信号类型的某一好月的平均值(按天算)
//	 *
//	 * @param switchID      断路器id
//	 * @param signalsTypeID 信号类型id
//	 * @param time          指定时间,格式yyyy-MM
//	 * @return
//	 * @throws Exception
//	 */
//	@Override
//	public JSONArray getAverageByMonth(String switchID, String signalsTypeID, String time) throws Exception {
//		if (switchID == null || switchID.trim().isEmpty() || signalsTypeID == null || signalsTypeID.trim().isEmpty()
//				|| time == null || time.trim().isEmpty())
//			return null;
//		// SignalDao signalDao = new SignalDao();
//		List<Object> objects = signalDao.getAverageByMonth(Integer.valueOf(switchID), Short.valueOf(signalsTypeID),
//				time);
//		// signalDao.close();
//		if (objects == null || objects.isEmpty())
//			return null;
//		JSONArray ja = new JSONArray();
//		for (Object obj : objects) {
//			Object[] objs = (Object[]) obj;
//			JSONObject jo = new JSONObject();
//			jo.put("time", objs[0]);
//			jo.put("value", objs[1]);
//			ja.add(jo);
//		}
//		return ja;
//	}

	/**
	 * 修改指定断路器信息
	 *
	 * @param switchID 断路器id
	 * @param name     要修改的名称
	 * @param iconType 要修改的图片类型
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public WSCode updateSwitch(String userkey, String switchID, String name, String iconType) throws Exception {
		if (userkey == null || userkey.trim().isEmpty())
			return WSCode.WRONG_PARAMETER_FORMAT;
		User user = checkLogin(userkey);
		if (user == null)
			return WSCode.AUTH_FAILED;
		if (switchID == null || switchID.trim().isEmpty())
			return WSCode.WRONG_PARAMETER_FORMAT;
		List<Switch> switchs = switchDao.findBySwitchID(Integer.valueOf(switchID));
		if (switchs == null || switchs.isEmpty())
			return WSCode.WRONG_PARAMETER_VALUE;
		Switch s = switchs.get(0);
		List<UserCollector> ucs = userCollectorDao.findByUsernameAndCollectorID(user.getUsername(),
				s.getCollector().getCollectorID());
		if (ucs == null || ucs.isEmpty())
			return WSCode.WRONG_PARAMETER_VALUE;
		if (name != null && !name.trim().isEmpty())
			s.setName(name);
		if (iconType != null && !iconType.trim().isEmpty())
			s.setIconType(Integer.valueOf(iconType));
		switchDao.saveAndFlush(s);
		return WSCode.SUCCESS;
	}

	/**
	 * 获取所有信号类型
	 *
	 * @return
	 * @throws Exception
	 */
	@Override
	public JSONArray findSignalsType(String type) throws Exception {
		List<Signalstype> signalsTypes = signalsTypeDao.findAll();
		if (signalsTypes == null || signalsTypes.isEmpty())
			return null;
		JSONArray ja = new JSONArray();

		if ("day".equals(type)) {
			for (Signalstype st : signalsTypes) {
				Short id = st.getSignalsTypeID();
				if (id == 10 || id == 8 || id == 4 || id == 1 || id == 11 || id == 9 || id == 5 || id == 6 || id == 7)
					/*
					 * String id = st.getSignalsTypeID(); if (id.equals("ygdl") || id.equals("wgdl")
					 * || id.equals("dy") || id.equals("dl") || id.equals("yggl") ||
					 * id.equals("wggl") || id.equals("glys") || id.equals("pl") || id.equals("wd"))
					 */
					ja.add(JSONObject.fromObject(st));
			}
		} else if ("month".equals(type)) {
			for (Signalstype st : signalsTypes) {
				Short id = st.getSignalsTypeID();
//				String id = st.getSignalsTypeID();
				if (id == 10 || id == 4 || id == 11)
//				if (id.equals("ygdl") || id.equals("dy") || id.equals("yggl"))
					ja.add(JSONObject.fromObject(st));
			}
		} else {
			for (Signalstype st : signalsTypes)
				ja.add(JSONObject.fromObject(st));
		}
		return ja;
	}

	/**
	 * 对指定断路器进行开/关操作
	 *
	 * @param switchID 断路器id
	 * @param cmdData  操作命令,0表示关,其他表示开
	 * @return
	 * @throws Exception
	 */
	@Override
	public JSONObject operateSwitch(String userkey, String switchID, String cmdData) throws Exception {
		if (userkey == null || userkey.trim().isEmpty() || switchID == null || switchID.trim().isEmpty()
				|| cmdData == null || cmdData.trim().isEmpty())
			return null;
		User user = checkLogin(userkey);
		if (user == null)
			return null;
		List<Switch> ss = switchDao.findBySwitchID(Integer.valueOf(switchID));
		if (ss == null || ss.isEmpty())
			return null;
		Switch switchs = ss.get(0);
		List<UserCollector> ucs = userCollectorDao.findByUsernameAndCollectorID(user.getUsername(),
				switchs.getCollector().getCollectorID());
		if (ucs == null || ucs.isEmpty())
			return null;
		Controller controller = VirtualFsuController.switchControl(switchs, Byte.valueOf(cmdData), (byte) 0);
		if (controller == null)
			return null;
		JSONObject jo = new JSONObject();
		jo.put("controllerID", controller.getControllerID());
		return jo;
	}

//	/**
//	 * 获取指定控制器下所有断路器的指定月份的用电总量
//	 *
//	 * @param collectorCode 控制器id
//	 * @param time          时间,格式yyyy-MM
//	 * @return
//	 * @throws Exception
//	 */
//	@Override
//	public JSONArray getTotalPowerByMonth(String collectorCode, String time) throws Exception {
//		if (collectorCode == null || collectorCode.trim().isEmpty() || time == null || time.trim().isEmpty())
//			return null;
//		List<Switch> switchs = switchDao.findByCollectorCode(collectorCode);
//		if (switchs == null || switchs.isEmpty())
//			return null;
//		JSONArray ja = new JSONArray();
//		for (Switch s : switchs) {
//			List<Signal> signals = signalDao.findYGDLBySwitchIDAndTimeWithMonth(s.getSwitchID(), time);
//			if (signals == null || signals.isEmpty())
//				continue;
//			double value = signals.get(0).getValue() - signals.get(signals.size() - 1).getValue();
//			JSONObject jo_s = JSONObject.fromObject(s);
//			jo_s.remove("collector");
//			jo_s.put("value", value);
//			ja.add(jo_s);
//		}
//		return ja;
//	}

//	/**
//	 * 获取指定控制器下所有断路器指定日期的用电总量
//	 *
//	 * @param collectorCode 控制器唯一编码
//	 * @param time          时间格式为yyyy-MM-dd
//	 * @return
//	 * @throws Exception
//	 */
//	@Override
//	public JSONArray getTotalPowerByDay(String collectorCode, String time) throws Exception {
//		if (collectorCode == null || collectorCode.trim().isEmpty() || time == null || time.trim().isEmpty())
//			return null;
//		List<Switch> switchs = switchDao.findByCollectorCode(collectorCode);
//		if (switchs == null || switchs.isEmpty())
//			return null;
//		JSONArray ja = new JSONArray();
//		for (Switch s : switchs) {
//			List<Signal> signals = signalDao.findYGDLBySwitchIDAndTimeWithDay(s.getSwitchID(), time);
//			if (signals == null || signals.isEmpty())
//				continue;
//			double value = signals.get(0).getValue() - signals.get(signals.size() - 1).getValue();
//			JSONObject jo_s = JSONObject.fromObject(s);
//			jo_s.remove("collector");
//			jo_s.put("value", value);
//			ja.add(jo_s);
//		}
//		return ja;
//	}

	/**
	 * 获取指定控制器下所有断路器的指定月份的用电总量
	 *
	 * @param collectorCode 控制器id
	 * @param time          时间,格式yyyy-MM
	 * @return
	 * @throws Exception
	 */
	@Override
	public JSONArray getTotalPowerByMonth(String userID, String collectorCode, String time) throws Exception {
		if (collectorCode == null || collectorCode.trim().isEmpty() || time == null || time.trim().isEmpty())
			return null;
		List<Switch> switchs = switchDao.findByCollectorCode(collectorCode);
		if (switchs == null || switchs.isEmpty())
			return null;
		JSONArray ja = new JSONArray();
		for (Switch s : switchs) {
			double value = 0;
			double price = 0;
			JSONObject jo_s = JSONObject.fromObject(s);
			jo_s.remove("collector");
			jo_s.put("addTime", PowerManagerUtil.timestampToString(s.getAddTime()));
			jo_s.put("value", value);
			jo_s.put("price", price);
			List<SignalHour> shs = signalHourDao.findBySwitchAndTypeAndMonth(s.getSwitchID(), (short) 10, time + "-1",
					time + "-31");
			if (shs == null || shs.isEmpty()) {
				ja.add(jo_s);
				continue;
			}
			for (SignalHour sh : shs) {
				value += sh.getStatistik();
				List<TimeOfUsePricing> toups = timeOfUsePricingDao.findByUserIdAndTimePoint(userID, sh.getHour());
				if (toups == null || toups.isEmpty())
					continue;
				price += sh.getStatistik() * toups.get(0).getPrice();
			}
			jo_s.put("value", value);
			jo_s.put("price", price);
			ja.add(jo_s);
		}
		return ja;
	}

	/**
	 * 获取指定控制器下所有断路器指定日期的用电总量
	 *
	 * @param collectorCode 控制器唯一编码
	 * @param time          时间格式为yyyy-MM-dd
	 * @return
	 * @throws Exception
	 */
	@Override
	public JSONArray getTotalPowerByDay(String userID, String collectorCode, String time) throws Exception {
		if (userID == null || userID.trim().isEmpty() || collectorCode == null || collectorCode.trim().isEmpty()
				|| time == null || time.trim().isEmpty())
			return null;
		List<Switch> switchs = switchDao.findByCollectorCode(collectorCode);
		if (switchs == null || switchs.isEmpty())
			return null;
		JSONArray ja = new JSONArray();
		for (Switch s : switchs) {
			double value = 0;
			double price = 0;
			JSONObject jo_s = JSONObject.fromObject(s);
			jo_s.remove("collector");
			jo_s.put("addTime", PowerManagerUtil.timestampToString(s.getAddTime()));
			jo_s.put("value", value);
			jo_s.put("price", price);
			List<SignalHour> shs = signalHourDao.findBySwitchAndTypeAndDay(s.getSwitchID(), (short) 10,
					Date.valueOf(time));
			if (shs == null || shs.isEmpty()) {
				ja.add(jo_s);
				continue;
			}
			for (SignalHour sh : shs) {
				value += sh.getStatistik();
				List<TimeOfUsePricing> toups = timeOfUsePricingDao.findByUserIdAndTimePoint(userID, sh.getHour());
				if (toups.isEmpty())
					continue;
				else
					price += sh.getStatistik() * toups.get(0).getPrice();
			}
			jo_s.put("value", value);
			jo_s.put("price", price);
			ja.add(jo_s);
		}
		return ja;
	}

	/**
	 * 新增或修改定时控制器信息
	 *
	 * @param timeControllerID 定时控制器记录id,有值的时候表示修改,无值的时候表示新增
	 * @param switchID         断路器id
	 * @param runTime          执行时间,格式hh:mm
	 * @param weekday          是否重复以及具体哪几天重复,0表示不重复
	 * @param state            此记录是否有效,0表示无效,1表示有效
	 * @param cmdData          要执行的操作命令，0表示关,1表示开
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public WSCode addOrUpdateTimeController(String timeControllerID, String switchID, String runTime, String weekday,
			String state, String cmdData, String upload) throws Exception {
		Time orunTime = null;
		if (runTime != null && !runTime.trim().isEmpty())
			orunTime = Time.valueOf(LocalTime.parse(runTime, TIME_FORMATTER));
		Integer oweekday = null;
		if (weekday != null && !weekday.trim().isEmpty())
			oweekday = Integer.valueOf(weekday);
		Byte ostate = null;
		if (state != null && !state.trim().isEmpty())
			ostate = Byte.valueOf(state);
		Byte ocmdData = null;
		if (cmdData != null && !cmdData.trim().isEmpty())
			ocmdData = Byte.valueOf(cmdData);
		Byte oupload = null;
		if (upload != null && !upload.trim().isEmpty())
			oupload = Byte.valueOf(upload);
		if (timeControllerID == null || timeControllerID.trim().isEmpty()) { // 新增定时
			List<Switch> ss = switchDao.findBySwitchID(Integer.valueOf(switchID));
			if (ss == null || ss.isEmpty())
				return WSCode.WRONG_PARAMETER_VALUE;
			Switch switchs = ss.get(0);
			timeControllerImpl.add(switchs, ocmdData, orunTime, ostate, oweekday, oupload);
			return WSCode.SUCCESS;
		} else { // 修改定时
			timeControllerImpl.updateById(Long.valueOf(timeControllerID), ocmdData, orunTime, ostate, oweekday, oupload);

			return WSCode.SUCCESS;
		}
	}

	/**
	 * 获取指定控制器下所有断路器的定时任务信息
	 *
	 * @param collectorID 控制器id
	 * @return
	 * @throws Exception
	 */
	@Override
	public JSONArray findTimeControllerByCollectorID(String collectorID) throws Exception {
		if (collectorID == null || collectorID.trim().isEmpty())
			return null;
		List<Switch> switchs = switchDao.findByCollectorID(Integer.valueOf(collectorID));
		if (switchs == null || switchs.isEmpty())
			return null;
		JSONArray ja = new JSONArray();
		for (Switch s : switchs) {
			JSONObject jo_switch = JSONObject.fromObject(s);
			jo_switch.remove("collector");
			List<TimeController> tcs = timeControllerDao.findBySwitchID(s.getSwitchID());
			if (tcs != null && !tcs.isEmpty()) {
				JSONArray ja_tc = new JSONArray();
				for (TimeController tc : tcs) {
					JSONObject jo_tc = new JSONObject();
					jo_tc.put("timeControllerID", tc.getId());
					jo_tc.put("runTime", tc.getRunTime().toLocalTime().format(TIME_FORMATTER));
					jo_tc.put("weekday", tc.getWeekday());
					jo_tc.put("state", tc.getState());
					jo_tc.put("cmdData", tc.getCmdData());
					jo_tc.put("upload", tc.getUpload());
					ja_tc.add(jo_tc);
				}
				jo_switch.put("timeController", ja_tc);
			}
			ja.add(jo_switch);
		}
		return ja;
	}

	@Override
	public JSONArray findTimeControllerByUser(String userID) throws Exception {
		if (userID == null || userID.trim().isEmpty())
			return null;
		List<TimeController> tcs = timeControllerDao.findByUser(userID);
		if (tcs == null || tcs.isEmpty())
			return null;
		JSONArray ja = new JSONArray();
		for (TimeController tc : tcs) {
			Switch s = tc.getSwitchs();
			JSONObject jo_s = JSONObject.fromObject(s);
			jo_s.put("addTime", PowerManagerUtil.timestampToString(s.getAddTime()));
			jo_s.put("faultTime", PowerManagerUtil.timestampToString(s.getFaultTime()));
			// jo_s.getJSONObject("collector").put(
			// "HBTime",
			// PowerManagerUtil.timestampToString(s.getCollector()
			// .getHBTime()));
			jo_s.getJSONObject("collector").put("faultTime",
					PowerManagerUtil.timestampToString(s.getCollector().getFaultTime()));
			// LocalDateTime now = LocalDateTime.now();
			// if (s.getCollector().getHBTime() != null
			// && (now.minusSeconds(s.getCollector().getHBFreq() * 2)
			// .isBefore(s.getCollector().getHBTime()
			// .toLocalDateTime())))
			// jo_s.getJSONObject("collector").put("online", 1);
			// else
			// jo_s.getJSONObject("collector").put("online", 0);
			jo_s.getJSONObject("collector").put("online", s.getCollector().getActive());
			jo_s.getJSONObject("collector").remove("fsu");
			JSONObject jo_tc = new JSONObject();
			jo_tc.put("timeControllerID", tc.getId());
			jo_tc.put("runTime", tc.getRunTime().toLocalTime().format(TIME_FORMATTER));
			jo_tc.put("weekday", tc.getWeekday());
			jo_tc.put("state", tc.getState());
			jo_tc.put("cmdData", tc.getCmdData());
			jo_tc.put("upload", tc.getUpload());
			jo_s.put("timeController", jo_tc);
			ja.add(jo_s);
		}
		return ja;
	}

	/**
	 * 删除指定定时任务
	 *
	 * @param timeControllerID 定时任务记录id
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public WSCode deleteTimeController(String timeControllerID) throws Exception {
		if (timeControllerID == null || timeControllerID.trim().isEmpty())
			return WSCode.WRONG_PARAMETER_FORMAT;
		timeControllerImpl.deleteById(Long.valueOf(timeControllerID));
		return WSCode.SUCCESS;
	}

	/**
	 * 发送查询阈值命令
	 *
	 * @param type       1表示获取过流阈值,2表示获取过压阈值,3表示获取欠压阈值
	 * @param switchCode 断路器唯一编码
	 * @param index      过压阈值索引(0,1,2,3)
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean sendGetThreadValueCommand(String type, String switchCode, String index) throws Exception {
		if (switchCode == null || switchCode.trim().isEmpty() || type == null || type.trim().isEmpty())
			return false;
		if (Integer.valueOf(type) == 1) {
			boolean flag1 = VirtualFsuController.getSwitchOverCurrentThreadhold(switchCode);
			return flag1;
		} else if (Integer.valueOf(type) == 2) {
			if (index == null || index.trim().isEmpty() || Integer.valueOf(index) < 0 || Integer.valueOf(index) > 3)
				return false;
			boolean flag2 = VirtualFsuController.getSwitchOverVoltageThreadhold(switchCode, Integer.valueOf(index));
			return flag2;
		} else if (Integer.valueOf(type) == 3) {
			boolean flag3 = VirtualFsuController.getSwitchUnderVoltageThreadhold(switchCode);
			return flag3;
		} else if (Integer.valueOf(type) == 4) {
			boolean flag4 = VirtualFsuController.getSwitchEleUpper(switchCode);
			return flag4;
		} else
			return false;
	}

	/**
	 * 发送设置阈值命令
	 *
	 * @param type       1表示设置过流阈值,2表示设置过压阈值,3表示设置欠压阈值
	 * @param switchCode 断路器唯一编码
	 * @param index      过压阈值索引(0,1,2,3)
	 * @param value      阈值
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public boolean sendSetThreadValueCommand(String type, String switchCode, String index, String value)
			throws Exception {
		if (type == null || type.trim().isEmpty() || switchCode == null || switchCode.trim().isEmpty() || value == null
				|| value.trim().isEmpty())
			return false;
		List<Switch> ss = switchDao.findByCode(switchCode);
		if (ss == null || ss.isEmpty())
			return false;
		SwitchParamSetting sps = new SwitchParamSetting();
		sps.setId(0L);
		boolean flag = false;
		if (Integer.valueOf(type) == 1) { // 设置过流阈值
			sps.setParamID(17);
			flag = VirtualFsuController.setSwitchOverCurrentThreadhold(switchCode, Double.valueOf(value));
		} else if (Integer.valueOf(type) == 2 && Integer.valueOf(index) >= 0 && Integer.valueOf(index) < 4) { // 设置过压阈值
			if (Integer.valueOf(index) == 0)
				sps.setParamID(5);
			else if (Integer.valueOf(index) == 1)
				sps.setParamID(6);
			else if (Integer.valueOf(index) == 2)
				sps.setParamID(7);
			else if (Integer.valueOf(index) == 3)
				sps.setParamID(8);
			flag = VirtualFsuController.setSwitchOverVoltageThreadhold(switchCode, Integer.valueOf(index),
					Double.valueOf(value));
		} else if (Integer.valueOf(type) == 3) { // 设置欠压阈值
			sps.setParamID(13);
			flag = VirtualFsuController.setSwitchUnderVoltageThreadhold(switchCode, Double.valueOf(value));
		} else if (Integer.valueOf(type) == 4) { // 设置电量上限
			sps.setParamID(25);
			flag = VirtualFsuController.setSwitchEleUpper(switchCode, Double.valueOf(value));
		}
		sps.setParamValue(value);
		sps.setSwitchs(ss.get(0));
		sps.setTime(Timestamp.valueOf(LocalDateTime.now()));
		switchParamSettingDao.saveAndFlush(sps);
		return flag;
	}

	/**
	 * 获取指定断路器的某种阈值
	 *
	 * @param switchID 断路器id
	 * @param paramID  阈值id
	 * @return
	 * @throws Exception
	 */
	@Override
	public JSONObject findSwitchParamBySwitchIDAndParamID(String switchID, String paramID) throws Exception {
		if (switchID == null || switchID.trim().isEmpty() || paramID == null || paramID.trim().isEmpty())
			return null;
		List<SwitchParam> sps = switchParamDao.findBySwitchIDParamID(Integer.valueOf(switchID),
				Integer.valueOf(paramID));
		if (sps == null || sps.isEmpty())
			return null;
		JSONObject jo = JSONObject.fromObject(sps.get(0));
		jo.remove("switchs");
		return jo;
	}

	/**
	 * 新增或修改指定场景中的断路器开关记录
	 *
	 * @param sceneSwitchID 不传值时为新增,传值时为修改
	 * @param sceneID       场景id
	 * @param switchID      断路器id
	 * @param cmdData       控制命令
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public WSCode addOrUpdateSceneSwitch(String sceneSwitchID, String sceneID, String switchID, String cmdData)
			throws Exception {
		if (sceneID == null || sceneID.trim().isEmpty() || switchID == null || switchID.trim().isEmpty()
				|| cmdData == null || cmdData.trim().isEmpty())
			return WSCode.WRONG_PARAMETER_FORMAT;
		List<Scene> scenes = sceneDao.findBySceneID(Integer.valueOf(sceneID));
		if (scenes == null || scenes.isEmpty())
			return WSCode.WRONG_PARAMETER_VALUE;
		Scene scene = scenes.get(0);
		List<Switch> sss = switchDao.findBySwitchID(Integer.valueOf(switchID));
		if (sss == null || sss.isEmpty())
			return WSCode.WRONG_PARAMETER_VALUE;
		Switch switchs = sss.get(0);
		if (sceneSwitchID == null || sceneSwitchID.trim().isEmpty()) { // 新增
			List<SceneSwitch> ss = sceneSwitchDao.findBySceneIDAndSwitchID(Integer.valueOf(sceneID),
					Integer.valueOf(switchID));
			if (ss != null && !ss.isEmpty())
				return WSCode.EXIST;
			SceneSwitch sceneSwitch = new SceneSwitch();
			sceneSwitch.setId(0);
//			sceneSwitch.setId(UUID.randomUUID().toString().replaceAll("-", ""));
			sceneSwitch.setScene(scene);
			sceneSwitch.setSwitchs(switchs);
			sceneSwitch.setCmdData(Byte.valueOf(cmdData));
			sceneSwitchDao.saveAndFlush(sceneSwitch);
			return WSCode.SUCCESS;
		} else { // 修改
			List<SceneSwitch> sceneSwitchs = sceneSwitchDao.findBySceneSwitchID(sceneSwitchID);
			if (sceneSwitchs == null || sceneSwitchs.isEmpty())
				return WSCode.WRONG_PARAMETER_VALUE;
			SceneSwitch sceneSwitch = sceneSwitchs.get(0);
			List<SceneSwitch> ss = sceneSwitchDao.findBySceneIDAndSwitchID(Integer.valueOf(sceneID),
					Integer.valueOf(switchID));
			if (ss != null && !ss.isEmpty())
				return WSCode.EXIST;
//            List<SceneSwitch> ss2 = sceneSwitchDao.findBySceneIDAndSwitchID(Integer.valueOf(sceneID), Integer.valueOf(switchID));
//            if (ss2 != null && !ss2.isEmpty()) {
//            	SceneSwitch ss = ss2.get(0);
//            	if (ss != null && ss.getScene().getSceneID() == Integer.valueOf(sceneID)
//            			&& ss.getSwitchs().getSwitchID() == Integer.valueOf(switchID)
//            			&& ss.getCmdData() == Byte.valueOf(cmdData))
//            		return WSCode.EXIST;
//            }
			sceneSwitch.setScene(scene);
			sceneSwitch.setSwitchs(switchs);
			sceneSwitch.setCmdData(Byte.valueOf(cmdData));
			sceneSwitchDao.saveAndFlush(sceneSwitch);
			return WSCode.SUCCESS;
		}
	}

	/**
	 * 删除场景记录
	 *
	 * @param sceneID 场景id
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public WSCode deleteScene(String sceneID) throws Exception {
		if (sceneID == null || sceneID.trim().isEmpty())
			return WSCode.WRONG_PARAMETER_FORMAT;
		List<Scene> scenes = sceneDao.findBySceneID(Integer.valueOf(sceneID));
		if (scenes == null || scenes.isEmpty())
			return WSCode.WRONG_PARAMETER_VALUE;
		sceneDao.delete(scenes.get(0));
		return WSCode.SUCCESS;
	}

	/**
	 * 删除场景中断路器记录
	 *
	 * @param sceneSwitchID 记录id
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public WSCode deleteSceneSwitch(String sceneSwitchID) throws Exception {
		if (sceneSwitchID == null || sceneSwitchID.trim().isEmpty())
			return WSCode.WRONG_PARAMETER_FORMAT;
		List<SceneSwitch> sceneSwitchs = sceneSwitchDao.findBySceneSwitchID(sceneSwitchID);
		if (sceneSwitchs == null || sceneSwitchs.isEmpty())
			return WSCode.WRONG_PARAMETER_VALUE;
		sceneSwitchDao.delete(sceneSwitchs.get(0));
		return WSCode.SUCCESS;
	}

	/**
	 * 获取控制器下所有场景信息
	 *
	 * @param userID
	 * @return
	 * @throws Exception
	 */
	@Override
	public JSONArray findSceneByUser(String userID) throws Exception {
		if (userID == null || userID.trim().isEmpty())
			return null;
		List<Scene> scenes = sceneDao.findByUserID(userID);
		if (scenes == null || scenes.isEmpty())
			return null;
		JSONArray ja = new JSONArray();
		for (Scene s : scenes) {
			JSONObject jo_s = JSONObject.fromObject(s);
			jo_s.remove("user");
			List<SceneSwitch> ss = sceneSwitchDao.findBySceneID(s.getSceneID());
			if (ss != null)
				jo_s.put("count", ss.size());
			ja.add(jo_s);
		}
		return ja;
	}

	/**
	 * 获取场景下所有断路器信息
	 *
	 * @param sceneID 场景id
	 * @return
	 * @throws Exception
	 */
	@Override
	public JSONArray findSceneSwitchByScene(String sceneID) throws Exception {
		if (sceneID == null || sceneID.trim().isEmpty())
			return null;
		List<SceneSwitch> sceneSwitchs = sceneSwitchDao.findBySceneID(Integer.valueOf(sceneID));
		if (sceneSwitchs == null || sceneSwitchs.isEmpty())
			return null;
		JSONArray ja = new JSONArray();
		for (SceneSwitch ss : sceneSwitchs) {
			JSONObject jo_ss = JSONObject.fromObject(ss);
			jo_ss.remove("scene");
			JSONObject jo_switch = jo_ss.getJSONObject("switchs");
			JSONObject jo_collector = jo_switch.getJSONObject("collector");
			jo_collector.remove("fsu");
			jo_ss.put("switchs", jo_switch);
			ja.add(jo_ss);
		}
		return ja;
	}

	/**
	 * 执行场景
	 *
	 * @param sceneID 场景id
	 * @return
	 * @throws Exception
	 */
	@Override
	public WSCode doScene(String sceneID) throws Exception {
		if (sceneID == null || sceneID.trim().isEmpty())
			return WSCode.WRONG_PARAMETER_FORMAT;
		List<Scene> scenes = sceneDao.findBySceneID(Integer.valueOf(sceneID));
		if (scenes == null || scenes.isEmpty())
			return WSCode.WRONG_PARAMETER_VALUE;
		Controller controller = VirtualFsuController.sceneControl(scenes.get(0), (byte) 0);
		if (controller == null)
			return WSCode.FAILED;
		else
			return WSCode.SUCCESS;
	}

	/**
	 * 执行场景
	 *
	 * @param sceneID 场景id
	 * @return
	 * @throws Exception
	 */
	/*
	 * public WSCode doScene1(String sceneID) throws Exception { if (sceneID == null
	 * || sceneID.trim().isEmpty()) return WSCode.WRONG_PARAMETER_FORMAT; Scene
	 * scene = sceneDao.findBySceneID(sceneID); if (scene == null) return
	 * WSCode.WRONG_PARAMETER_VALUE; List<SceneSwitch> sceneSwitchs =
	 * sceneSwitchDao.findBySceneID(sceneID); if (sceneSwitchs == null ||
	 * sceneSwitchs.isEmpty()) return WSCode.EMPTY_RESULT; for (SceneSwitch ss :
	 * sceneSwitchs) { Switch s = ss.getSwitchs(); s.setState(ss.getCmdData());
	 * switchDao.addOrUpdateSwitch(s); Controller controller = new Controller();
	 * controller.setControllerID(UUID.randomUUID().toString() .replaceAll("-",
	 * "")); // controller.setSwitch(s); controller.setCmdData(ss.getCmdData());
	 * controller.setGenTime(new Timestamp(System.currentTimeMillis()));
	 * controller.setSource(2); controllerDao.addOrUpdateController(controller); }
	 * return WSCode.SUCCESS; }
	 */

	/**
	 * 查询操作结果
	 *
	 * @param controllerID 操作记录id
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean getResultOfController(String controllerID) throws Exception {
		if (controllerID == null || controllerID.trim().isEmpty())
			return false;
		List<Controller> controllers = controllerDao.findByControllerID(Long.valueOf(controllerID));
		if (controllers == null || controllers.isEmpty())
			return false;
		if ("2".equals(controllers.get(0).getRunCode()))
			return true;
		return false;
	}

	/**
	 * 新增/修改场景信息
	 *
	 * @param scenes 指定格式的json字符串,包含所有场景信息
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public WSCode addOrUpdateScene(String scenes) throws Exception {
		if (scenes == null || scenes.trim().isEmpty())
			return WSCode.WRONG_PARAMETER_FORMAT;
		JSONObject jo = JSONObject.fromObject(scenes);
		User user = checkLogin(jo.getString("userkey"));
		if (user == null)
			return WSCode.AUTH_FAILED;
		String sceneID = jo.getString("sceneID");
		Scene scene = null;
		List<Scene> ss;
		if (sceneID != null && !sceneID.trim().isEmpty()) { // 修改。
			ss = sceneDao.findBySceneID(Integer.valueOf(sceneID));
			if (ss.isEmpty()) // 指定的记录不存在。
				return WSCode.UNEXIST;
			scene = ss.get(0);
			// 删除该Scene的原有SceneSwitch记录。
			sceneSwitchDao.deleteAllSwitchsOfScene(Integer.valueOf(sceneID));
		} else { // 新增。
			scene = new Scene();
			scene.setSceneID(0);
		}
		// 检测是否有同名Secne。
		ss = sceneDao.findByUserIDAndName(user.getId(), jo.getString("name"));
		if (!ss.isEmpty()) { // 存在新名字的Scene
			if (scene.getSceneID().intValue() == 0) // 新增Scene名字重复
				return WSCode.EXIST;
			else if (scene.getSceneID().intValue() != ss.get(0).getSceneID().intValue()) // 修改名字重复
				return WSCode.EXIST;
		}
		scene.setUser(user);
		scene.setName(jo.getString("name"));
		scene.setIconType(jo.getInt("iconType"));
		scene = sceneDao.saveAndFlush(scene);
		JSONArray ja = jo.getJSONArray("switchs");
		List<Switch> swts;
		JSONObject jo_switch;
		for (int i = 0; i < ja.size(); i++) {
			jo_switch = ja.getJSONObject(i);
			swts = switchDao.findBySwitchID(Integer.valueOf(jo_switch.getString("switchID")));
			if (swts.isEmpty())
				return WSCode.WRONG_PARAMETER_VALUE;
			SceneSwitch sceneSwitch = new SceneSwitch();
			sceneSwitch.setId(0);
//			sceneSwitch.setId(UUID.randomUUID().toString().replaceAll("-", ""));
			sceneSwitch.setScene(scene);
			sceneSwitch.setSwitchs(swts.get(0));
			sceneSwitch.setCmdData((byte) jo_switch.getInt("cmdData"));
			sceneSwitchDao.saveAndFlush(sceneSwitch);
		}
		return WSCode.SUCCESS;
	}

	/**
	 * 更新集中器的波特率,数据上报频率,数据上报变化幅度
	 *
	 * @param collectorID 集中器id
	 * @param name        名称
	 * @param baud        波特率
	 * @param freq        上报频率
	 * @param ranges      变化幅度
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public WSCode updateCollectorParam(String collectorID, String name, String baud, String freq, String ranges,
			String HBFreq) throws Exception {
		if (collectorID == null || collectorID.trim().isEmpty())
			return WSCode.WRONG_PARAMETER_FORMAT;
		List<Collector> collectors = collectorDao.findByCollectorID(Integer.valueOf(collectorID));
		if (collectors == null || collectors.isEmpty())
			return WSCode.WRONG_PARAMETER_VALUE;
		Collector collector = collectors.get(0);
		if (name != null && !name.trim().isEmpty())
			collector.setName(name);
		if (baud != null && !baud.trim().isEmpty())
			collector.setBaud(Integer.valueOf(baud));
		if (freq != null && !freq.trim().isEmpty())
			collector.setFreq(Integer.valueOf(freq));
		if (ranges != null && !ranges.trim().isEmpty())
			collector.setRanges(Integer.valueOf(ranges));
		if (HBFreq != null && !HBFreq.trim().isEmpty())
			collector.setHBFreq(Integer.valueOf(HBFreq));
		// collectorDao.addOrUpdateCollector(collector);
		// VirtualFsuController.setCollectorConfig(collector);
		collectorService.addOrUpdate(collector);
		return WSCode.SUCCESS;
	}

	/**
	 * 新增/修改断路器信息
	 *
	 * @param switchID    断路器id,新增时不传,修改时传
	 * @param collectorID 集中器id
	 * @param name        名称
	 * @param switchCode  唯一编码
	 * @param freq        频率
	 * @param iconType    图标
	 * @param state       开关状态
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public WSCode addOrUpdateSwitch(String userkey, String switchID, String collectorID, String name, String switchCode,
			String freq, String iconType, String state) throws Exception {
		if (userkey == null || userkey.trim().isEmpty())
			return WSCode.WRONG_PARAMETER_FORMAT;
		User user = checkLogin(userkey);
		if (user == null)
			return WSCode.AUTH_FAILED;
		if (switchID == null || switchID.trim().isEmpty()) { // 新增
			if (collectorID == null || collectorID.trim().isEmpty() || name == null || name.trim().isEmpty()
					|| switchCode == null || switchCode.trim().isEmpty() || iconType == null
					|| iconType.trim().isEmpty() || state == null || state.trim().isEmpty())
				return WSCode.WRONG_PARAMETER_FORMAT;
			List<UserCollector> ucs = userCollectorDao.findByUsernameAndCollectorID(user.getUsername(),
					Integer.valueOf(collectorID));
			if (ucs == null || ucs.isEmpty())
				return WSCode.WRONG_PARAMETER_VALUE;
			List<Collector> cs = collectorDao.findByCollectorID(Integer.valueOf(collectorID));
			if (cs == null || cs.isEmpty())
				return WSCode.WRONG_PARAMETER_VALUE;
			List<Switch> ss = switchDao.findByCode(switchCode);
			if (ss != null && !ss.isEmpty()/* && ss.get(0).getCollector() != null */)
				return WSCode.EXIST;
			Switch switchs;
			switchs = new Switch();
			// switchs.setSwitchID(UUID.randomUUID().toString()
			// .replaceAll("-", "").toUpperCase());
			switchs.setSwitchID(0);
			switchs.setCollector(cs.get(0));
			switchs.setName(name);
			switchs.setCode(switchCode);
			switchs.setFrequency(Double.valueOf(freq));
			switchs.setIconType(Integer.valueOf(iconType));
			// switchs.setState(Integer.valueOf(state));
			switchService.addOrUpdate(switchs);
			// switchDao.addOrUpdateSwitch(switchs);
			return WSCode.SUCCESS;
		} else { // 修改
			List<Switch> ss = switchDao.findBySwitchID(Integer.valueOf(switchID));
			if (ss == null || ss.isEmpty())
				return WSCode.WRONG_PARAMETER_VALUE;
			List<UserCollector> ucs = userCollectorDao.findByUsernameAndCollectorID(user.getUsername(),
					ss.get(0).getCollector().getCollectorID());
			if (ucs == null || ucs.isEmpty())
				return WSCode.WRONG_PARAMETER_VALUE;
			Switch s = ss.get(0);
			if (name != null && !name.trim().isEmpty())
				s.setName(name);
			if (freq != null && !freq.trim().isEmpty())
				s.setFrequency(Double.valueOf(freq));
			if (iconType != null && iconType.trim().isEmpty())
				s.setIconType(Integer.valueOf(iconType));
			// if (state != null && !state.trim().isEmpty())
			// s.setState(Integer.valueOf(state));
			switchDao.saveAndFlush(s);
			return WSCode.SUCCESS;
		}
	}

	/**
	 * 删除断路器
	 *
	 * @param switchID 断路器id
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public WSCode deleteSwitch(String switchID) throws Exception {
		if (switchID == null || switchID.trim().isEmpty())
			return WSCode.WRONG_PARAMETER_FORMAT;
		List<Switch> ss = switchDao.findBySwitchID(Integer.valueOf(switchID));
		if (ss == null || ss.isEmpty())
			return WSCode.WRONG_PARAMETER_VALUE;
		switchService.delete(switchID);
		// switchDao.deleteSwitch(s);
		return WSCode.SUCCESS;
	}

	/**
	 * 根据集中器id获取集中器信息
	 *
	 * @param collectorID 集中器id
	 * @return
	 * @throws Exception
	 */
	@Override
	public JSONObject findCollectorByCollectorID(String collectorID) throws Exception {
		if (collectorID == null || collectorID.trim().isEmpty())
			return null;
		List<Collector> collectors = collectorDao.findByCollectorID(Integer.valueOf(collectorID));
		if (collectors == null || collectors.isEmpty())
			return null;
		Collector collector = collectors.get(0);
		JSONObject jo = JSONObject.fromObject(collector);
		jo.remove("fsu");
		// jo.put("HBTime",
		// PowerManagerUtil.timestampToString(collector.getHBTime()));
		// LocalDateTime now = LocalDateTime.now();
		// if (collector.getHBTime() != null
		// && (now.minusSeconds(collector.getHBFreq() * 2)
		// .isBefore(collector.getHBTime().toLocalDateTime())))
		// jo.put("online", 1);
		// else
		// jo.put("online", 0);
		jo.put("online", collector.getActive());
		return jo;
	}

	/**
	 * 刷新获取断路器最新数据
	 *
	 * @param switchCode 断路器唯一编码
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean refreshSwitchData(String switchCode) throws Exception {
		if (switchCode == null || switchCode.trim().isEmpty())
			return false;
		return VirtualFsuController.refreshSwitchData(switchCode);
	}

	@Override
	public JSONArray sendRefreshCollector(String collectorID) throws Exception {
		if (collectorID == null || collectorID.trim().isEmpty())
			return null;
		JSONArray ja = new JSONArray();
		String[] ids = collectorID.split(",");
		for (int i = 0; i < ids.length; i++) {
			List<Collector> cs = collectorDao.findByCollectorID(Integer.valueOf(ids[i]));
			if (cs == null || cs.isEmpty())
				continue;
			JSONObject jo = new JSONObject();
			jo.put("collector", ids[i]);
			boolean flag = VirtualFsuController.getCollectorFault(cs.get(0));
			if (flag)
				jo.put("state", 1);
			else
				jo.put("state", 0);
			ja.add(jo);
		}
		return ja;
	}

	@Override
	public JSONArray sendRefreshSwitch(String switchIDs) throws Exception {
		if (switchIDs == null || switchIDs.trim().isEmpty())
			return null;
		JSONArray ja = new JSONArray();
		String[] ids = switchIDs.split(",");
		for (int i = 0; i < ids.length; i++) {
			List<Switch> ss = switchDao.findBySwitchID(Integer.valueOf(ids[i]));
			if (ss == null || ss.isEmpty())
				continue;
			JSONObject jo = new JSONObject();
			jo.put("switchID", ss.get(0).getSwitchID());
			// boolean result =
			// VirtualFsuController.getSwitchFault(s.getCode());
			boolean result = VirtualFsuController.getSwitchFault(ss.get(0));
			if (result)
				jo.put("state", 1);
			else
				jo.put("state", 0);
			ja.add(jo);
		}
		return ja;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public boolean addOrUpdatePrice(String userkey, String prices) throws Exception {
		if (userkey == null || userkey.trim().isEmpty() || prices == null || prices.trim().isEmpty())
			return false;
		User u = checkLogin(userkey);
		if (u == null)
			return false;
		String[] price = prices.split(";");
		for (int i = 0; i < price.length; i++) {
			String[] toups = price[i].split(",");
			TimeOfUsePricing toup;
			List<TimeOfUsePricing> toupss = timeOfUsePricingDao.findByUserIdAndTimePoint(u.getId(),
					Integer.valueOf(toups[0]));
			if (toupss == null || toupss.isEmpty()) {
				toup = new TimeOfUsePricing();
				toup.setId(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
				toup.setUser(u);
				toup.setTimePoint(Integer.valueOf(toups[0]));
				toup.setPrice(Double.valueOf(toups[1]));
				timeOfUsePricingDao.saveAndFlush(toup);
			} else {
				toup = toupss.get(0);
				if (toup.getPrice() != Double.valueOf(toups[1])) {
					toup.setPrice(Double.valueOf(toups[1]));
					timeOfUsePricingDao.saveAndFlush(toup);
				}
			}
		}
		return true;
	}

	@Override
	public JSONArray getPowerPrice(String userID) throws Exception {
		if (userID == null || userID.trim().isEmpty())
			return null;
		List<TimeOfUsePricing> toups = timeOfUsePricingDao.findByUserId(userID);
		if (toups == null || toups.isEmpty())
			return null;
		JSONArray ja = new JSONArray();
		for (TimeOfUsePricing toup : toups) {
			JSONObject jo = JSONObject.fromObject(toup);
			jo.remove("user");
			ja.add(jo);
		}
		return ja;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public boolean updateUserMsg(String userID, String nickName, String headImg, String phone, String oldHeadImg,
			String host_user, String prefix_user) throws Exception {
		if ((nickName == null || nickName.trim().isEmpty()) && (headImg == null || headImg.trim().isEmpty())
				&& (phone == null || phone.trim().isEmpty()))
			return false;
		List<User> users = userDao.findByIds(userID);
		if (users == null || users.isEmpty())
			return false;
		User user = users.get(0);
		if (nickName != null)
			user.setNickName(nickName);
		if (headImg != null) {
			if (user.getHeadImg() == null || user.getHeadImg().trim().isEmpty())
				user.setHeadImg(headImg);
			else {
				String fileName = user.getHeadImg().split(host_user)[1];
				File f = new File(prefix_user + fileName);
				if (f.exists() && f.isFile())
					f.delete();
				user.setHeadImg(headImg);
			}
		}
		if (phone != null)
			user.setPhone(phone);
		userDao.saveAndFlush(user);
		return true;
	}

	@Override
	public JSONObject uploadImg(String prefix_user, MultipartFile photo, String host_user) throws Exception {
		if (photo == null)
			return null;
		String fileName = UUID.randomUUID().toString().replaceAll("-", "") + ".jpg";
		String path = prefix_user + fileName;
		File f = new File(path);
		photo.transferTo(f);
//		FileUtils.copyFile(photo, f);
		JSONObject jo = new JSONObject();
		jo.put("headImg", host_user + fileName);
		return jo;
	}

	@Override
	public JSONObject getUserMsg(String userID) throws Exception {
		if (userID == null || userID.trim().isEmpty())
			return null;
		List<User> users = userDao.findByIds(userID);
		if (users == null || users.isEmpty())
			return null;
		JSONObject jo = JSONObject.fromObject(users.get(0));
		return jo;
	}

	private static final DateTimeFormatter TOT = DateTimeFormatter.ofPattern("yyyy-M-d");

	@Override
	public JSONArray getSignalByDay(String time, String switchID, String signalsTypeID) throws Exception {
		if (time == null || time.trim().isEmpty() || switchID == null || switchID.trim().isEmpty()
				|| signalsTypeID == null || signalsTypeID.trim().isEmpty())
			return null;
		if (Signalstype.YGDL.equals(signalsTypeID) || Signalstype.WGDL.equals(signalsTypeID)) {
//			if ("ygdl".equals(signalsTypeID) || "wgdl".equals(signalsTypeID)) {
			List<SignalHour> shs = signalHourDao.findBySwitchAndTypeAndDay(Integer.valueOf(switchID),
					Short.valueOf(signalsTypeID), Date.valueOf(time));
			if (shs == null || shs.isEmpty())
				return null;
			JSONArray ja = new JSONArray();
			for (SignalHour sh : shs) {
				JSONObject jo = new JSONObject();
				jo.put("time", String.format("%02d", sh.getHour()) + ":00");
				jo.put("value", sh.getStatistik());
				ja.add(jo);
			}
			return ja;
		} else {
			List<ZData> zdatas = zDataDao.findBySwitchAndSignalsTypeByDay(entityManager,
					ZDataDao.getZDataTableName(LocalDate.parse(time, TOT).getDayOfYear()), Integer.valueOf(switchID),
					Short.valueOf(signalsTypeID), LocalDate.parse(time, TOT).getYear());
			if (zdatas == null || zdatas.isEmpty())
				return null;
			JSONArray ja = new JSONArray();
			LocalTime lt;
			for (ZData z : zdatas) {
				JSONObject jo = new JSONObject();
				lt = z.getTime().toLocalTime();
				jo.put("time", String.format("%02d", lt.getHour()) + ":" + String.format("%02d", lt.getMinute()));
				jo.put("value", z.getValue());
				ja.add(jo);
			}
//			List<Signal> signals = signalDao.findBySwitchAndSignalsTypeByDay(Integer.valueOf(switchID), Short.valueOf(signalsTypeID), time);
//			if (signals == null || signals.isEmpty())
//				return null;
//			JSONArray ja = new JSONArray();
//			for (Signal s : signals) {
//				JSONObject jo = new JSONObject();
//				jo.put("time", String.format("%02d", s.getTime().getHours()) + ":"
//						+ String.format("%02d", s.getTime().getMinutes()));
//				jo.put("value", s.getValue());
//				ja.add(jo);
//			}
			return ja;
		}
	}

//	@Override
//	@SuppressWarnings("deprecation")
//	public JSONArray getSignalByMonth(String time, String switchID, String signalsTypeID) throws Exception {
//		if (time == null || time.trim().isEmpty() || switchID == null || switchID.trim().isEmpty()
//				|| signalsTypeID == null || signalsTypeID.trim().isEmpty())
//			return null;
////		if (signalsTypeID.equals("ygdl") || signalsTypeID.equals("wgdl")) {
//		if (signalsTypeID.equals(Signalstype.YGDL) || signalsTypeID.equals(Signalstype.WGDL)) {
//			List<SignalHour> shs = signalHourDao.findBySwitchAndTypeAndMonth(Integer.valueOf(switchID),
//					Short.valueOf(signalsTypeID), time + "-1", time + "-31");
//			// if (shs == null || shs.isEmpty())
//			// return null;
//			JSONArray ja = new JSONArray();
//			for (int i = 1; i <= PowerManagerUtil.getDaysFromTime(time); i++) {
//				JSONObject jo = new JSONObject();
//				jo.put("time", String.format("%02d", i));
//				jo.put("value", 0);
//				ja.add(jo);
//			}
//			if (shs == null || shs.isEmpty())
//				return ja;
//			for (SignalHour sh : shs) {
//				int day = sh.getTime().getDate();
//				JSONObject jo = ja.getJSONObject(day - 1);
//				double value = jo.getDouble("value");
//				jo.put("value", value + sh.getStatistik());
//			}
//			return ja;
//		} else {
////			System.out.println(time +"-01 00:00:00");
////			System.out.println(time +"-31 23:59:59");
//			List<Object> objects = signalDao.getAverageByMonth(Integer.valueOf(switchID), Short.valueOf(signalsTypeID),
//					time);
//			if (objects == null || objects.isEmpty())
//				return null;
//			JSONArray ja = new JSONArray();
//			for (Object obj : objects) {
//				Object[] objs = (Object[]) obj;
//				JSONObject jo = new JSONObject();
//				jo.put("time", objs[0]);
//				jo.put("value", objs[1]);
//				ja.add(jo);
//			}
//			return ja;
//		}
//	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public boolean setSwitchSequence(String collectorID, String switchIDs) throws Exception {
		if (collectorID == null || collectorID.trim().isEmpty() || switchIDs == null || switchIDs.trim().isEmpty())
			return false;
		List<Collector> collectors = collectorDao.findByCollectorID(Integer.valueOf(collectorID));
		if (collectors == null || collectors.isEmpty())
			return false;
		String[] switchs = switchIDs.split(",");
		if (switchs == null || switchs.length < 1)
			return false;
		for (int i = 0; i < switchs.length; i++) {
			List<Switch> ss = switchDao.findBySwitchID(Integer.valueOf(switchs[i]));
			if (ss == null || ss.isEmpty())
				continue;
			Switch s = ss.get(0);
			s.setSequence(i);
			switchDao.saveAndFlush(s);
		}
		return true;
	}

	@Override
	public JSONArray getSignalByTime(String switchID, String time, String signalsTypeID, String type) throws Exception {
		if (switchID == null || switchID.trim().isEmpty() || time == null || time.trim().isEmpty()
				|| signalsTypeID == null || signalsTypeID.trim().isEmpty() || type == null || type.trim().isEmpty())
			return null;
		if (type.equals("month")) { // 时间格式为yyyy-MM
			if (signalsTypeID.equals(Signalstype.YGDL) || signalsTypeID.equals(Signalstype.WGDL)) {
//				if (signalsTypeID.equals("ygdl")) {
				// YGDL和WGDL为每小时计算值
				// 返回该月的每天求和值
				List<Object> values = signalHourDao.getYgdlAndWgdlByMonth(Integer.valueOf(switchID),
						Short.valueOf(signalsTypeID), time + "-1", time + "-31");
				if (values == null || values.isEmpty())
					return null;
				JSONArray ja = new JSONArray();
				for (Object o : values) {
					Object[] os = (Object[]) o;
					JSONObject jo = new JSONObject();
					jo.put("time", os[1]);
					jo.put("value", os[0]);
					ja.add(jo);
				}
				return ja;
			} else {
				// 其它信号类型返回该月的每日最大最小值
//            	if (signalsTypeID.equals(Signalstype.DY)
//		} else if (signalsTypeID.equals("dy")
//                    || signalsTypeID.equals(Signalstype.YGGL)) {
//			|| signalsTypeID.equals("yggl")) {
				List<ApexDay> ads = apexDayDao.findByMonth(Integer.valueOf(switchID), Short.valueOf(signalsTypeID),
						time + "-1", time + "-31");
				if (ads == null)
					return null;
				JSONArray ja = new JSONArray();
				for (ApexDay ad : ads) {
					JSONObject jo = new JSONObject();
					jo.put("time", ad.getTime().toString());
					jo.put("max", ad.getMaxDay());
					jo.put("min", ad.getMinDay());
					ja.add(jo);
				}
				return ja;
			}
			// return null;
		} else if (type.equals("year")) { // 时间格式为yyyy
			if (signalsTypeID.equals(Signalstype.YGDL) || signalsTypeID.equals(Signalstype.WGDL)) {
//				if (signalsTypeID.equals("ygdl")) {
				// YGDL和WGDL为每小时计算值
				// 返回该年的每月求和值
				List<Object> values = signalHourDao.getYgdlAndWgdlByYear(Integer.valueOf(switchID),
						Short.valueOf(signalsTypeID), time + "-1-1", time + "-12-31");
				if (values == null || values.isEmpty())
					return null;
				JSONArray ja = new JSONArray();
				for (Object o : values) {
					Object[] os = (Object[]) o;
					JSONObject jo = new JSONObject();
					jo.put("time", os[1]);
					jo.put("value", os[0]);
					ja.add(jo);
				}
				return ja;
			} else {
				// 其它信号类型返回该年每月最大最小值
//			} else if (signalsTypeID.equals("dy")
//            } else if (signalsTypeID.equals(Signalstype.DY)
//					|| signalsTypeID.equals("yggl")) {
//                    || signalsTypeID.equals(Signalstype.YGGL)) {
				List<ApexMonth> ams = apexMonthDao.findByYear(Integer.valueOf(switchID), Short.valueOf(signalsTypeID),
						Integer.valueOf(time));
				if (ams == null || ams.isEmpty())
					return null;
				JSONArray ja = new JSONArray();
				for (ApexMonth am : ams) {
					JSONObject jo = new JSONObject();
					jo.put("time", am.getTimeYear() + "-" + am.getTimeMonth());
					jo.put("max", am.getMaxMonth());
					jo.put("min", am.getMinMonth());
					ja.add(jo);
				}
				return ja;
			}
			// return null;
		}
		return null;
	}

	@Override
	@Transactional(transactionManager = "TransactionManager_1")
	public JSONArray getAllSwitchMsg(String userID) throws Exception {
		if (userID == null || userID.trim().isEmpty())
			return null;
		List<UserCollector> ucs = userCollectorDao.findByUserID(userID);
		if (ucs == null || ucs.isEmpty())
			return null;
		JSONArray ja_c = new JSONArray();
		for (UserCollector uc : ucs) {
			JSONObject jo_collector = new JSONObject();
			Collector c = uc.getCollector();
			// c.setFsu(null);
			JSONObject jo_c = JSONObject.fromObject(c);
			// jo_c.put("HBTime",
			// PowerManagerUtil.timestampToString(c.getHBTime()));
			jo_c.put("faultTime", PowerManagerUtil.timestampToString(c.getFaultTime()));
			jo_c.remove("fsu");
			// LocalDateTime now1 = LocalDateTime.now();
			// if (c.getHBTime() != null
			// && (now1.minusSeconds(c.getHBFreq() * 2).isBefore(c
			// .getHBTime().toLocalDateTime())))
			// jo_c.put("online", 1);
			// else
			// jo_c.put("online", 0);
			jo_c.put("online", c.getActive());
			List<Switch> ss = switchDao.findByCollectorID(c.getCollectorID());
			if (ss == null || ss.isEmpty()) {
				jo_collector.put("collector", jo_c);
				ja_c.add(jo_collector);
				continue;
			} else {
				JSONArray ja_s = new JSONArray();
				for (Switch s : ss) {
					JSONObject jo_s = JSONObject.fromObject(s);
					jo_s.put("addTime", PowerManagerUtil.timestampToString(s.getAddTime()));
					jo_s.put("faultTime", PowerManagerUtil.timestampToString(s.getFaultTime()));
					jo_s.remove("collector");
					LocalDate ld = LocalDate.now();
					int y = ld.getYear(), m = ld.getMonthValue(), d = ld.getDayOfMonth();
					double today = signalHourDao.getYgdlAndWgdlByTime(entityManager, s.getSwitchID(),
							y + "-" + m + "-" + d, y + "-" + m + "-" + d, "day");
					double month = signalHourDao.getYgdlAndWgdlByTime(entityManager, s.getSwitchID(),
							y + "-" + m + "-1", y + "-" + m + "-31", "month");
					double lastMonth = signalHourDao.getYgdlAndWgdlByTime(entityManager, s.getSwitchID(),
							(m == 1 ? y - 1 : y) + "-" + (m == 1 ? 12 : m - 1) + "-1",
							(m == 1 ? y - 1 : y) + "-" + (m == 1 ? 12 : m - 1) + "-31", "month");
					double year = signalHourDao.getYgdlAndWgdlByTime(entityManager, s.getSwitchID(), y + "-1-1",
							y + "-12-31", "year");
					double lastMonthYear = signalHourDao.getYgdlAndWgdlByTime(entityManager, s.getSwitchID(),
							(y - 1) + "-" + m + "-1", (y - 1) + "-" + m + "-31", "month");
					NumberFormat nf = NumberFormat.getInstance();
					nf.setMaximumFractionDigits(2);
					jo_s.put("today", today);
					jo_s.put("month", month);
					jo_s.put("lastMonth", lastMonth);
					jo_s.put("year", year);
					jo_s.put("tbjn", 0);
					jo_s.put("hbjn", 0);
					if (lastMonthYear > 0)
						jo_s.put("tbjn", nf.format((month - lastMonthYear) / lastMonthYear * 100) + "%");
					if (lastMonth > 0)
						jo_s.put("hbjn", nf.format((month - lastMonth) / lastMonth * 100) + "%");
					ja_s.add(jo_s);
				}
				// jo_c.put("switchs", ja_s);
				// jo_collector.put("collector", jo_c);
				jo_collector.put("collector", jo_c);
				jo_collector.put("switchs", ja_s);
				ja_c.add(jo_collector);
				// ja_c.add(jo_c);
			}
		}
		return ja_c;
	}

	@Override
	public JSONArray getAllEE(String userID) throws Exception {
		if (userID == null || userID.trim().isEmpty())
			return null;
		List<UserCollector> ucs = userCollectorDao.findByUserID(userID);
		if (ucs == null || ucs.isEmpty())
			return null;
		JSONArray ja = new JSONArray();
		for (UserCollector uc : ucs) {
			JSONObject jo_c = JSONObject.fromObject(uc.getCollector());
			// jo_c.put("HBTime", PowerManagerUtil.timestampToString(uc
			// .getCollector().getHBTime()));
			jo_c.remove("fsu");
			List<Switch> ss = switchDao.findByCollectorID(uc.getCollector().getCollectorID());
			if (ss == null || ss.isEmpty())
				continue;
			JSONArray ja_ss = new JSONArray();
			for (Switch s : ss) {
				JSONObject jo_s = JSONObject.fromObject(s);
				jo_s.put("addTime", PowerManagerUtil.timestampToString(s.getAddTime()));
				jo_s.remove("collector");
				List<ElectricalEquipment> ees = electricalEquipmentDao.findBySwitchID(s.getSwitchID());
				if (ees == null || ees.isEmpty())
					continue;
				JSONArray ja_ees = new JSONArray();
				for (ElectricalEquipment ee : ees) {
					JSONObject jo = JSONObject.fromObject(ee);
					jo.remove("switchs");
					jo.put("addTime", PowerManagerUtil.timestampToString(ee.getAddTime()));
					ja_ees.add(jo);
				}
				jo_s.put("ees", ja_ees);
				ja_ss.add(jo_s);
			}
			jo_c.put("switchs", ja_ss);
			ja.add(jo_c);
		}
		return ja;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public boolean addOrUpdateEE(String id, String switchID, String name, Double gonglv) throws Exception {
		if (switchID == null || switchID.trim().isEmpty() || name == null || name.trim().isEmpty() || gonglv == null)
			return false;
		List<Switch> ss = switchDao.findBySwitchID(Integer.valueOf(switchID));
		if (ss == null || ss.isEmpty())
			return false;
		ElectricalEquipment ee;
		if (id == null || id.trim().isEmpty()) { // 新增
			ee = new ElectricalEquipment();
			ee.setId(UUID.randomUUID().toString().replaceAll("-", ""));
			ee.setAddTime(Timestamp.from(Instant.now()));
		} else {
			List<ElectricalEquipment> ees = electricalEquipmentDao.findByIds(id);
			if (ees == null || ees.isEmpty())
				return false;
			ee = ees.get(0);
		}
		ee.setSwitchs(ss.get(0));
		ee.setName(name);
		ee.setGonglv(gonglv);
		electricalEquipmentDao.saveAndFlush(ee);
		return true;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public boolean deleteEE(String ids) throws Exception {
		if (ids == null || ids.trim().isEmpty())
			return false;
		String[] id = ids.split(",");
		for (String electricalEquipmentID : id) {
			List<ElectricalEquipment> ees = electricalEquipmentDao.findByIds(electricalEquipmentID);
			if (ees != null && !ees.isEmpty())
				electricalEquipmentDao.delete(ees.get(0));
		}
		return true;
	}

	@Override
	public JSONObject getCode(String phone) throws Exception {
		if (phone == null || phone.trim().isEmpty())
			return null;
		StringBuffer code = new StringBuffer();
		for (int i = 0; i < 4; i++) {
			code.append((int) (Math.random() * 10));
		}
		HttpSession session = request.getSession();
		session.setAttribute("phone", phone);
		session.setAttribute("code", code.toString());
		session.setAttribute("time", new Timestamp(System.currentTimeMillis()));
		JSONObject jo = new JSONObject();
		jo.put("code", code.toString());
		System.out.println(jo.toString());
		if (SendMessage.sendMessage(phone, code.toString(), "" + timeout / 60000)) {
			return jo;
		} else {
			return null;
		}
	}

	@Override
	public WSCode validatePhone(String phone) throws Exception {
		if (phone == null || phone.trim().isEmpty())
			return WSCode.WRONG_PARAMETER_FORMAT;
		List<User> us = userDao.findByPhone(phone);
		if (us == null || us.isEmpty())
			return WSCode.PHONE_UNREGISTERED;
		else
			return WSCode.PHONE_REGISTERED;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public WSCode registByPhone(String username, String phone, String password, String code) throws Exception {
		if (username == null || username.trim().isEmpty() || phone == null || phone.trim().isEmpty() || password == null
				|| password.trim().isEmpty() || code == null || code.trim().isEmpty())
			return WSCode.WRONG_PARAMETER_FORMAT;
		List<User> user = userDao.findByPhone(phone);
		if (user != null && !user.isEmpty())
			return WSCode.PHONE_REGISTERED;
		List<User> user2 = userDao.findByUsername(username);
		if (user2 != null && !user2.isEmpty())
			return WSCode.USERNAME_REIGSTERED;
		HttpSession session = request.getSession();
		String phone1 = (String) session.getAttribute("phone");
		String code1 = (String) session.getAttribute("code");
		Timestamp time1 = (Timestamp) session.getAttribute("time");
		if (phone1 == null || phone1.trim().isEmpty() || code1 == null || code1.trim().isEmpty() || time1 == null)
			return WSCode.CODE_DISABLED;
		LocalDateTime time = time1.toLocalDateTime();
		LocalDateTime now = LocalDateTime.now();
		Duration d = Duration.between(time, now);
		if (d.toMillis() > timeout)
			return WSCode.CODE_DISABLED;
		if (phone.equals(phone1) && code.equals(code1)) {
			User u = new User();
			u.setId(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
			// u.setUsername("user" + phone);
			u.setUsername(username);
			u.setPassword(password);
			u.setPhone(phone);
			userDao.saveAndFlush(u);
			return WSCode.SUCCESS;
		}
		return WSCode.FAILED;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public WSCode updatePwd(String userkey, String newPwd, String oldPwd) throws Exception {
		if (userkey == null || userkey.trim().isEmpty() || newPwd == null || newPwd.trim().isEmpty() || oldPwd == null
				|| oldPwd.trim().isEmpty())
			return WSCode.WRONG_PARAMETER_FORMAT;
		User u = checkLogin(userkey);
		if (u == null)
			return WSCode.AUTH_FAILED;
		if (!oldPwd.equals(u.getPassword()))
			return WSCode.WRONG_PWD;
		u.setPassword(newPwd);
		userDao.saveAndFlush(u);
		return WSCode.SUCCESS;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public WSCode resetPwd(String phone, String code, String password) throws Exception {
		if (phone == null || phone.trim().isEmpty() || code == null || code.trim().isEmpty() || password == null
				|| password.trim().isEmpty())
			return WSCode.WRONG_PARAMETER_FORMAT;
		List<User> us = userDao.findByPhone(phone);
		if (us == null || us.isEmpty())
			return WSCode.PHONE_UNREGISTERED;
		HttpSession session = request.getSession();
		String phone1 = (String) session.getAttribute("phone");
		String code1 = (String) session.getAttribute("code");
		Timestamp time1 = (Timestamp) session.getAttribute("time");
		LocalDateTime time = time1.toLocalDateTime();
		LocalDateTime now = LocalDateTime.now();
		Duration d = Duration.between(time, now);
		if (d.toMillis() > timeout)
			return WSCode.CODE_DISABLED;
		if (!phone.equals(phone1) || !code.equals(code1))
			return WSCode.PHONE_MISMATCHING_CODE;
		us.get(0).setPassword(password);
		userDao.saveAndFlush(us.get(0));
		return WSCode.SUCCESS;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public JSONObject login(String username, String phone, String password, Integer version) throws Exception {
		if (password == null || password.trim().isEmpty())
			return null;
		if (version == null || version < this.version)
			return null;
		User u = null;
		List<User> us = null;
		if (username != null && !username.trim().isEmpty() && (phone == null || phone.trim().isEmpty()))
			us = userDao.findByUsername(username);
		if (phone != null && !phone.trim().isEmpty() && (username == null || username.trim().isEmpty()))
			us = userDao.findByPhone(phone);
		if (us == null || us.isEmpty())
			return null;
		u = us.get(0);
		if (!u.getPassword().equals(password))
			return null;
		u.setTimeMills(BigInteger.valueOf(System.currentTimeMillis()));
		userDao.saveAndFlush(u);
		JSONObject jo = new JSONObject();
		UserKey userKey = new UserKey();
		userKey.setUserID(u.getId());
		userKey.setUserLoginID(u.getUsername());
		userKey.setPassword(password);
		userKey.setTimeMillis(u.getTimeMills().longValue());
		userKey.Valid(true);
		jo.put("userkey", userKey.toUserKeyString());
		return jo;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public boolean shareCollectorToUser(String userkey, String collectorID, String username, Integer enable)
			throws Exception {
		if (userkey == null || userkey.trim().isEmpty() || collectorID == null || collectorID.trim().isEmpty()
				|| username == null || username.trim().isEmpty())
			return false;
		User owner = checkLogin(userkey);
		if (owner == null)
			return false;
		List<Collector> css = collectorDao.findByCollectorID(Integer.valueOf(collectorID));
		if (css == null || css.isEmpty())
			return false;
		List<User> shareds = userDao.findByUsername(username);
		if (shareds == null || shareds.isEmpty())
			return false;
		List<UserCollector> ucs = userCollectorDao.findByUsernameAndCollectorID(owner.getUsername(),
				Integer.valueOf(collectorID));
		if (ucs == null || ucs.isEmpty())
			return false;
		List<CollectorShare> collectorShares = collectorShareDao.findByCollectorAndUser(Integer.valueOf(collectorID),
				shareds.get(0).getId());
		if (collectorShares != null && !collectorShares.isEmpty())
			return false;
		CollectorShare cs = new CollectorShare();
		cs.setCollector(css.get(0));
		cs.setUser(shareds.get(0));
		cs.setEnable(enable == null ? 0 : enable);
		collectorShareDao.saveAndFlush(cs);
		return true;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public boolean deleteShareCollector(String userkey, String collectorID, String username) throws Exception {
		if (userkey == null || userkey.trim().isEmpty() || collectorID == null || collectorID.trim().isEmpty()
				|| username == null || username.trim().isEmpty())
			return false;
		User owner = checkLogin(userkey);
		if (owner == null)
			return false;
		List<Collector> cs = collectorDao.findByCollectorID(Integer.valueOf(collectorID));
		if (cs == null || cs.isEmpty())
			return false;
		List<User> shareds = userDao.findByUsername(username);
		if (shareds == null || shareds.isEmpty())
			return false;
		List<UserCollector> ucs = userCollectorDao.findByUsernameAndCollectorID(owner.getUsername(),
				Integer.valueOf(collectorID));
		if (ucs == null || ucs.isEmpty())
			return false;
		List<CollectorShare> css = collectorShareDao.findByCollectorAndUser(Integer.valueOf(collectorID),
				shareds.get(0).getId());
		if (css == null || css.isEmpty())
			return false;
		collectorShareDao.delete(css.get(0));
		return true;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public JSONObject wechatLogin(String token, String openid, String prefix_user, String host_user) throws Exception {
		if (token == null || token.trim().isEmpty() || openid == null || openid.trim().isEmpty())
			return null;
		String url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + token + "&openid=" + openid;
		URL realUrl = new URL(url);
		// 打开和URL之间的连接
		URLConnection connection = realUrl.openConnection();
		// 设置通用的请求属性
		connection.setRequestProperty("accept", "*/*");
		connection.setRequestProperty("connection", "Keep-Alive");
		connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
		// 建立实际的连接
		connection.connect();
		// 获取所有响应头字段
		// Map<String, List<String>> map = connection.getHeaderFields();
		// 遍历所有的响应头字段
		// for (String key : map.keySet()) {
		// System.out.println(key + "--->" + map.get(key));
		// }
		// 定义 BufferedReader输入流来读取URL的响应
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		String result = "";
		while ((line = in.readLine()) != null) {
			result += line;
		}
		JSONObject user = JSONObject.fromObject(result);
		String unionid = user.getString("unionid");
		String headimgurl = user.getString("headimgurl");
		String nickname = user.getString("nickname");
		// User u = userDao.findByOuterIDAndOuterType(openid, "wx");
		List<User> us = userDao.findByUsername("wx" + unionid);
		String path = prefix_user + "wx" + unionid + ".png";
		// CreateQrCode.createImg("wx" + unionid, path);
		User u;
		if (us == null || us.isEmpty()) { // 第一次登陆生成用户
			// u = new User("wx" + unionid, path);
			u = new User();
			u.setId(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
			u.setUserUrl(host_user + path);
			u.setHeadImg(headimgurl);
			u.setNickName(nickname);
			u.setOuterId(openid);
			u.setUsername("wx" + unionid);
			u.setOuterType("wx");
		} else
			u = us.get(0);
		u.setTimeMills(BigInteger.valueOf(System.currentTimeMillis()));
		userDao.saveAndFlush(u);
		UserKey userKey = new UserKey();
		JSONObject jo = new JSONObject();
		userKey.setUserID(u.getId());
		userKey.setUserLoginID(u.getUsername());
		userKey.setTimeMillis(u.getTimeMills().longValue());
		userKey.Valid(true);
		jo.put("userkey", userKey.toUserKeyString());
		return jo;
	}

	@Override
	public JSONArray getShareList(String userID, String collectorID) throws Exception {
		if (userID == null || userID.trim().isEmpty() || collectorID == null || collectorID.trim().isEmpty())
			return null;
		List<User> us = userDao.findByIds(userID);
		if (us == null || us.isEmpty())
			return null;
		List<UserCollector> ucs = userCollectorDao.findByUsernameAndCollectorID(us.get(0).getUsername(),
				Integer.valueOf(collectorID));
		if (ucs == null || ucs.isEmpty())
			return null;
		List<CollectorShare> css = collectorShareDao.findByCollectorID(Integer.valueOf(collectorID));
		if (css == null || css.isEmpty())
			return null;
		JSONArray ja = new JSONArray();
		for (CollectorShare cs : css) {
			User user = cs.getUser();
			JSONObject jo = JSONObject.fromObject(user);
			jo.remove("password");
			jo.put("enable", cs.getEnable());
			ja.add(jo);
		}
		return ja;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {
            RuntimeException.class, Exception.class})
	public JSONObject qqLogin(String nickName, String headImg, String openid, String prefix_user, String host_user)
			throws Exception {
		if (nickName == null || nickName.trim().isEmpty() || headImg == null || headImg.trim().isEmpty()
				|| openid == null || openid.trim().isEmpty())
			return null;
		List<User> us = userDao.findByOuterIDAndOuterType(openid, "qq");
		String path = prefix_user + "qq" + openid + ".png";
		// CreateQrCode.createImg("qq" + openid, path);
		User u;
		if (us == null || us.isEmpty()) {
			// u = new User("qq" + openid, path);
			u = new User();
			u.setId(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
			u.setUserUrl(host_user + path);
		} else
			u = us.get(0);
		u.setUsername("qq" + openid);
		u.setTimeMills(BigInteger.valueOf(System.currentTimeMillis()));
		u.setHeadImg(headImg);
		u.setOuterId(openid);
		u.setOuterType("qq");
		userDao.saveAndFlush(u);
		UserKey userKey = new UserKey();
		JSONObject jo = new JSONObject();
		userKey.setUserID(u.getId());
		userKey.setUserLoginID(u.getUsername());
		userKey.setTimeMillis(u.getTimeMills().longValue());
		userKey.Valid(true);
		jo.put("userkey", userKey.toUserKeyString());
		return jo;
	}

	@Override
	public JSONArray getOperateRecord(String userID, int start, int length) throws Exception {
		if (userID == null || userID.trim().isEmpty() || start < 0 || length < 1)
			return null;
		List<Controller> cs = controllerDao.findControllerByUser(userID, start * length, length);
		if (cs == null || cs.isEmpty())
			return null;
		JSONArray ja = new JSONArray();
		for (Controller c : cs) {
			JSONObject jo = new JSONObject();
			jo.put("type", c.getTargetType());
			if (c.getTargetType() == 0) { // 用户操作断路器
				List<Switch> ss = switchDao.findBySwitchID(c.getTargetID());
				if (ss == null || ss.isEmpty())
					continue;
				Switch s = ss.get(0);
				jo.put("runResult", c.getRunResult());
				jo.put("name", s.getName());
				jo.put("code", s.getCode());
				jo.put("source", c.getSource());
				jo.put("cmdData", c.getCmdData());
				jo.put("time", PowerManagerUtil.timestampToString(c.getRunTime()));
				jo.put("runCode", c.getRunCode());
			} else if (c.getTargetType() == 1) { // 用户操作场景
				List<Scene> ss = sceneDao.findBySceneID(Integer.valueOf(c.getTargetID()));
				if (ss == null || ss.isEmpty())
					continue;
				jo.put("name", ss.get(0).getName());
				jo.put("time", PowerManagerUtil.timestampToString(c.getRunTime()));
				List<SceneSwitch> sss = sceneSwitchDao.findBySceneID(Integer.valueOf(c.getTargetID()));
				if (sss == null || sss.isEmpty())
					continue;
				JSONArray ja_ss = new JSONArray();
				for (SceneSwitch sceneSwitch : sss) {
					JSONObject jo_ss = new JSONObject();
					jo_ss.put("name", sceneSwitch.getSwitchs().getName());
					jo_ss.put("code", sceneSwitch.getSwitchs().getCode());
					jo_ss.put("cmdData", sceneSwitch.getCmdData());
					ja_ss.add(jo_ss);
				}
				jo.put("switchs", ja_ss);
			}
			ja.add(jo);
		}
		return ja;
	}

	@Override
	public JSONArray getAlarmList(String userID, int start, int length) throws Exception {
		if (userID == null || userID.trim().isEmpty() || start < 0 || length < 1)
			return null;
		List<DeviceAlarm> das_switch = deviceAlarmDao.findSwitchAlarmByUser(userID, start * length, length);
		List<DeviceAlarm> das_collector = deviceAlarmDao.findCollectorAlarmByUser(userID, start * length, length);
		if ((das_switch == null || das_switch.isEmpty()) && (das_collector == null || das_collector.isEmpty()))
			return null;
		JSONArray ja = new JSONArray();
		if (das_switch != null && !das_switch.isEmpty()) { // 断路器的告警
			for (DeviceAlarm da : das_switch) {
				JSONObject jo = new JSONObject();
				List<Switch> ss = switchDao.findBySwitchID(Integer.valueOf(da.getDeviceID()));
				if (ss == null || ss.isEmpty())
					continue;
				Switch s = ss.get(0);
				jo.put("name", s.getName());
				jo.put("code", s.getCode());
				jo.put("time", PowerManagerUtil.timestampToString(da.getTime()));
				jo.put("state", da.getCode());
				ja.add(jo);
			}
		}
		if (das_collector != null && !das_collector.isEmpty()) { // 集中器的告警

		}
		return ja;
	}

	@Override
	public boolean rebootCollector(String userID, String collectorID) throws Exception {
		if (userID == null || userID.trim().isEmpty() || collectorID == null || collectorID.trim().isEmpty())
			return false;
		List<UserCollector> ucs = userCollectorDao.findByUserIDAndCollectorID(userID, Integer.valueOf(collectorID));
		if (ucs == null || ucs.isEmpty())
			return false;
		List<Collector> cs = collectorDao.findByCollectorID(Integer.valueOf(collectorID));
		if (cs == null || cs.isEmpty())
			return false;
		return VirtualFsuController.rebootCollector(cs.get(0));
	}

	@Override
	public boolean resetCollector(String userID, String collectorID) throws Exception {
		if (userID == null || userID.trim().isEmpty() || collectorID == null || collectorID.trim().isEmpty())
			return false;
		List<UserCollector> ucs = userCollectorDao.findByUserIDAndCollectorID(userID, Integer.valueOf(collectorID));
		if (ucs == null || ucs.isEmpty())
			return false;
		List<Collector> cs = collectorDao.findByCollectorID(Integer.valueOf(collectorID));
		if (cs == null || cs.isEmpty())
			return false;
		return VirtualFsuController.resetCollector(cs.get(0));
	}

	@Override
	public void getQRCode(String content, String title, HttpServletResponse resp) throws Exception {
		CreateQrCode.createImg(content, title, resp);
	}

	@Override
	public String getIpBySwitchID(String switchID) {
		if (switchID == null || switchID.trim().isEmpty())
			return null;
		List<Switch> ss = switchDao.findBySwitchID(Integer.valueOf(switchID));
		if (ss == null || ss.isEmpty())
			return null;
		String ip = ss.get(0).getCollector().getServer().getIp();
		return ip;
	}

	@Override
	public Set<String> getIpBySceneID(String sceneID) {
		if (sceneID == null || sceneID.trim().isEmpty())
			return null;
		List<SceneSwitch> sss = sceneSwitchDao.findBySceneID(Integer.valueOf(sceneID));
		if (sss == null || sss.isEmpty())
			return null;
		Set<String> ips = new HashSet<String>();
		for (SceneSwitch ss : sss) {
			ips.add(ss.getSwitchs().getCollector().getServer().getIp());
		}
		return ips;
	}
}
