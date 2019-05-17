package com.rogy.smarte.fsu;

import com.rogy.smarte.entity.db1.*;
import com.rogy.smarte.fsu.message.*;
import com.rogy.smarte.util.Aes128;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;


/**
 * 设备操作接口类。
 */
public class VirtualFsuController {
    /**
     * 断路器控制命令执行对象。
     */
    public static CollectorCommandExecutor collectorCommandExecutor =
            new CollectorCommandExecutor(VirtualFsuUtil.executorService);
    /**
     * 断路器定时操作执行对象。
     */
    public static BreakerTimerExecutor breakerTimerExecutor =
            new BreakerTimerExecutor(VirtualFsuUtil.scheduledExecutorService);
    /**
     * 最大上传个数。
     */
    public static final int MAX_UPLOAD_TIMERS = 24;

    /**
     * 断路器控制命令执行次数。
     * 因为存在发送控制命令后未执行的情况，所以多次发送同一命令以确保执行。
     */
    public static int BREAKER_CONTROL_TIMES = 6;
    /**
     * 断路器控制命令执行等待时间(毫秒)。
     */
    public static int BREAKER_CONTROL_WAITMS = 500;
    /**
     * 断路器故障命令执行等待时间(毫秒)。
     */
    public static int BREAKER_FAULT_WAITMS = 500;

	private VirtualFsuController() {
	}

	/**
	 * 断路器操作。
	 * @param switchID 断路器ID。
	 * @param cmdData 操作命令(0=断闸;1=合闸)。
	 * @param source 来源(0=远程执行;1=定时执行)。
	 * @return 返回对应的Controller记录。null表示失败。
	 */
	public static Controller switchControl(int switchID, byte cmdData, byte source) {
		Switch swt = null;
		try {
			swt = VirtualFsuUtil.virtualFsuService.findSwitchBySwitchID(switchID);	// 对应的断路器
		} catch (Exception e) {
			swt = null;
			e.printStackTrace();
		}
		if(swt == null)
			return null;
		return switchControl(swt, cmdData, source);
	}

	/**
	 * 断路器操作。
	 * @param swt 断路器对象。
	 * @param cmdData 操作命令(0=断闸;1=合闸)。
	 * @param source 来源(0=远程执行;1=定时执行)。
	 * @return 返回对应的Controller记录。null表示失败。
	 */
	public static Controller switchControl(final Switch swt, byte cmdData, byte source) {
		if((cmdData != 0 && cmdData != 1) ||
				(source != 0 && source != 1))
			return null;
		Controller controller = null;
		try {
			// 生成对应的Controller记录。
			controller = new Controller();
			controller.setControllerID(0L);
			controller.setTargetType((byte) 0);	// 断路器
			controller.setTargetID(swt.getSwitchID());
			Timestamp tsNow = Timestamp.valueOf(LocalDateTime.now());
			controller.setGenTime(tsNow);
			controller.setRunTime(tsNow);
			controller.setCmdData(cmdData);
			controller.setSource(source);
			controller.setRunCode("0");
			controller.setRunResult(0);
			controller = VirtualFsuUtil.virtualFsuService.addOrUpdateControllerResult(controller);
			//System.out.printf("[%s] switchControl - new controller(%d)\n", LocalDateTime.now(), controller.getControllerID());
			// 提交执行
			boolean bret = doBreakerControl(controller, swt);
			if(!bret) {	// 提交执行失败
				controller.setRunCode("1");
				controller = VirtualFsuUtil.virtualFsuService.addOrUpdateControllerResult(controller);
			}
		} catch(Exception e) {
			controller = null;
			e.printStackTrace();
		}
		return controller;
	}

	/**
	 * 场景执行。
	 * @param scene 场景。
	 * @param source 来源(0=远程执行;1=定时执行)。
	 * @return 返回对应的Controller记录。null表示失败。
	 */
	public static Controller sceneControl(final Scene scene, byte source) {
		if(scene == null ||
				(source != 0 && source != 1))
			return null;
		Controller controller = null;
		try {
			// 生成对应的Controller记录。
			controller = new Controller();
			controller.setControllerID(0L);
			controller.setTargetType((byte) 1);	// 场景
			controller.setTargetID(scene.getSceneID());
			Timestamp tsNow = Timestamp.valueOf(LocalDateTime.now());
			controller.setGenTime(tsNow);
			controller.setRunTime(tsNow);
			controller.setSource(source);
			controller.setRunCode("0");
			controller.setRunResult(0);
			controller = VirtualFsuUtil.virtualFsuService.addOrUpdateControllerResult(controller);
			// 提交执行
			boolean bret = doSceneControl(controller);
			if(!bret) {	// 提交执行失败
				controller.setRunCode("1");
				controller = VirtualFsuUtil.virtualFsuService.addOrUpdateControllerResult(controller);
			}
		} catch(Exception e) {
			controller = null;
			e.printStackTrace();
		}
		return controller;
	}

	/**
	 * 断路器控制。
	 * @param controller 控制命令对象。
	 * @return 成功与否。
	 */
	private static boolean doBreakerControl(Controller controller, final Switch swt) {
		try {
			LocalDateTime now = LocalDateTime.now();
			long codeValueBreaker = Long.parseLong(swt.getCode(), 16);
			// 获取断路器对应的集中器
			Long codeValueCollector;
			try {
				codeValueCollector = Long.parseLong(swt.getCollector().getCode(), 16);
			} catch(Exception e) {
				codeValueCollector = null;
			}
			if(codeValueCollector == null) {
				System.out.printf("[%s] doBreakerControl - Failed to get Collector of Breaker(%012X)\n",
						now,
						codeValueBreaker);
				return false;
			}
			// 获取集中器信息对象
			CollectorInfo collectorInfo = VirtualFsuCollectorInfo.getCollectorInfo(codeValueCollector);
			if(collectorInfo == null) {
				System.out.printf("[%s] doBreakerControl - Failed to get CollectorInfo of Collector(%012X)\n",
						now,
						codeValueCollector);
				return false;
			}
			// 获取集中器对应的连接
			ChannelHandlerContext ctx = collectorInfo.getChannelHandlerContext();
			if(ctx == null) {
				System.out.printf("[%s] doBreakerControl - Failed to get Socket of Collector(%012X)\n",
						now,
						codeValueCollector);
				return false;
			}
			// 发送命令
			//return doBreakerControl(controller, swt, controller.getCmdData(), collectorInfo);
			if(0 == collectorCommandExecutor.addCommand(
					codeValueBreaker, swt.getSwitchID(),
					codeValueCollector, swt.getCollector().getCollectorID(),
					controller.getCmdData(), controller.getControllerID()))
				return true;
			else
				return false;
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 场景控制。
	 * @param controller 控制命令对象。
	 * @return 发送成功与否。
	 */
	private static boolean doSceneControl(Controller controller) {
		int count = 0;
		try {
			List<SceneSwitch> sceneSwitchs = VirtualFsuUtil.virtualFsuService.findSceneSwitchsBySceneID(controller.getTargetID());
			if(sceneSwitchs.isEmpty()) {
				System.out.printf("[%s] doSceneControl(%s) - empty sceneSwitchs.\n",
						LocalDateTime.now().toString(),
						controller.getControllerID());
				return false;
			}
			Switch swt;
			long codeValueBreaker;
			Long codeValueCollector;
			LocalDateTime now;
			for(SceneSwitch sceneSwitch : sceneSwitchs) {
				now = LocalDateTime.now();
				swt = sceneSwitch.getSwitchs();
				if(swt == null)
					continue;
				codeValueBreaker = Long.parseLong(swt.getCode(), 16);
				// 获取断路器对应的集中器
				try {
					codeValueCollector = Long.parseLong(swt.getCollector().getCode(), 16);
				} catch(Exception e) {
					codeValueCollector = null;
				}
				if(codeValueCollector == null) {
					System.out.printf("[%s] doSceneControl(%s) - Failed to get Collector of Breaker(%012X)\n",
							now,
							controller.getControllerID(),
							codeValueBreaker);
					continue;
				}
				// 获取集中器信息对象
				CollectorInfo collectorInfo = VirtualFsuCollectorInfo.getCollectorInfo(codeValueCollector);
				if(collectorInfo == null) {
					System.out.printf("[%s] doSceneControl - Failed to get CollectorInfo of Collector(%012X)\n",
							now,
							codeValueCollector);
					continue;
				}
				// 获取集中器对应的连接
				ChannelHandlerContext ctx = collectorInfo.getChannelHandlerContext();
				if(ctx == null) {
					System.out.printf("[%s] doSceneControl - Failed to get Socket of Collector(%012X)\n",
							now,
							codeValueCollector);
					continue;
				}
				if(0 == collectorCommandExecutor.addCommand(
						codeValueBreaker, swt.getSwitchID(),
						codeValueCollector, swt.getCollector().getCollectorID(),
						sceneSwitch.getCmdData(), controller.getControllerID()))
					count++;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return count > 0;
	}

	/**
	 * 发送断路器开合命令。
	 * @param cmdData 0=断开;1=合闸。
	 * @param codeValueBreaker 断路器地址值。
	 * @param encrypt 数据加密方式。
	 * @param key 数据加密密钥。
	 * @ctx 对应的连接。
	 * @return 命令发送成功与否。
	 */
	public static boolean switchOnOff(int cmdData, long codeValueBreaker, int encrypt, final byte[] key, ChannelHandlerContext ctx) {
		try {
			MessageBodyBreakerControl body = new MessageBodyBreakerControl();
			body.setBrkCode(codeValueBreaker);
			body.setOn(cmdData == 0 ? false : true);
			byte[] bodyBytes = body.getBytes();
			if((encrypt & 0x02) > 0)	// AES
				bodyBytes = Aes128.cfb8(key, 1, bodyBytes);
			Message mm = new Message();
			mm.setId(0x0301);
			mm.setSerialno(VirtualFsuUtil.serialNo.getAndIncrement());
			mm.wrapBody(bodyBytes);
			mm.setBodyLength(bodyBytes.length);
			mm.setEncrypt(encrypt);
			mm.setChecksum(mm.calcChecksum());
			byte[] mmBytes = mm.getBytes(VirtualFsuUtil.BYTEORDER, true);
			if(mmBytes != null) {
				writeAndFlush(mmBytes, ctx);
				return true;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

    /**
     * 设置断路器过压阀值。
     *
     * @param switchCode 断路器编码。
     * @param index      过压阀值索引(0、1、2、3)。
     * @param th         阀值(XXX.X)。
     * @return 命令发送成功与否。
     */
    public static boolean setSwitchOverVoltageThreadhold(String switchCode, int index, double th) {
        if (index < 0 || index > 3)
            throw new java.lang.IllegalArgumentException("index out of range, must be 0 - 3.");

        try {
            LocalDateTime now = LocalDateTime.now();
            Switch swt = VirtualFsuUtil.virtualFsuService.findSwitchBySwitchCode(switchCode);    // 对应的断路器
            if (swt == null)
                return false;
            long codeValueBreaker = Long.parseLong(swt.getCode(), 16);
            // 获取断路器对应的集中器
            Long codeValueCollector;
            try {
                codeValueCollector = Long.parseLong(swt.getCollector().getCode(), 16);
            } catch (Exception e) {
                codeValueCollector = null;
            }
            if (codeValueCollector == null) {
                System.out.printf("[%s] setSwitchOverVoltageThreadhold - Failed to get Collector of Breaker(%012X)\n",
                        now,
                        codeValueBreaker);
                return false;
            }
            // 获取集中器信息对象
            CollectorInfo collectorInfo = VirtualFsuCollectorInfo.getCollectorInfo(codeValueCollector);
            if (collectorInfo == null) {
                System.out.printf("[%s] setSwitchOverVoltageThreadhold - Failed to get CollectorInfo of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 获取集中器对应的连接
            ChannelHandlerContext ctx = collectorInfo.getChannelHandlerContext();
            if (ctx == null) {
                System.out.printf("[%s] setSwitchOverVoltageThreadhold - Failed to get Socket of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 发送命令
            MessageBodyParam param = new MessageBodyParam();
            param.setCode(codeValueBreaker);
            MessageBodyParamValue value = new MessageBodyParamValue(0x05L + index, 0);    // 过压阀值
            value.setValue(VirtualFsuUtil.BYTEORDER, Double.valueOf(th));
            param.addValue(value);
            byte[] bodyBytes = param.getBytes(VirtualFsuUtil.BYTEORDER);
            int encrypt = collectorInfo.getEncrypt();
            if ((encrypt & 0x02) > 0)    // AES
                bodyBytes = Aes128.cfb8(collectorInfo.getKey(), 1, bodyBytes);
            Message mm = new Message();
            mm.setId(0x0401);
            mm.setSerialno(VirtualFsuUtil.serialNo.getAndIncrement());
            mm.wrapBody(bodyBytes);
            mm.setBodyLength(bodyBytes.length);
            mm.setEncrypt(encrypt);
            mm.setChecksum(mm.calcChecksum());
            byte[] mmBytes = mm.getBytes(VirtualFsuUtil.BYTEORDER, true);
            if (mmBytes != null) {
                writeAndFlush(mmBytes, ctx);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 获取断路器过压阀值。
     *
     * @param switchCode 断路器编码。
     * @param index      过压阀值索引(0、1、2、3)。
     * @return 命令发送成功与否。
     */
    public static boolean getSwitchOverVoltageThreadhold(String switchCode, int index) {
        if (index < 0 || index > 3)
            throw new java.lang.IllegalArgumentException("index out of range, must be 0 - 3.");

        try {
            LocalDateTime now = LocalDateTime.now();
            Switch swt = VirtualFsuUtil.virtualFsuService.findSwitchBySwitchCode(switchCode);    // 对应的断路器
            if (swt == null)
                return false;
            long codeValueBreaker = Long.parseLong(swt.getCode(), 16);
            // 获取断路器对应的集中器
            Long codeValueCollector;
            try {
                codeValueCollector = Long.parseLong(swt.getCollector().getCode(), 16);
            } catch (Exception e) {
                codeValueCollector = null;
            }
            if (codeValueCollector == null) {
                System.out.printf("[%s] getSwitchOverVoltageThreadhold - Failed to get Collector of Breaker(%012X)\n",
                        now,
                        codeValueBreaker);
                return false;
            }
            // 获取集中器信息对象
            CollectorInfo collectorInfo = VirtualFsuCollectorInfo.getCollectorInfo(codeValueCollector);
            if (collectorInfo == null) {
                System.out.printf("[%s] getSwitchOverVoltageThreadhold - Failed to get CollectorInfo of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 获取集中器对应的连接
            ChannelHandlerContext ctx = collectorInfo.getChannelHandlerContext();
            if (ctx == null) {
                System.out.printf("[%s] getSwitchOverVoltageThreadhold - Failed to get Socket of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 发送命令
            int paramId = 0x05 + index;    // 过压阀值
            MessageBodyQueryParam body = new MessageBodyQueryParam();
            body.setCode(codeValueBreaker);
            body.addId(paramId);
            byte[] bodyBytes = body.getBytes(VirtualFsuUtil.BYTEORDER);
            int encrypt = collectorInfo.getEncrypt();
            if ((encrypt & 0x02) > 0)    // AES
                bodyBytes = Aes128.cfb8(collectorInfo.getKey(), 1, bodyBytes);
            Message mm = new Message();
            mm.setId(0x0403);
            mm.setSerialno(VirtualFsuUtil.serialNo.getAndIncrement());
            mm.wrapBody(bodyBytes);
            mm.setBodyLength(bodyBytes.length);
            mm.setEncrypt(encrypt);
            mm.setChecksum(mm.calcChecksum());
            byte[] mmBytes = mm.getBytes(VirtualFsuUtil.BYTEORDER, true);
            if (mmBytes != null) {
                SwitchParam switchParam = VirtualFsuUtil.virtualFsuService.findSwitchParamBySwitchIDParamID(swt.getSwitchID(), paramId);
                if (switchParam == null) {
                    switchParam = new SwitchParam();
                    switchParam.setId(UUID.randomUUID().toString()
                            .replaceAll("-", ""));
                    switchParam.setSwitchs(swt);
                    switchParam.setParamID(paramId);
                }
                switchParam.setGenTime(Timestamp.valueOf(now));
                switchParam.setReturnTime(null);
                switchParam.setState(0);
                VirtualFsuUtil.virtualFsuService.addOrUpdateSwitchParam(switchParam);

                writeAndFlush(mmBytes, ctx);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 设置断路器欠压阀值。
     *
     * @param switchCode 断路器编码。
     * @param th         阀值(XXX.X)。
     * @return 命令发送成功与否。
     */
    public static boolean setSwitchUnderVoltageThreadhold(String switchCode, double th) {
        try {
            LocalDateTime now = LocalDateTime.now();
            Switch swt = VirtualFsuUtil.virtualFsuService.findSwitchBySwitchCode(switchCode);    // 对应的断路器
            if (swt == null)
                return false;
            long codeValueBreaker = Long.parseLong(swt.getCode(), 16);
            // 获取断路器对应的集中器
            Long codeValueCollector;
            try {
                codeValueCollector = Long.parseLong(swt.getCollector().getCode(), 16);
            } catch (Exception e) {
                codeValueCollector = null;
            }
            if (codeValueCollector == null) {
                System.out.printf("[%s] setSwitchUnderVoltageThreadhold - Failed to get Collector of Breaker(%012X)\n",
                        now,
                        codeValueBreaker);
                return false;
            }
            // 获取集中器信息对象
            CollectorInfo collectorInfo = VirtualFsuCollectorInfo.getCollectorInfo(codeValueCollector);
            if (collectorInfo == null) {
                System.out.printf("[%s] setSwitchUnderVoltageThreadhold - Failed to get CollectorInfo of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 获取集中器对应的连接
            ChannelHandlerContext ctx = collectorInfo.getChannelHandlerContext();
            if (ctx == null) {
                System.out.printf("[%s] setSwitchUnderVoltageThreadhold - Failed to get Socket of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 发送命令
            MessageBodyParam param = new MessageBodyParam();
            param.setCode(codeValueBreaker);
            MessageBodyParamValue value = new MessageBodyParamValue(0x0DL, 0);    // 欠压阀值
            value.setValue(VirtualFsuUtil.BYTEORDER, Double.valueOf(th));
            param.addValue(value);
            byte[] bodyBytes = param.getBytes(VirtualFsuUtil.BYTEORDER);
            int encrypt = collectorInfo.getEncrypt();
            if ((encrypt & 0x02) > 0)    // AES
                bodyBytes = Aes128.cfb8(collectorInfo.getKey(), 1, bodyBytes);
            Message mm = new Message();
            mm.setId(0x0401);
            mm.setSerialno(VirtualFsuUtil.serialNo.getAndIncrement());
            mm.wrapBody(bodyBytes);
            mm.setBodyLength(bodyBytes.length);
            mm.setEncrypt(encrypt);
            mm.setChecksum(mm.calcChecksum());
            byte[] mmBytes = mm.getBytes(VirtualFsuUtil.BYTEORDER, true);
            if (mmBytes != null) {
                writeAndFlush(mmBytes, ctx);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 获取断路器欠压阀值。
     *
     * @param switchCode 断路器编码。
     * @return 命令发送成功与否。
     */
    public static boolean getSwitchUnderVoltageThreadhold(String switchCode) {
        try {
            LocalDateTime now = LocalDateTime.now();
            Switch swt = VirtualFsuUtil.virtualFsuService.findSwitchBySwitchCode(switchCode);    // 对应的断路器
            if (swt == null)
                return false;
            long codeValueBreaker = Long.parseLong(swt.getCode(), 16);
            // 获取断路器对应的集中器
            Long codeValueCollector;
            try {
                codeValueCollector = Long.parseLong(swt.getCollector().getCode(), 16);
            } catch (Exception e) {
                codeValueCollector = null;
            }
            if (codeValueCollector == null) {
                System.out.printf("[%s] getSwitchUnderVoltageThreadhold - Failed to get Collector of Breaker(%012X)\n",
                        now,
                        codeValueBreaker);
                return false;
            }
            // 获取集中器信息对象
            CollectorInfo collectorInfo = VirtualFsuCollectorInfo.getCollectorInfo(codeValueCollector);
            if (collectorInfo == null) {
                System.out.printf("[%s] getSwitchUnderVoltageThreadhold - Failed to get CollectorInfo of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 获取集中器对应的连接
            ChannelHandlerContext ctx = collectorInfo.getChannelHandlerContext();
            if (ctx == null) {
                System.out.printf("[%s] getSwitchUnderVoltageThreadhold - Failed to get Socket of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 发送命令
            int paramId = 0x0D;    // 欠压阀值
            MessageBodyQueryParam body = new MessageBodyQueryParam();
            body.setCode(codeValueBreaker);
            body.addId(paramId);
            byte[] bodyBytes = body.getBytes(VirtualFsuUtil.BYTEORDER);
            int encrypt = collectorInfo.getEncrypt();
            if ((encrypt & 0x02) > 0)    // AES
                bodyBytes = Aes128.cfb8(collectorInfo.getKey(), 1, bodyBytes);
            Message mm = new Message();
            mm.setId(0x0403);
            mm.setSerialno(VirtualFsuUtil.serialNo.getAndIncrement());
            mm.wrapBody(bodyBytes);
            mm.setBodyLength(bodyBytes.length);
            mm.setEncrypt(encrypt);
            mm.setChecksum(mm.calcChecksum());
            byte[] mmBytes = mm.getBytes(VirtualFsuUtil.BYTEORDER, true);
            if (mmBytes != null) {
                SwitchParam switchParam = VirtualFsuUtil.virtualFsuService.findSwitchParamBySwitchIDParamID(swt.getSwitchID(), paramId);
                if (switchParam == null) {
                    switchParam = new SwitchParam();
                    switchParam.setId(UUID.randomUUID().toString()
                            .replaceAll("-", ""));
                    switchParam.setSwitchs(swt);
                    switchParam.setParamID(paramId);
                }
                switchParam.setGenTime(Timestamp.valueOf(now));
                switchParam.setReturnTime(null);
                switchParam.setState(0);
                VirtualFsuUtil.virtualFsuService.addOrUpdateSwitchParam(switchParam);

                writeAndFlush(mmBytes, ctx);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 设置断路器过流阀值。
     *
     * @param switchCode 断路器编码。
     * @param th         阀值(XXXXX.XXX)。
     * @return 命令发送成功与否。
     */
    public static boolean setSwitchOverCurrentThreadhold(String switchCode, double th) {
        try {
            LocalDateTime now = LocalDateTime.now();
            Switch swt = VirtualFsuUtil.virtualFsuService.findSwitchBySwitchCode(switchCode);    // 对应的断路器
            if (swt == null)
                return false;
            long codeValueBreaker = Long.parseLong(swt.getCode(), 16);
            // 获取断路器对应的集中器
            Long codeValueCollector;
            try {
                codeValueCollector = Long.parseLong(swt.getCollector().getCode(), 16);
            } catch (Exception e) {
                codeValueCollector = null;
            }
            if (codeValueCollector == null) {
                System.out.printf("[%s] setSwitchOverCurrentThreadhold - Failed to get Collector of Breaker(%012X)\n",
                        now,
                        codeValueBreaker);
                return false;
            }
            // 获取集中器信息对象
            CollectorInfo collectorInfo = VirtualFsuCollectorInfo.getCollectorInfo(codeValueCollector);
            if (collectorInfo == null) {
                System.out.printf("[%s] setSwitchOverCurrentThreadhold - Failed to get CollectorInfo of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 获取集中器对应的连接
            ChannelHandlerContext ctx = collectorInfo.getChannelHandlerContext();
            if (ctx == null) {
                System.out.printf("[%s] setSwitchOverCurrentThreadhold - Failed to get Socket of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 发送命令
            MessageBodyParam param = new MessageBodyParam();
            param.setCode(codeValueBreaker);
            MessageBodyParamValue value = new MessageBodyParamValue(0x11L, 0);    // 过流阀值
            value.setValue(VirtualFsuUtil.BYTEORDER, Double.valueOf(th));
            param.addValue(value);
            byte[] bodyBytes = param.getBytes(VirtualFsuUtil.BYTEORDER);
            int encrypt = collectorInfo.getEncrypt();
            if ((encrypt & 0x02) > 0)    // AES
                bodyBytes = Aes128.cfb8(collectorInfo.getKey(), 1, bodyBytes);
            Message mm = new Message();
            mm.setId(0x0401);
            mm.setSerialno(VirtualFsuUtil.serialNo.getAndIncrement());
            mm.wrapBody(bodyBytes);
            mm.setBodyLength(bodyBytes.length);
            mm.setEncrypt(encrypt);
            mm.setChecksum(mm.calcChecksum());
            byte[] mmBytes = mm.getBytes(VirtualFsuUtil.BYTEORDER, true);
            if (mmBytes != null) {
                writeAndFlush(mmBytes, ctx);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 获取断路器过流阀值。
     *
     * @param switchCode 断路器编码。
     * @return 命令发送成功与否。
     */
    public static boolean getSwitchOverCurrentThreadhold(String switchCode) {
        try {
            LocalDateTime now = LocalDateTime.now();
            Switch swt = VirtualFsuUtil.virtualFsuService.findSwitchBySwitchCode(switchCode);    // 对应的断路器
            if (swt == null)
                return false;
            long codeValueBreaker = Long.parseLong(swt.getCode(), 16);
            // 获取断路器对应的集中器
            Long codeValueCollector;
            try {
                codeValueCollector = Long.parseLong(swt.getCollector().getCode(), 16);
            } catch (Exception e) {
                codeValueCollector = null;
            }
            if (codeValueCollector == null) {
                System.out.printf("[%s] getSwitchOverCurrentThreadhold - Failed to get Collector of Breaker(%012X)\n",
                        now,
                        codeValueBreaker);
                return false;
            }
            // 获取集中器信息对象
            CollectorInfo collectorInfo = VirtualFsuCollectorInfo.getCollectorInfo(codeValueCollector);
            if (collectorInfo == null) {
                System.out.printf("[%s] getSwitchOverCurrentThreadhold - Failed to get CollectorInfo of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 获取集中器对应的连接
            ChannelHandlerContext ctx = collectorInfo.getChannelHandlerContext();
            if (ctx == null) {
                System.out.printf("[%s] getSwitchOverCurrentThreadhold - Failed to get Socket of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 发送命令
            int paramId = 0x11;    // 过流阀值
            MessageBodyQueryParam body = new MessageBodyQueryParam();
            body.setCode(codeValueBreaker);
            body.addId(paramId);
            byte[] bodyBytes = body.getBytes(VirtualFsuUtil.BYTEORDER);
            int encrypt = collectorInfo.getEncrypt();
            if ((encrypt & 0x02) > 0)    // AES
                bodyBytes = Aes128.cfb8(collectorInfo.getKey(), 1, bodyBytes);
            Message mm = new Message();
            mm.setId(0x0403);
            mm.setSerialno(VirtualFsuUtil.serialNo.getAndIncrement());
            mm.wrapBody(bodyBytes);
            mm.setBodyLength(bodyBytes.length);
            mm.setEncrypt(encrypt);
            mm.setChecksum(mm.calcChecksum());
            byte[] mmBytes = mm.getBytes(VirtualFsuUtil.BYTEORDER, true);
            if (mmBytes != null) {
                SwitchParam switchParam = VirtualFsuUtil.virtualFsuService.findSwitchParamBySwitchIDParamID(swt.getSwitchID(), paramId);
                if (switchParam == null) {
                    switchParam = new SwitchParam();
                    switchParam.setId(UUID.randomUUID().toString()
                            .replaceAll("-", ""));
                    switchParam.setSwitchs(swt);
                    switchParam.setParamID(paramId);
                }
                switchParam.setGenTime(Timestamp.valueOf(now));
                switchParam.setReturnTime(null);
                switchParam.setState(0);
                VirtualFsuUtil.virtualFsuService.addOrUpdateSwitchParam(switchParam);

                writeAndFlush(mmBytes, ctx);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 设置断路器电量下限。
     *
     * @param switchCode 断路器编码。
     * @param ele        电量下限(XXXXXX.XX)。
     * @return 命令发送成功与否。
     */
    public static boolean setSwitchEleLower(String switchCode, double ele) {
        try {
            LocalDateTime now = LocalDateTime.now();
            Switch swt = VirtualFsuUtil.virtualFsuService.findSwitchBySwitchCode(switchCode);    // 对应的断路器
            if (swt == null)
                return false;
            long codeValueBreaker = Long.parseLong(swt.getCode(), 16);
            // 获取断路器对应的集中器
            Long codeValueCollector;
            try {
                codeValueCollector = Long.parseLong(swt.getCollector().getCode(), 16);
            } catch (Exception e) {
                codeValueCollector = null;
            }
            if (codeValueCollector == null) {
                System.out.printf("[%s] setSwitchEleLower - Failed to get Collector of Breaker(%012X)\n",
                        now,
                        codeValueBreaker);
                return false;
            }
            // 获取集中器信息对象
            CollectorInfo collectorInfo = VirtualFsuCollectorInfo.getCollectorInfo(codeValueCollector);
            if (collectorInfo == null) {
                System.out.printf("[%s] setSwitchEleLower - Failed to get CollectorInfo of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 获取集中器对应的连接
            ChannelHandlerContext ctx = collectorInfo.getChannelHandlerContext();
            if (ctx == null) {
                System.out.printf("[%s] setSwitchEleLower - Failed to get Socket of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 发送命令
            MessageBodyParam param = new MessageBodyParam();
            param.setCode(codeValueBreaker);
            MessageBodyParamValue value = new MessageBodyParamValue(0x18L, 0);    // 电量下限
            value.setValue(VirtualFsuUtil.BYTEORDER, Double.valueOf(ele));
            param.addValue(value);
            byte[] bodyBytes = param.getBytes(VirtualFsuUtil.BYTEORDER);
            int encrypt = collectorInfo.getEncrypt();
            if ((encrypt & 0x02) > 0)    // AES
                bodyBytes = Aes128.cfb8(collectorInfo.getKey(), 1, bodyBytes);
            Message mm = new Message();
            mm.setId(0x0401);
            mm.setSerialno(VirtualFsuUtil.serialNo.getAndIncrement());
            mm.wrapBody(bodyBytes);
            mm.setBodyLength(bodyBytes.length);
            mm.setEncrypt(encrypt);
            mm.setChecksum(mm.calcChecksum());
            byte[] mmBytes = mm.getBytes(VirtualFsuUtil.BYTEORDER, true);
            if (mmBytes != null) {
                writeAndFlush(mmBytes, ctx);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 获取断路器电量下限。
     *
     * @param switchCode 断路器编码。
     * @return 命令发送成功与否。
     */
    public static boolean getSwitchEleLower(String switchCode) {
        try {
            LocalDateTime now = LocalDateTime.now();
            Switch swt = VirtualFsuUtil.virtualFsuService.findSwitchBySwitchCode(switchCode);    // 对应的断路器
            if (swt == null)
                return false;
            long codeValueBreaker = Long.parseLong(swt.getCode(), 16);
            // 获取断路器对应的集中器
            Long codeValueCollector;
            try {
                codeValueCollector = Long.parseLong(swt.getCollector().getCode(), 16);
            } catch (Exception e) {
                codeValueCollector = null;
            }
            if (codeValueCollector == null) {
                System.out.printf("[%s] getSwitchEleLower - Failed to get Collector of Breaker(%012X)\n",
                        now,
                        codeValueBreaker);
                return false;
            }
            // 获取集中器信息对象
            CollectorInfo collectorInfo = VirtualFsuCollectorInfo.getCollectorInfo(codeValueCollector);
            if (collectorInfo == null) {
                System.out.printf("[%s] getSwitchEleLower - Failed to get CollectorInfo of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 获取集中器对应的连接
            ChannelHandlerContext ctx = collectorInfo.getChannelHandlerContext();
            if (ctx == null) {
                System.out.printf("[%s] getSwitchEleLower - Failed to get Socket of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 发送命令
            int paramId = 0x18;    // 电量下限
            MessageBodyQueryParam body = new MessageBodyQueryParam();
            body.setCode(codeValueBreaker);
            body.addId(paramId);
            byte[] bodyBytes = body.getBytes(VirtualFsuUtil.BYTEORDER);
            int encrypt = collectorInfo.getEncrypt();
            if ((encrypt & 0x02) > 0)    // AES
                bodyBytes = Aes128.cfb8(collectorInfo.getKey(), 1, bodyBytes);
            Message mm = new Message();
            mm.setId(0x0403);
            mm.setSerialno(VirtualFsuUtil.serialNo.getAndIncrement());
            mm.wrapBody(bodyBytes);
            mm.setBodyLength(bodyBytes.length);
            mm.setEncrypt(encrypt);
            mm.setChecksum(mm.calcChecksum());
            byte[] mmBytes = mm.getBytes(VirtualFsuUtil.BYTEORDER, true);
            if (mmBytes != null) {
                SwitchParam switchParam = VirtualFsuUtil.virtualFsuService.findSwitchParamBySwitchIDParamID(swt.getSwitchID(), paramId);
                if (switchParam == null) {
                    switchParam = new SwitchParam();
                    switchParam.setId(UUID.randomUUID().toString()
                            .replaceAll("-", ""));
                    switchParam.setSwitchs(swt);
                    switchParam.setParamID(paramId);
                }
                switchParam.setGenTime(Timestamp.valueOf(now));
                switchParam.setReturnTime(null);
                switchParam.setState(0);
                VirtualFsuUtil.virtualFsuService.addOrUpdateSwitchParam(switchParam);

                writeAndFlush(mmBytes, ctx);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 设置断路器电量上限。
     *
     * @param switchCode 断路器编码。
     * @param ele        电量上限(XXXXXX.XX)。
     * @return 命令发送成功与否。
     */
    public static boolean setSwitchEleUpper(String switchCode, double ele) {
        try {
            LocalDateTime now = LocalDateTime.now();
            Switch swt = VirtualFsuUtil.virtualFsuService.findSwitchBySwitchCode(switchCode);    // 对应的断路器
            if (swt == null)
                return false;
            long codeValueBreaker = Long.parseLong(swt.getCode(), 16);
            // 获取断路器对应的集中器
            Long codeValueCollector;
            try {
                codeValueCollector = Long.parseLong(swt.getCollector().getCode(), 16);
            } catch (Exception e) {
                codeValueCollector = null;
            }
            if (codeValueCollector == null) {
                System.out.printf("[%s] setSwitchEleUpper - Failed to get Collector of Breaker(%012X)\n",
                        now,
                        codeValueBreaker);
                return false;
            }
            // 获取集中器信息对象
            CollectorInfo collectorInfo = VirtualFsuCollectorInfo.getCollectorInfo(codeValueCollector);
            if (collectorInfo == null) {
                System.out.printf("[%s] setSwitchEleUpper - Failed to get CollectorInfo of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 获取集中器对应的连接
            ChannelHandlerContext ctx = collectorInfo.getChannelHandlerContext();
            if (ctx == null) {
                System.out.printf("[%s] setSwitchEleUpper - Failed to get Socket of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 发送命令
            MessageBodyParam param = new MessageBodyParam();
            param.setCode(codeValueBreaker);
            MessageBodyParamValue value = new MessageBodyParamValue(0x19L, 0);    // 电量上限
            value.setValue(VirtualFsuUtil.BYTEORDER, Double.valueOf(ele));
            param.addValue(value);
            byte[] bodyBytes = param.getBytes(VirtualFsuUtil.BYTEORDER);
            int encrypt = collectorInfo.getEncrypt();
            if ((encrypt & 0x02) > 0)    // AES
                bodyBytes = Aes128.cfb8(collectorInfo.getKey(), 1, bodyBytes);
            Message mm = new Message();
            mm.setId(0x0401);
            mm.setSerialno(VirtualFsuUtil.serialNo.getAndIncrement());
            mm.wrapBody(bodyBytes);
            mm.setBodyLength(bodyBytes.length);
            mm.setEncrypt(encrypt);
            mm.setChecksum(mm.calcChecksum());
            byte[] mmBytes = mm.getBytes(VirtualFsuUtil.BYTEORDER, true);
            if (mmBytes != null) {
                writeAndFlush(mmBytes, ctx);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 获取断路器电量上限。
     *
     * @param switchCode 断路器编码。
     * @return 命令发送成功与否。
     */
    public static boolean getSwitchEleUpper(String switchCode) {
        try {
            LocalDateTime now = LocalDateTime.now();
            Switch swt = VirtualFsuUtil.virtualFsuService.findSwitchBySwitchCode(switchCode);    // 对应的断路器
            if (swt == null)
                return false;
            long codeValueBreaker = Long.parseLong(swt.getCode(), 16);
            // 获取断路器对应的集中器
            Long codeValueCollector;
            try {
                codeValueCollector = Long.parseLong(swt.getCollector().getCode(), 16);
            } catch (Exception e) {
                codeValueCollector = null;
            }
            if (codeValueCollector == null) {
                System.out.printf("[%s] getSwitchEleUpper - Failed to get Collector of Breaker(%012X)\n",
                        now,
                        codeValueBreaker);
                return false;
            }
            // 获取集中器信息对象
            CollectorInfo collectorInfo = VirtualFsuCollectorInfo.getCollectorInfo(codeValueCollector);
            if (collectorInfo == null) {
                System.out.printf("[%s] getSwitchEleUpper - Failed to get CollectorInfo of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 获取集中器对应的连接
            ChannelHandlerContext ctx = collectorInfo.getChannelHandlerContext();
            if (ctx == null) {
                System.out.printf("[%s] getSwitchEleUpper - Failed to get Socket of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 发送命令
            int paramId = 0x19;    // 电量上限
            MessageBodyQueryParam body = new MessageBodyQueryParam();
            body.setCode(codeValueBreaker);
            body.addId(paramId);
            byte[] bodyBytes = body.getBytes(VirtualFsuUtil.BYTEORDER);
            int encrypt = collectorInfo.getEncrypt();
            if ((encrypt & 0x02) > 0)    // AES
                bodyBytes = Aes128.cfb8(collectorInfo.getKey(), 1, bodyBytes);
            Message mm = new Message();
            mm.setId(0x0403);
            mm.setSerialno(VirtualFsuUtil.serialNo.getAndIncrement());
            mm.wrapBody(bodyBytes);
            mm.setBodyLength(bodyBytes.length);
            mm.setEncrypt(encrypt);
            mm.setChecksum(mm.calcChecksum());
            byte[] mmBytes = mm.getBytes(VirtualFsuUtil.BYTEORDER, true);
            if (mmBytes != null) {
                SwitchParam switchParam = VirtualFsuUtil.virtualFsuService.findSwitchParamBySwitchIDParamID(swt.getSwitchID(), paramId);
                if (switchParam == null) {
                    switchParam = new SwitchParam();
                    switchParam.setId(UUID.randomUUID().toString()
                            .replaceAll("-", ""));
                    switchParam.setSwitchs(swt);
                    switchParam.setParamID(paramId);
                }
                switchParam.setGenTime(Timestamp.valueOf(now));
                switchParam.setReturnTime(null);
                switchParam.setState(0);
                VirtualFsuUtil.virtualFsuService.addOrUpdateSwitchParam(switchParam);

                writeAndFlush(mmBytes, ctx);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 设置断路器地址（即编码）。
     *
     * @param switchCode    断路器编码。
     * @param switchNewCode 断路器新编码。
     * @return 命令发送成功与否。
     */
    public static boolean setSwitchCode(String switchCode, String switchNewCode) {
        try {
            LocalDateTime now = LocalDateTime.now();
            Switch swt = VirtualFsuUtil.virtualFsuService.findSwitchBySwitchCode(switchCode);    // 对应的断路器
            if (swt == null)
                return false;
            long codeValueBreaker = Long.parseLong(swt.getCode(), 16);
            long codeNewValue = Long.parseLong(switchNewCode, 16);
            long cv = codeNewValue;
            byte[] codeNewBytes = new byte[6];
            for (int i = codeNewBytes.length - 1; i >= 0; i--) {
                codeNewBytes[i] = (byte) (cv & 0xFF);
                cv = cv >> 8;
            }
            // 获取断路器对应的集中器
            Long codeValueCollector;
            try {
                codeValueCollector = Long.parseLong(swt.getCollector().getCode(), 16);
            } catch (Exception e) {
                codeValueCollector = null;
            }
            if (codeValueCollector == null) {
                System.out.printf("[%s] setSwitchCode - Failed to get Collector of Breaker(%012X)\n",
                        now,
                        codeValueBreaker);
                return false;
            }
            // 获取集中器信息对象
            CollectorInfo collectorInfo = VirtualFsuCollectorInfo.getCollectorInfo(codeValueCollector);
            if (collectorInfo == null) {
                System.out.printf("[%s] setSwitchCode - Failed to get CollectorInfo of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 获取集中器对应的连接
            ChannelHandlerContext ctx = collectorInfo.getChannelHandlerContext();
            if (ctx == null) {
                System.out.printf("[%s] setSwitchCode - Failed to get Socket of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 发送命令
            MessageBodyParam param = new MessageBodyParam();
            param.setCode(codeValueBreaker);
            MessageBodyParamValue value = new MessageBodyParamValue(0x04L, 0);    // 地址
            value.setValue(VirtualFsuUtil.BYTEORDER, codeNewBytes);
            param.addValue(value);
            byte[] bodyBytes = param.getBytes(VirtualFsuUtil.BYTEORDER);
            int encrypt = collectorInfo.getEncrypt();
            if ((encrypt & 0x02) > 0)    // AES
                bodyBytes = Aes128.cfb8(collectorInfo.getKey(), 1, bodyBytes);
            Message mm = new Message();
            mm.setId(0x0401);
            mm.setSerialno(VirtualFsuUtil.serialNo.getAndIncrement());
            mm.wrapBody(bodyBytes);
            mm.setBodyLength(bodyBytes.length);
            mm.setEncrypt(encrypt);
            mm.setChecksum(mm.calcChecksum());
            byte[] mmBytes = mm.getBytes(VirtualFsuUtil.BYTEORDER, true);
            if (mmBytes != null) {
                writeAndFlush(mmBytes, ctx);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 刷新获取断路器最新数据。
     *
     * @param switchCode 断路器编码。
     * @return 命令发送成功与否。
     */
    public static boolean refreshSwitchData(String switchCode) {
        try {
            LocalDateTime now = LocalDateTime.now();
            Switch swt = VirtualFsuUtil.virtualFsuService.findSwitchBySwitchCode(switchCode);    // 对应的断路器
            if (swt == null)
                return false;
            long codeValueBreaker = Long.parseLong(swt.getCode(), 16);
            // 获取断路器对应的集中器
            Long codeValueCollector;
            try {
                codeValueCollector = Long.parseLong(swt.getCollector().getCode(), 16);
            } catch (Exception e) {
                codeValueCollector = null;
            }
            if (codeValueCollector == null) {
                System.out.printf("[%s] refreshSwitchData - Failed to get Collector of Breaker(%012X)\n",
                        now,
                        codeValueBreaker);
                return false;
            }
            // 获取集中器信息对象
            CollectorInfo collectorInfo = VirtualFsuCollectorInfo.getCollectorInfo(codeValueCollector);
            if (collectorInfo == null) {
                System.out.printf("[%s] refreshSwitchData - Failed to get CollectorInfo of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 获取集中器对应的连接
            ChannelHandlerContext ctx = collectorInfo.getChannelHandlerContext();
            if (ctx == null) {
                System.out.printf("[%s] refreshSwitchData - Failed to get Socket of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 发送命令
            MessageBodyBreakerRefresh body = new MessageBodyBreakerRefresh();
            body.setBrkCode(codeValueBreaker);
            byte[] bodyBytes = body.getBytes();
            int encrypt = collectorInfo.getEncrypt();
            if ((encrypt & 0x02) > 0)    // AES
                bodyBytes = Aes128.cfb8(collectorInfo.getKey(), 1, bodyBytes);
            Message mm = new Message();
            mm.setId(0x0415);
            mm.setSerialno(VirtualFsuUtil.serialNo.getAndIncrement());
            mm.wrapBody(bodyBytes);
            mm.setBodyLength(bodyBytes.length);
            mm.setEncrypt(encrypt);
            mm.setChecksum(mm.calcChecksum());
            byte[] mmBytes = mm.getBytes(VirtualFsuUtil.BYTEORDER, true);
            if (mmBytes != null) {
                writeAndFlush(mmBytes, ctx);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 获取断路器故障。
     *
     * @param codeValueBreaker 断路器地址值。
     * @param encrypt          数据加密方式。
     * @param key              数据加密密钥。
     * @return 命令发送成功与否。
     * @ctx 对应的连接。
     */
    public static boolean switchFault(long codeValueBreaker, int encrypt, final byte[] key, ChannelHandlerContext ctx) {
        try {
            MessageBodyQueryBreakerFault body = new MessageBodyQueryBreakerFault();
            body.setCode(codeValueBreaker);
            byte[] bodyBytes = body.getBytes(VirtualFsuUtil.BYTEORDER);
            if ((encrypt & 0x02) > 0)    // AES
                bodyBytes = Aes128.cfb8(key, 1, bodyBytes);
            Message mm = new Message();
            mm.setId(0x0505);
            mm.setSerialno(VirtualFsuUtil.serialNo.getAndIncrement());
            mm.wrapBody(bodyBytes);
            mm.setBodyLength(bodyBytes.length);
            mm.setEncrypt(encrypt);
            mm.setChecksum(mm.calcChecksum());
            byte[] mmBytes = mm.getBytes(VirtualFsuUtil.BYTEORDER, true);
            if (mmBytes != null) {
                writeAndFlush(mmBytes, ctx);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取断路器故障。
     *
     * @param switchCode 断路器编码。
     * @return 命令发送成功与否。
     */
    public static boolean getSwitchFault(String switchCode) {
        try {
            Switch swt = VirtualFsuUtil.virtualFsuService.findSwitchBySwitchCode(switchCode);    // 对应的断路器
            if (swt == null)
                return false;
            else
                return getSwitchFault(swt);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取断路器故障。
     *
     * @param swt 断路器。
     * @return 命令发送成功与否。
     */
    public static boolean getSwitchFault(Switch swt) {
        try {
            LocalDateTime now = LocalDateTime.now();
            long codeValueBreaker = Long.parseLong(swt.getCode(), 16);
            // 获取断路器对应的集中器
            Long codeValueCollector;
            try {
                codeValueCollector = Long.parseLong(swt.getCollector().getCode(), 16);
            } catch (Exception e) {
                codeValueCollector = null;
            }
            if (codeValueCollector == null) {
                System.out.printf("[%s] getSwitchFault - Failed to get Collector of Breaker(%012X)\n",
                        now,
                        codeValueBreaker);
                return false;
            }
            // 获取集中器信息对象
            CollectorInfo collectorInfo = VirtualFsuCollectorInfo.getCollectorInfo(codeValueCollector);
            if (collectorInfo == null) {
                System.out.printf("[%s] getSwitchFault - Failed to get CollectorInfo of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 获取集中器对应的连接
            ChannelHandlerContext ctx = collectorInfo.getChannelHandlerContext();
            if (ctx == null) {
                System.out.printf("[%s] getSwitchFault - Failed to get Socket of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 发送命令
            return switchFault(codeValueBreaker, collectorInfo.getEncrypt(), collectorInfo.getKey(), ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 获取集中器的所有断路器故障。
     *
     * @param collectorCode 集中器编码。
     * @return 命令发送成功与否。
     */
    public static boolean getCollectorFault(String collectorCode) {
        try {
            Collector collector = VirtualFsuUtil.virtualFsuService.findCollectorByCode(collectorCode);    // 对应的集中器
            if (collector == null)
                return false;
            else
                return getCollectorFault(collector);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取集中器的所有断路器故障。
     *
     * @param collector 集中器。
     * @return 命令发送成功与否。
     */
    public static boolean getCollectorFault(Collector collector) {
        try {
            LocalDateTime now = LocalDateTime.now();
            Long codeValueCollector = Long.parseLong(collector.getCode(), 16);
            // 获取集中器信息对象
            CollectorInfo collectorInfo = VirtualFsuCollectorInfo.getCollectorInfo(codeValueCollector);
            if (collectorInfo == null) {
                System.out.printf("[%s] getCollectorFault - Failed to get CollectorInfo of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 获取集中器对应的连接
            ChannelHandlerContext ctx = collectorInfo.getChannelHandlerContext();
            if (ctx == null) {
                System.out.printf("[%s] getCollectorFault - Failed to get Socket of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 发送命令
            MessageBodyQueryCollectorFault body = new MessageBodyQueryCollectorFault();
            body.setCode(codeValueCollector);
            byte[] bodyBytes = body.getBytes(VirtualFsuUtil.BYTEORDER);
            int encrypt = collectorInfo.getEncrypt();
            if ((encrypt & 0x02) > 0)    // AES
                bodyBytes = Aes128.cfb8(collectorInfo.getKey(), 1, bodyBytes);
            Message mm = new Message();
            mm.setId(0x0506);
            mm.setSerialno(VirtualFsuUtil.serialNo.getAndIncrement());
            mm.wrapBody(bodyBytes);
            mm.setBodyLength(bodyBytes.length);
            mm.setEncrypt(encrypt);
            mm.setChecksum(mm.calcChecksum());
            byte[] mmBytes = mm.getBytes(VirtualFsuUtil.BYTEORDER, true);
            if (mmBytes != null) {
//				collector.setFaultState(0);
//				collector.setFaultTime(Timestamp.valueOf(now));
//				VirtualFsuUtil.virtualFsuService.addOrUpdateCollector(collector);
                VirtualFsuUtil.virtualFsuService.updateCollectorFault(collector, 0, now);

                writeAndFlush(mmBytes, ctx);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 向集中器发送配置信息。
     *
     * @param collectorInfo 集中器信息。
     * @param collector     集中器记录对象。
     * @param ctx           对应的连接。
     * @return 发送成功与否。
     */
    public static boolean setCollectorConfig(CollectorInfo collectorInfo, final Collector collector, ChannelHandlerContext ctx) {
        if (ctx == null)
            return false;
        try {
            long codeValueCollector = collectorInfo.getCodeValue();
            int encrypt = collectorInfo.getEncrypt();
            // 配置信息发送给集中器
            MessageBodyConfig body = new MessageBodyConfig();
            body.setClctCode(codeValueCollector);
            body.setBaud(collector.getBaud());
            body.setFreq(collector.getFreq());
            body.setRange((byte) collector.getRanges());
            body.setHbFreq(collector.getHBFreq());
            List<Switch> switchs = VirtualFsuUtil.virtualFsuService.findSwitchByCollectorIDOrderByCode(collector.getCollectorID());
            int breakerCount = 0;
            if (switchs != null) {
                Long brkCodeValue;
                for (Switch swt : switchs) {
                    brkCodeValue = Long.parseLong(swt.getCode(), 16);
                    body.addBreaker(brkCodeValue);
                    breakerCount++;
                }
            }
            collectorInfo.setBreakerCount(breakerCount);
            body.setKey(collectorInfo.getKey());
            byte[] bodyBytes = body.getBytes(VirtualFsuUtil.BYTEORDER);
            if ((encrypt & 0x02) > 0)
                bodyBytes = Aes128.cfb8(VirtualFsuUtil.CONFIG_KEY, 1, bodyBytes);
            Message mm = new Message();
            mm.setId(0x0409);
            mm.setSerialno(VirtualFsuUtil.serialNo.getAndIncrement());
            mm.setBodyLength(bodyBytes.length);
            mm.setEncrypt(encrypt);
            mm.wrapBody(bodyBytes);
            mm.setChecksum(mm.calcChecksum());
            byte[] mmBytes = mm.getBytes(VirtualFsuUtil.BYTEORDER, true);
            if (mmBytes != null) {
                writeAndFlush(mmBytes, ctx);
//				if(breakerCount > 0) {
//					System.out.printf("[%s] %s CollectorConfig = %s\n",
//							LocalDateTime.now().toString(),
//							ctx.channel().remoteAddress().toString(),
//							body.toString());
//				}
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 向集中器发送配置信息。
     *
     * @param collector 集中器记录对象。
     * @return 发送成功与否。
     */
    public static boolean setCollectorConfig(final Collector collector) {
        try {
//			LocalDateTime now = LocalDateTime.now();
            Long codeValueCollector = Long.parseLong(collector.getCode(), 16);
            // 获取集中器信息对象
            CollectorInfo collectorInfo = VirtualFsuCollectorInfo.getCollectorInfo(codeValueCollector);
            if (collectorInfo == null) {    // 集中器尚未登录过。
//				System.out.printf("[%s] setCollectorConfig - Failed to get CollectorInfo of Collector(%012X)\n",
//						now,
//						codeValueCollector);
                return false;
            }
            // 获取集中器对应的连接
            ChannelHandlerContext ctx = collectorInfo.getChannelHandlerContext();
            if (ctx == null) {    // 集中器当前不在线。
//				System.out.printf("[%s] setCollectorConfig - Failed to get Socket of Collector(%012X)\n",
//						now,
//						codeValueCollector);
                return false;
            }
            // 发送配置
            return setCollectorConfig(collectorInfo, collector, ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 向集中器发送RTC。
     *
     * @param ctx 集中器对应的Socket连接。
     * @return 发送成功与否。
     */
    public static boolean collectorHeartbeat(ChannelHandlerContext ctx) {
        try {
            // 心跳消息
            Message mm = new Message();
            mm.setId(0x0001);
            mm.setSerialno(VirtualFsuUtil.serialNo.getAndIncrement());
            mm.setBodyLength(0);
            mm.setChecksum(mm.calcChecksum());
            byte[] mmBytes = mm.getBytes(VirtualFsuUtil.BYTEORDER, true);
            if (mmBytes != null) {
                writeAndFlush(mmBytes, ctx);
                return true;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return false;
    }

    /**
     * 向集中器发送RTC。
     *
     * @param ctx 集中器对应的Socket连接。
     * @return 发送成功与否。
     */
    public static boolean setCollectorRTC(ChannelHandlerContext ctx) {
        try {
            long codeValue = 0L;
            MessageBodyParam param = new MessageBodyParam();
            param.setCode(codeValue);
            MessageBodyParamValue value = new MessageBodyParamValue(
                    0x03, 0); // 设置RTC时间
            value.setValue(VirtualFsuUtil.BYTEORDER, LocalDateTime.now());
            param.addValue(value);
            byte[] bodyBytes = param.getBytes(VirtualFsuUtil.BYTEORDER);
            // RTC消息不加密。
            Message mm = new Message();
            mm.setId(0x0401);
            mm.setSerialno(VirtualFsuUtil.serialNo.getAndIncrement());
            mm.wrapBody(bodyBytes);
            mm.setBodyLength(bodyBytes.length);
            mm.setChecksum(mm.calcChecksum());
            byte[] mmBytes = mm.getBytes(VirtualFsuUtil.BYTEORDER, true);
            if (mmBytes != null) {
                writeAndFlush(mmBytes, ctx);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 向集中器发送工作服务器信息。
     *
     * @param ctx       集中器对应的Socket连接。
     * @param collector 集中器记录对象。
     * @return 发送成功与否。
     */
    public static boolean setCollectorWork(ChannelHandlerContext ctx, final Collector collector) {
        try {
            //工作服务器信息发送给集中器
            long clctCodeValue = Long.parseLong(collector.getCode(), 16);
            MessageBodyWork body = new MessageBodyWork();
            body.setClctCode(clctCodeValue);
			body.setIp(collector.getServer().getIp());
			body.setPort(collector.getServer().getPort());
//			System.out.printf("MessageBodyWork = %s\n", body.toString());
            byte[] bodyBytes = body.getBytes(VirtualFsuUtil.BYTEORDER);
            bodyBytes = Aes128.cfb8(VirtualFsuUtil.CONFIG_KEY, 1, bodyBytes);
            Message mm = new Message();
            mm.setId(0x040A);
            mm.setSerialno(VirtualFsuUtil.serialNo.getAndIncrement());
            mm.wrapBody(bodyBytes);
            mm.setBodyLength(bodyBytes.length);
            mm.setEncrypt(0x02);
            mm.setChecksum(mm.calcChecksum());
            byte[] mmBytes = mm.getBytes(VirtualFsuUtil.BYTEORDER, true);
            if (mmBytes != null) {
                writeAndFlush(mmBytes, ctx);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 设置集中器的定时操作。
     *
     * @param collector 集中器记录对象。
     * @return 上传个数。<0表示失败。
     */
    public static int setCollectorTimer(Collector collector) {
        try {
            LocalDateTime now = LocalDateTime.now();
            Long codeValueCollector = Long.parseLong(collector.getCode(), 16);
            // 获取集中器信息对象
            CollectorInfo collectorInfo = VirtualFsuCollectorInfo.getCollectorInfo(codeValueCollector);
            if (collectorInfo == null) {
                System.out.printf("[%s] setCollectorTimer - Failed to get CollectorInfo of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return -2;
            }
            // 获取集中器对应的连接
            ChannelHandlerContext ctx = collectorInfo.getChannelHandlerContext();
            if (ctx == null) {
                System.out.printf("[%s] setCollectorTimer - Failed to get Socket of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return -1;
            }
            // 比较集中器编码
            if (codeValueCollector != collectorInfo.getCodeValue()) {
                System.out.printf("[%s] setCollectorTimer - Unexpected Collector(%012X <> %012X)\n",
                        now,
                        codeValueCollector,
                        collectorInfo.getCodeValue());
                return -3;
            }
            // 对应的结果记录
            CollectorTimerUpload collectorTimerUpload = null;
            collectorTimerUpload = VirtualFsuUtil.virtualFsuService.findTimerUploadByCollectorID(collector.getCollectorID());
            if (collectorTimerUpload == null) {
                collectorTimerUpload = new CollectorTimerUpload();
                collectorTimerUpload.setId(0L);
                collectorTimerUpload.setCollector(collector);
            }
            // 上传
            int count = setCollectorTimer(collectorInfo, collectorTimerUpload, 0);
            return count < 0 ? count - 10 : count;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -5;
    }

    /**
     * 设置集中器的定时操作失败后重新执行。
     *
     * @param collectorTimerUpload 失败的上传记录。
     * @return 上传个数。<0表示失败。
     */
    public static int setCollectorTimer(CollectorTimerUpload collectorTimerUpload) {
        try {
            LocalDateTime now = LocalDateTime.now();
            Long codeValueCollector = Long.parseLong(collectorTimerUpload.getCollector().getCode(), 16);
            // 获取集中器信息对象
            CollectorInfo collectorInfo = VirtualFsuCollectorInfo.getCollectorInfo(codeValueCollector);
            if (collectorInfo == null) {
                System.out.printf("[%s] setCollectorTimer - Failed to get CollectorInfo of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return -2;
            }
            // 获取集中器对应的连接
            ChannelHandlerContext ctx = collectorInfo.getChannelHandlerContext();
            if (ctx == null) {
                System.out.printf("[%s] setCollectorTimer - Failed to get Socket of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return -1;
            }
            // 比较集中器编码
            if (codeValueCollector != collectorInfo.getCodeValue()) {
                System.out.printf("[%s] setCollectorTimer - Unexpected Collector(%012X <> %012X)\n",
                        now,
                        codeValueCollector,
                        collectorInfo.getCodeValue());
                return -3;
            }
            // 上传
            int count = setCollectorTimer(collectorInfo, collectorTimerUpload, collectorTimerUpload.getFail() + 1);
            return count < 0 ? count - 10 : count;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -5;
    }

    /**
     * 上传集中器定时设置。
     *
     * @param collectorInfo        集中器信息。
     * @param collectorTimerUpload 上传记录。
     * @param fail                 失败次数。
     * @return 上传个数。<0表示失败。
     */
    private static int setCollectorTimer(CollectorInfo collectorInfo, CollectorTimerUpload collectorTimerUpload, int fail) {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<TimeController> timeControllers = VirtualFsuUtil.virtualFsuService.findCollectorTimeController(collectorTimerUpload.getCollector().getCollectorID());
            if (timeControllers == null)
                return -1;
            MessageBodyCollectorTimer bodyCollectorTimer = new MessageBodyCollectorTimer();
            LocalTime time;
            int count = 0;
            for (TimeController timeController : timeControllers) {
                if (timeController.getState() != 0 &&    // 有效
                        timeController.getUpload() != 0 &&    // 上传集中器
                        timeController.getWeekday() != 0 &&    // 重复执行
                        count < MAX_UPLOAD_TIMERS) {    // 个数限制
                    MessageBodyBreakerTimer bodyBreakerTimer = new MessageBodyBreakerTimer();
                    bodyBreakerTimer.setBrkCode(Long.parseLong(timeController.getSwitchs().getCode(), 16));
                    bodyBreakerTimer.setOn(timeController.getCmdData() == 0 ? false : true);
                    time = timeController.getRunTime().toLocalTime();
                    bodyBreakerTimer.setHour((byte) time.getHour());
                    bodyBreakerTimer.setMinute((byte) time.getMinute());
                    bodyBreakerTimer.setActiveWeekDays(timeController.getWeekday());
                    bodyCollectorTimer.addBreakerTimer(bodyBreakerTimer);
                    count++;
                }
            }
            byte[] bodyBytes = bodyCollectorTimer.getBytes(VirtualFsuUtil.BYTEORDER);
            int encrypt = collectorInfo.getEncrypt();
            if ((encrypt & 0x02) > 0)    // AES
                bodyBytes = Aes128.cfb8(collectorInfo.getKey(), 1, bodyBytes);
            Message mm = new Message();
            mm.setId(0x0406);
            mm.setSerialno(VirtualFsuUtil.serialNo.getAndIncrement());
            mm.wrapBody(bodyBytes);
            mm.setBodyLength(bodyBytes.length);
            mm.setEncrypt(encrypt);
            mm.setChecksum(mm.calcChecksum());
            byte[] mmBytes = mm.getBytes(VirtualFsuUtil.BYTEORDER, true);
            if (mmBytes != null) {
                // 对应的结果记录
                collectorTimerUpload.setMsgId(mm.getId());
                collectorTimerUpload.setMsgNo(mm.getSerialno());
                collectorTimerUpload.setUploadTime(Timestamp.valueOf(now));
                collectorTimerUpload.setResultTime(null);
                collectorTimerUpload.setResult(null);
                collectorTimerUpload.setFail(fail);
                collectorTimerUpload = VirtualFsuUtil.virtualFsuService.addOrUpdateCollectorTimerUpload(collectorTimerUpload);
                // 发送
                writeAndFlush(mmBytes, collectorInfo.getChannelHandlerContext());
                return count;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -2;
    }

    /**
     * 重启集中器。
     *
     * @param collector 集中器记录对象。
     * @return 发送成功与否。
     */
    public static boolean rebootCollector(final Collector collector) {
        try {
            LocalDateTime now = LocalDateTime.now();
            Long codeValueCollector = Long.parseLong(collector.getCode(), 16);
            // 获取集中器信息对象
            CollectorInfo collectorInfo = VirtualFsuCollectorInfo.getCollectorInfo(codeValueCollector);
            if (collectorInfo == null) {
                System.out.printf("[%s] rebootCollector - Failed to get CollectorInfo of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 获取集中器对应的连接
            ChannelHandlerContext ctx = collectorInfo.getChannelHandlerContext();
            if (ctx == null) {
                System.out.printf("[%s] rebootCollector - Failed to get Socket of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 发送命令
            MessageBodyCollectorControl body = new MessageBodyCollectorControl();
            body.setCmdReboot();
            byte[] bodyBytes = body.getBytes();
            int encrypt = collectorInfo.getEncrypt();
            if ((encrypt & 0x02) > 0)    // AES
                bodyBytes = Aes128.cfb8(collectorInfo.getKey(), 1, bodyBytes);
            Message mm = new Message();
            mm.setId(0x0302);
            mm.setSerialno(VirtualFsuUtil.serialNo.getAndIncrement());
            mm.wrapBody(bodyBytes);
            mm.setBodyLength(bodyBytes.length);
            mm.setEncrypt(encrypt);
            mm.setChecksum(mm.calcChecksum());
            byte[] mmBytes = mm.getBytes(VirtualFsuUtil.BYTEORDER, true);
            if (mmBytes != null) {
                writeAndFlush(mmBytes, ctx);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 集中器恢复出厂设置。
     *
     * @param collector 集中器记录对象。
     * @return 发送成功与否。
     */
    public static boolean resetCollector(final Collector collector) {
        try {
            LocalDateTime now = LocalDateTime.now();
            Long codeValueCollector = Long.parseLong(collector.getCode(), 16);
            // 获取集中器信息对象
            CollectorInfo collectorInfo = VirtualFsuCollectorInfo.getCollectorInfo(codeValueCollector);
            if (collectorInfo == null) {
                System.out.printf("[%s] resetCollector - Failed to get CollectorInfo of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 获取集中器对应的连接
            ChannelHandlerContext ctx = collectorInfo.getChannelHandlerContext();
            if (ctx == null) {
                System.out.printf("[%s] resetCollector - Failed to get Socket of Collector(%012X)\n",
                        now,
                        codeValueCollector);
                return false;
            }
            // 发送命令
            MessageBodyCollectorControl body = new MessageBodyCollectorControl();
            body.setCmdReset();
            byte[] bodyBytes = body.getBytes();
            int encrypt = collectorInfo.getEncrypt();
            if ((encrypt & 0x02) > 0)    // AES
                bodyBytes = Aes128.cfb8(collectorInfo.getKey(), 1, bodyBytes);
            Message mm = new Message();
            mm.setId(0x0302);
            mm.setSerialno(VirtualFsuUtil.serialNo.getAndIncrement());
            mm.wrapBody(bodyBytes);
            mm.setBodyLength(bodyBytes.length);
            mm.setEncrypt(encrypt);
            mm.setChecksum(mm.calcChecksum());
            byte[] mmBytes = mm.getBytes(VirtualFsuUtil.BYTEORDER, true);
            if (mmBytes != null) {
                writeAndFlush(mmBytes, ctx);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 发送网络数据。
     *
     * @param bytes 要发送的数据。
     * @param ctx   通道。
     */
    private static void writeAndFlush(byte[] bytes, ChannelHandlerContext ctx) {
        ByteBuf bfCmd = Unpooled.wrappedBuffer(bytes);
        ctx.writeAndFlush(bfCmd);
        CollectorInfo collectorInfo = CollectorChannelHandler.getCollectorInfoOfCtx(ctx);
        if (collectorInfo != null) {
            collectorInfo.addByteWrite(bytes.length);
            collectorInfo.addPacketWrite(1);
        }
    }
}
