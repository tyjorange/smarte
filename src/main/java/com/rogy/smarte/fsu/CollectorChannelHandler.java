package com.rogy.smarte.fsu;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.rogy.smarte.entity.db1.*;
import com.rogy.smarte.fsu.message.*;
import com.rogy.smarte.util.Aes128;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.AttributeMap;

/**
 * 集中器Socket连接的处理类。
 */
@Sharable
public class CollectorChannelHandler extends
//		SimpleChannelInboundHandler<ByteBuf> {
        SimpleChannelInboundHandler<Message> {

    /**
     * 时间偏差限制(秒)。
     * 如果集中器时间与服务器时间偏差超过此数值，则认为集中器时钟错误。
     */
    private static final int COLLECTOR_TIMEERR_SECONDS = 2 * 60;

    /**
     * 集中器请求配置的时间间隔。
     * 为防止集中器频繁请求配置信息，在同一网络连接中的配置请求间隔秒数需要超过此数值。
     */
    private static final int COLLECTOR_CONFIG_SECONDS = 30;

    /**
     * 保存集中器地址数值的属性KEY。
     */
    private static final AttributeKey<CollectorInfo> keyCollectorInfo =
            AttributeKey.valueOf("CollectorInfo");

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 和集中器建立连接
        // 向集中器发送RTC
        VirtualFsuController.setCollectorRTC(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx)
            throws Exception {
        LocalDateTime now = LocalDateTime.now();
        // 从本连接ctx中移除其对应的集中器信息对象。
        CollectorInfo collectorInfo = removeCollectorInfoOfCtx(ctx);
        if (collectorInfo != null) {
            // 把集中器信息对象中连接ctx属性设置为null
            // 注意，如果集中器信息对象中连接ctx属性已经指向了新的连接，则不能设成null！
            if (collectorInfo.inactiveChannelHandlerContext(ctx)) {    // 还没有重新连接。设成了null。
                // 更新集中器活跃状态-离线
                updateCollectorActive(collectorInfo.getCollectID(), 0, now);
                // 添加离线记录
                collectorOnline(collectorInfo.getCollectID(), false, now, -4);
                if (collectorInfo.getBreakerCount() > 0)
                    System.out.printf("[%s] Collector(%s) disconnected.\n",
                            now,
                            String.format("%012X", collectorInfo.getCodeValue()));
            }
        }

        //super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        LocalDateTime now = LocalDateTime.now();
        CollectorInfo collectorInfo = getCollectorInfoOfCtx(ctx);
        if (collectorInfo != null) {
            // 如果本ctx和集中器信息对象中记录的连接ctx不一致，
            // 则说明该集中器已经重新上线了，本ctx是老的连接。
            // 如果本ctx和集中器信息对象中记录的连接ctx一致，
            // 则说明本ctx就是该集中器的最新连接。
            if (ctx == collectorInfo.getChannelHandlerContext()) {
                // 添加离线记录
                collectorOnline(collectorInfo.getCollectID(), false, now, -3);
                System.out.printf("[%s] Collector(%s) exception : %s\n",
                        now,
                        String.format("%012X", collectorInfo.getCodeValue()),
                        cause.getMessage());
            }
            // 记录Exception次数，不论是否重连了。
            VirtualFsuUtil.virtualFsuService.incCollectorException(collectorInfo.getCollectID());
        }
        try {
            // Socket Exception，立即强制关闭。
            ctx.close();
        } catch (Exception e) {
        }

        //super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message msg) {
        try {
            MessageHandler(ctx, msg);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    /**
     * 消息处理。
     *
     * @param ctx
     * @param mm  消息。
     */
    private static void MessageHandler(ChannelHandlerContext ctx, Message mm)
            throws Exception {
        LocalDateTime now = LocalDateTime.now();
        int id = mm.getId();
        byte[] body = decryptBody(ctx, mm);
        int encrypt = mm.getEncrypt();
        if (id == Message.ID_ERROR_MESSAGE) {    // SOCKET数据错误
            errorPacket(ctx, mm);
        } else if (id == 0x0001) {
            collectorHeartbeat(ctx);
        } else if (id == 0x0003) { // 终端参数
            if (body == null)
                return;
            MessageBodyParam param = MessageBodyParam.create(
                    VirtualFsuUtil.BYTEORDER, body);
            if (param == null) {
                System.out.printf("[%s] data error.\n", now.toString());
            } else {
                if (VirtualFsuUtil.DEBUG) {
                    System.out.printf("[%s] %s\n", now.toString(),
                            param.toString());
                } else {
                    switchParam(ctx, param);
                }
            }
        } else if (id == 0x0004) { // 断路器数据
            if (body == null)
                return;
            MessageBodyData data = MessageBodyData.create(
                    VirtualFsuUtil.BYTEORDER, body);
            //System.out.printf("data ==== %s\n" , data);
            if (data == null) {
                System.out.printf("[%s] data error.\n", now.toString());
            } else {
                if (VirtualFsuUtil.DEBUG) {
                    System.out.printf("[%s] %s\n", now.toString(),
                            data.toString());
                } else {
                    switchData(ctx, data);
                }
            }
        } else if (id == 0x0005) { // 断路器数据变化
            if (body == null)
                return;
            MessageBodyDataChange dataChange = MessageBodyDataChange.create(
                    VirtualFsuUtil.BYTEORDER, body);
            //System.out.printf("datachange ==== %s\n", dataChange);
            if (dataChange == null) {
                System.out.printf("[%s] dataChange error.\n", now.toString());
            } else {
                if (VirtualFsuUtil.DEBUG) {
                    System.out.printf("[%s] %s\n", now.toString(),
                            dataChange.toString());
                } else {
                    switchData(ctx, dataChange);
                }
            }
        } else if (id == 0x0505) { // 断路器故障
            if (body == null)
                return;
            MessageBodyBreakerFault fault = MessageBodyBreakerFault.create(
                    VirtualFsuUtil.BYTEORDER, body);
            switchFault(ctx, fault);
        } else if (id == 0x0506) { // 集中器故障
            if (body == null)
                return;
            MessageBodyCollectorFault fault = MessageBodyCollectorFault.create(
                    VirtualFsuUtil.BYTEORDER, body);
            collectorFault(ctx, fault);
        } else if (id == 0x8001 || id == 0x8002) { // 通用应答
            if (body == null)
                return;
            MessageBodyCommonAns ans = MessageBodyCommonAns.create(
                    VirtualFsuUtil.BYTEORDER, body);
            if (id == 0x8001)
                commonAnswerBreaker(ctx, ans);
            else if (id == 0x8002)
                commonAnswerCollector(ctx, ans);
        } else if (id == 0x0407) { // 请求RTC时间
            VirtualFsuController.setCollectorRTC(ctx);
        } else if (id == 0x040A) { // 请求集中器工作服务器信息
            if (body == null)
                return;
            MessageBodyQueryWork qw = MessageBodyQueryWork.create(
                    VirtualFsuUtil.BYTEORDER, body);
            collectorWork(ctx, qw);
        } else if (id == 0x0408) { // 请求集中器配置信息
            if (body == null)
                return;
            MessageBodyQueryConfig qc = MessageBodyQueryConfig.create(
                    VirtualFsuUtil.BYTEORDER, body);
            collectorConfig(ctx, qc, encrypt);
        } else {
            System.out.printf("[%s] message invalid id(%d=0x%s).\n",
                    now.toString(), id, Integer.toHexString(id).toUpperCase());
        }
    }

    /**
     * 对消息的消息体进行解密。
     * 注意：消息的原消息体并没有改变。
     *
     * @param ctx Socket连接。
     * @param mm  消息。
     * @return 解密后的消息体。
     */
    private static byte[] decryptBody(ChannelHandlerContext ctx, Message mm) {
        int id = mm.getId();
        byte[] body = mm.getBody();
        if (body == null)
            return null;
        int encrypt = mm.getEncrypt();
        if (encrypt == 0)    // 未加密
            return body;
        if (id == 0x0001) {    // 心跳，不需要处理消息体。
            return body;
        } else if (id == 0x0408 ||    // 请求集中器配置信息
                id == 0x040A) { // 请求集中器的工作服务器信息
//			System.out.printf("[%s] RequestConfig/Work encrypt = %d\n",
//					LocalDateTime.now().toString(),
//					encrypt);
            if ((encrypt & 0x02) > 0)    // AES
                return Aes128.cfb8(VirtualFsuUtil.CONFIG_KEY, 0, body);    // 解密
            else
                return body;
        } else {
            CollectorInfo collectorInfo = getCollectorInfoOfCtx(ctx);
            if (collectorInfo == null) {
                System.out.printf("[%s] CollectorInfo null for message(%04x) from %s\n",
                        LocalDateTime.now().toString(),
                        id,
                        ctx.channel().remoteAddress().toString());
                return null;
            } else {
                if ((encrypt & 0x02) > 0)    // AES
                    return Aes128.cfb8(collectorInfo.getKey(), 0, body);    // 解密
                else
                    return body;
            }
        }
    }

    /**
     * 错误数据包处理。
     *
     * @param ctx 对应的网络连接。
     * @param mm  错误消息。
     */
    private static void errorPacket(ChannelHandlerContext ctx, Message mm) {
        LocalDateTime now = LocalDateTime.now();
        CollectorInfo collectorInfo = getCollectorInfoOfCtx(ctx);
        if (collectorInfo != null) {
            if (ctx == collectorInfo.getChannelHandlerContext()) {
                System.out.printf("[%s] Error packet(len=%d). connection %s%s\n",
                        now,
                        mm.getBodyLength(),
                        ctx.channel().remoteAddress().toString(),
                        String.format("(%012X)", collectorInfo.getCodeValue()));
            }
        }
    }

    /**
     * 集中器心跳。
     *
     * @param ctx 对应的网络连接。
     * @return 应答成功与否。
     */
    private static boolean collectorHeartbeat(ChannelHandlerContext ctx) {
        LocalDateTime now = LocalDateTime.now();
        CollectorInfo collectorInfo = getCollectorInfoOfCtx(ctx);
        // 记录心跳时间
        if (collectorInfo != null) {
            if (ctx != collectorInfo.getChannelHandlerContext()) {
                System.out.printf("[%s] collectorHeartbeat - Collector(%s) unexpected socket.\n",
                        now,
                        String.format("%012X", collectorInfo.getCodeValue()));
                return false;
            }
            updateCollectorActive(collectorInfo.getCollectID(), 1, now);
        }

        // 心跳消息回复
        return VirtualFsuController.collectorHeartbeat(ctx);
    }

    /**
     * 获取集中器的工作服务器信息。
     *
     * @param ctx 对应的网络连接。
     * @param qw  请求消息对象。
     * @return 发送成功与否。
     */
    private static boolean collectorWork(ChannelHandlerContext ctx,
                                         MessageBodyQueryWork qw) {
        try {
            long collectorCodeValue = qw.getClctCodeValue();
//			System.out.printf("[%s] %s request Collector(%s) work\n",
//					LocalDateTime.now().toString(),
//					ctx.channel().remoteAddress().toString(),
//					String.format("%012X", collectorCodeValue));
            // 获取集中器的服务信息
            Collector collector = VirtualFsuUtil.virtualFsuService.findCollectorByCode(String.format("%012X", collectorCodeValue));
            if (collector == null) {
                System.out.printf("[%s] collectorWork - Collector(%s) not found\n",
                        LocalDateTime.now().toString(),
                        String.format("%012X", qw.getClctCodeValue()));
                return false;
            }
            return VirtualFsuController.setCollectorWork(ctx, collector);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取集中器配置信息消息处理。
     *
     * @param ctx     对应的网络连接。
     * @param qc      请求消息对象。
     * @param encrypt 消息加密方式。
     * @return 发送成功与否。
     */
    private static boolean collectorConfig(ChannelHandlerContext ctx, MessageBodyQueryConfig qc, int encrypt) {
        try {
            LocalDateTime now = LocalDateTime.now();
            // 获取集中器
            long collectorCodeValue = qc.getClctCodeValue();
            Collector collector = VirtualFsuUtil.virtualFsuService.findCollectorByCode(String.format("%012X", collectorCodeValue));
            if (collector == null) {
                System.out.printf("[%s] CollectorConfig - Collector(%s) not found.\n",
                        now,
                        String.format("%012X", collectorCodeValue));
                return false;
            }
            // 集中器信息对象
            ChannelHandlerContext oldctx = null;    // 该集中器原来的网络连接。
            CollectorInfo collectorInfo = getCollectorInfoOfCtx(ctx);
            if (collectorInfo == null) {    // 未登录。
                // 获取或创建该集中器的集中器信息对象
                collectorInfo = VirtualFsuCollectorInfo.getOrCreateCollectorInfo(collectorCodeValue, encrypt, ctx, collector.getCollectorID());
                // 记录集中器信息对象
                setCollectorInfoToCtx(collectorInfo, ctx);
            } else {    // 已登录。
                if (collectorCodeValue != collectorInfo.getCodeValue()) {    // 和原来请求登录的集中器编码不一致！
                    System.out.printf("[%s] CollectorConfig - Unexpected collector code %s != %s.\n",
                            now,
                            String.format("%012X", collectorCodeValue),
                            String.format("%012X", collectorInfo.getCodeValue()));
                    try {
                        ctx.close();
                    } catch (Exception e) {
                    }
                    return false;
                } else {
                    collectorInfo.setEncrypt(encrypt);
                    oldctx = collectorInfo.setChannelHandlerContext(ctx);
                }
            }

            // 设置定时主动心跳，以判断集中器是否超时断线。
//			int hbFreq = collector.getHBFreq();
//			if(hbFreq < 90)
//				hbFreq = 90;
//			ctx.executor().scheduleAtFixedRate(new CollectorHeartbeatRunnable(ctx), hbFreq / 2, hbFreq, TimeUnit.SECONDS);

            if (oldctx == ctx) {    // 网络连接没有改变，说明是重复发送请求消息。
                // 不需要重复记录在线记录。
                // 不需要重复记录IP。
                // 不更新集中器活跃状态和时间。
            } else {    // 网络连接改变，说明是新的连接。
                if (oldctx == null) {    // 原来不在线。
                    // 更新集中器活跃状态和时间。
                    updateCollectorActive(collector.getCollectorID(), 1, now);
                    // 添加上线记录
                    collectorOnline(collector, true, now, 1);    // Login
                } else {    // 原来就已经在线。说明是原来的连接尚未中断，就又建立了新的连接。
                    // 老的连接已过时，不再需要
                    setCollectorInfoToCtx(null, oldctx);
                    try {
                        oldctx.close();
                    } catch (Exception e) {
                    }
                    // 不更新集中器活跃状态和时间。
                    // 添加重复连接记录
                    collectorOnline(collector, true, now, 2);    // Reconnect
                }
                // 更新集中器连接IP
                String ip = getRemoteIp(ctx);
                if (!ip.equals(collector.getIp())) {
                    VirtualFsuUtil.virtualFsuService.updateCollectorIp(collector, ip);
                }
            }

            // 是否对本次配置请求进行响应。
            if (oldctx == ctx) {    // 同一网络连接，重复请求配置。
                LocalDateTime lastConfigTime = collectorInfo.getConfigTime();
                if (lastConfigTime != null &&
                        Duration.between(lastConfigTime, now).getSeconds() < COLLECTOR_CONFIG_SECONDS) {    // 重复请求配置过于频繁。
                    return false;    // 忽略本次请求。
                }
            }

            // 发送集中器配置信息
            collectorInfo.setConfigTime(now);
            boolean bresult = VirtualFsuController.setCollectorConfig(collectorInfo, collector, ctx);
            if (bresult &&
                    collectorInfo.getBreakerCount() > 0) {
                System.out.printf("[%s] Collector(%s) %s.\n",
                        now,
                        String.format("%012X", collectorCodeValue),
                        oldctx == ctx ? "reconfig" : "connected");
            }
            return bresult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 断路器数据处理。
     *
     * @param ctx  对应的网络连接。
     * @param data 断路器数据。
     */
    private static void switchData(ChannelHandlerContext ctx,
                                   MessageBodyData data) {
        final LocalDateTime now = LocalDateTime.now();
        List<MessageBodyDataValue> values = data.getValues();
        if (values != null)
            values.stream().forEach(v -> {
                LocalDateTime dtValue;    // 信号时间
                dtValue = v.getDatetimeValue();
                switchDataValue(ctx, v.getBrkCodeString(), now, dtValue, MessageBodyDataValue.typeIds, v.values);
            });
    }

    /**
     * 断路器变化数据处理。
     *
     * @param ctx        对应的网络连接。
     * @param dataChange 断路器变化数据。
     */
    private static void switchData(ChannelHandlerContext ctx,
                                   MessageBodyDataChange dataChange) {
        final LocalDateTime now = LocalDateTime.now();
        List<MessageBodyDataChangeValue> values = dataChange.getValues();
        if (values != null)
            values.stream().forEach(v -> {
                LocalDateTime dtValue;    // 信号时间
                dtValue = v.getDatetimeValue();
                switchDataValue(ctx, v.getBrkCodeString(), now, dtValue, MessageBodyDataChangeValue.typeIds, v.values);
            });
    }

    /**
     * 断路器数据值的处理。
     *
     * @param ctx       连接。
     * @param code      断路器编码字符串。
     * @param dtMessage 数据消息的服务器接收时间。
     * @param dtValue   数据时间。
     * @param typeIds   各数据类型ID。
     * @param values    各数据类型对应的值。
     */
    private static void switchDataValue(ChannelHandlerContext ctx, String code,
                                        final LocalDateTime dtMessage, LocalDateTime dtValue,
                                        final Short[] typeIds, final double[] values) {
        LocalDateTime now = LocalDateTime.now();
        try {
            // 查找对应的断路器
            SwitchCache sc = VirtualFsuUtil.SWITCHCACHE.get(code);
            if (sc == null ||
                    sc.getSwitchID() <= 0) {
                Switch swt = VirtualFsuUtil.virtualFsuService.findSwitchBySwitchCode(code);
                if (swt == null) {
                    System.out.printf("[%s] Breaker(%s) not found. data droped.\n",
                            now,
                            code);
                    return;
                } else {
                    sc = SwitchCache.newSwitchCacheFromSwitch(swt);
                    VirtualFsuUtil.SWITCHCACHE.put(swt.getCode(), sc);
                }
            }
            if (Math.abs(Duration.between(dtValue, dtMessage).getSeconds()) >= COLLECTOR_TIMEERR_SECONDS) {
                // 集中器和服务器时间偏差太大。
                collectorRTCErr(sc.getCollectorID(), dtValue, dtMessage);
                // 更新集中器RTC时间。
                VirtualFsuController.setCollectorRTC(ctx);
                // 使用服务器消息接收时间作为数据时间。
                dtValue = dtMessage.withNano(0);
            }
            // 保存该断路器的各采集数据值
            Signalstype signalstype;
            Short signalsTypeID;
            int count = 0;
            short[] stIDs = new short[MessageBodyDataValue.typeIds.length];
            double[] vs = new double[MessageBodyDataValue.typeIds.length];
            for (int j = 0; j < values.length; j++) {
                signalstype = VirtualFsuUtil.SIGNALTYPES[typeIds[j]];
                signalsTypeID = signalstype.getSignalsTypeID();
                // Signal记录
//					Signal signal = new Signal();
//					signal.setSwitchID(swt.getSwitchID());
//					signal.setSignalsTypeID(signalstype.getSignalsTypeID());
//					signal.setValue(values[j]);
//					signal.setSignalsID(0L);
//					signal.setTime(Timestamp.valueOf(dtValue));
                if (VirtualFsuUtil.virtualFsuService.newDataAndCalc(dtValue, values[j], sc.getSwitchID(), signalsTypeID, sc.getzDataValue(signalsTypeID))) {
                    stIDs[count] = signalsTypeID;
                    vs[count] = values[j];
                    count++;
                }
            }
            if (count > 0) {
                // 批量更新信号数据记录
                VirtualFsuUtil.virtualFsuService.newDataSave(dtValue, sc.getSwitchID(), stIDs, vs, count);
                // 更新集中器活跃时间
                updateCollectorActive(sc.getCollectorID(), 1, dtMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 记录集中器RTC时间错误。
     *
     * @param collectorID 集中器。
     * @param rtcTime     集中器RTC时间。
     * @param srvTime     服务器时间。
     */
    private static void collectorRTCErr(Integer collectorID, final LocalDateTime rtcTime, final LocalDateTime srvTime) {
        try {
            CollectorRTC collectorRTC = VirtualFsuUtil.virtualFsuService.findRTCByCollectorID(collectorID);
            if (collectorRTC == null) {
                collectorRTC = new CollectorRTC();
                collectorRTC.setId(0L);
                Collector collector = new Collector();
                collector.setCollectorID(collectorID);
                collectorRTC.setCollector(collector);
            }
            collectorRTC.setRtcTime(Timestamp.valueOf(rtcTime));
            collectorRTC.setSrvTime(Timestamp.valueOf(srvTime));
            VirtualFsuUtil.virtualFsuService.addOrUpdateCollectorRTC(collectorRTC);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 断路器参数处理。
     *
     * @param ctx   对应的网络连接。
     * @param param 断路器参数。
     */
    private static void switchParam(ChannelHandlerContext ctx,
                                    MessageBodyParam param) {
        try {
            Switch swt = VirtualFsuUtil.virtualFsuService.findSwitchBySwitchCode(
                    String.format("%012X", param.getCodeValue())); // 对应的断路器
            if (swt == null)
                return;
            LocalDateTime now = LocalDateTime.now();
            //System.out.printf("[%s] %s\n", now.toString(), param.toString(VirtualFsuUtil.BYTEORDER));
            for (MessageBodyParamValue v : param.getValues()) {
                Object value = v.getValue(VirtualFsuUtil.BYTEORDER);
                if (value != null) {
                    SwitchParam switchParam = VirtualFsuUtil.virtualFsuService.findSwitchParamBySwitchIDParamID(swt.getSwitchID(), Integer.valueOf(String.valueOf(v.getId())));
                    if (switchParam == null) {
                        switchParam = new SwitchParam();
                        switchParam.setId(UUID.randomUUID().toString()
                                .replaceAll("-", ""));
                        switchParam.setSwitchs(swt);
                        switchParam.setParamID((int) v.getId());
                    }
                    switchParam.setParamValue(value.toString());
                    switchParam.setReturnTime(Timestamp.valueOf(now));
                    switchParam.setState(2);
                    VirtualFsuUtil.virtualFsuService.addOrUpdateSwitchParam(switchParam);
                }
            }
            // 更新集中器活跃时间
            updateCollectorActive(swt.getCollector().getCollectorID(), 1, now);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 断路器故障消息处理。
     *
     * @param ctx   对应的网络连接。
     * @param fault 断路器故障。
     */
    private static void switchFault(ChannelHandlerContext ctx,
                                    MessageBodyBreakerFault fault) {
        try {
            Switch swt = VirtualFsuUtil.virtualFsuService.findSwitchBySwitchCode(
                    String.format("%012X", fault.getCodeValue())); // 对应的断路器
            if (swt == null)
                return;
            LocalDateTime now = LocalDateTime.now();
            int faultCode = (int) fault.getFault();
            if (faultCode == swt.getFault()) {    // 状态没有变化
                return;
            }
//			System.out.printf("Fault: %s. lastFault: %d.\n",
//					fault.toString(),
//					swt.getFault());
            VirtualFsuUtil.virtualFsuService.switchFault(swt, faultCode);
            // 更新集中器活跃时间
            updateCollectorActive(swt.getCollector().getCollectorID(), 1, now);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 集中器的全部断路器故障处理。
     *
     * @param ctx   对应的网络连接。
     * @param fault 集中器故障。
     */
    private static void collectorFault(ChannelHandlerContext ctx,
                                       MessageBodyCollectorFault fault) {
        try {
            Collector collector = VirtualFsuUtil.virtualFsuService.findCollectorByCode(
                    String.format("%012X", fault.getCodeValue())); // 对应的集中器
            if (collector == null)
                return;
            LocalDateTime now = LocalDateTime.now();
            //System.out.printf("[%s] %s\n", now.toString(), fault.toString());
            // 保存每个断路器的故障
            for (MessageBodyBreakerFault bf : fault.getFaults()) {
                switchFault(ctx, bf);
            }
            // 保存集中器故障信息
            VirtualFsuUtil.virtualFsuService.updateCollectorFault(collector, 2, now);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 集中器通用应答消息处理。
     *
     * @param ctx 消息对应的网络连接。
     * @param ans 通用应答消息。
     */
    private static void commonAnswerCollector(ChannelHandlerContext ctx,
                                              MessageBodyCommonAns ans) {
        LocalDateTime now = LocalDateTime.now();
//		System.out.printf("[%s] CommonAnswerCollector %s\n",
//				now,
//				ans.toString());
        try {
            if (ans.getId() == 0x0406) {    // 上传集中器定时的结果消息
                CollectorTimerUpload collectorTimerUpload = VirtualFsuUtil.virtualFsuService.findTimerUploadByCollectorCodeAndMsg(
                        String.format("%012X", ans.getCodeValue()),
                        ans.getId(),
                        ans.getSerialno());
                if (collectorTimerUpload != null) {
                    collectorTimerUpload.setResult((int) ans.getResult());
                    collectorTimerUpload.setResultTime(Timestamp.valueOf(now));
                    VirtualFsuUtil.virtualFsuService.addOrUpdateCollectorTimerUpload(collectorTimerUpload);
                } else {
                    System.out.printf("[%s] CollectorTimerUpload(code=%012X, MsgId=0x%04X, MsgNo=%d) not found.\n",
                            now,
                            ans.getCodeValue(),
                            ans.getId(),
                            ans.getSerialno());
                }
            } else {
                System.out.printf("[%s] commonAnswerCollector(code=%012X, MsgId=0x%04X, MsgNo=%d) ignored.\n",
                        now,
                        ans.getCodeValue(),
                        ans.getId(),
                        ans.getSerialno());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 断路器通用应答消息处理。
     *
     * @param ctx 消息对应的网络连接。
     * @param ans 通用应答消息。
     */
    private static void commonAnswerBreaker(ChannelHandlerContext ctx,
                                            MessageBodyCommonAns ans) {
		LocalDateTime now = LocalDateTime.now();
        System.out.printf("[%s] commonAnswerBreaker(code=%012X, MsgId=0x%04X, MsgNo=%d) ignored.\n",
                now,
                ans.getCodeValue(),
                ans.getId(),
                ans.getSerialno());
    }

    /**
     * 更新集中器的最新活跃(在线/离线)状态。
     *
     * @param collectorID 集中器。
     * @param active      活跃状态(0=离线;1=在线;)。
     * @param activeTime  状态时间。
     */
    private static void updateCollectorActive(Integer collectorID, int active, LocalDateTime activeTime) {
        try {
            VirtualFsuUtil.virtualFsuService.updateCollectorActive(
                    collectorID,
                    active,
                    activeTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加集中器在线/离线记录。
     * 注意，本方法并不设置集中器的在线/离线状态。
     *
     * @param collectorID 集中器ID。
     * @param online      状态。
     * @param ontime      状态时间。
     * @param reason      原因。
     */
    public static void collectorOnline(Integer collectorID, boolean online, LocalDateTime ontime, int reason) {
        CollectorOnline collectorOnline = new CollectorOnline();
        collectorOnline.setId(0L);
        Collector collector = new Collector();
        collector.setCollectorID(collectorID);
        collectorOnline.setCollector(collector);
        collectorOnline.setOnstatus((byte) (online ? 1 : 0));
        collectorOnline.setOntime(Timestamp.valueOf(ontime));
        collectorOnline.setReason(reason);
        try {
            VirtualFsuUtil.virtualFsuService.addOrUpdateCollectorOnline(collectorOnline);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加集中器在线/离线记录。
     * 注意，本方法并不设置集中器的在线/离线状态。
     *
     * @param collector 集中器。
     * @param online    状态。
     * @param ontime    状态时间。
     * @param reason    原因。
     */
    public static void collectorOnline(Collector collector, boolean online, LocalDateTime ontime, int reason) {
        CollectorOnline collectorOnline = new CollectorOnline();
        collectorOnline.setId(0L);
        collectorOnline.setCollector(collector);
        collectorOnline.setOnstatus((byte) (online ? 1 : 0));
        collectorOnline.setOntime(Timestamp.valueOf(ontime));
        collectorOnline.setReason(reason);
        try {
            VirtualFsuUtil.virtualFsuService.addOrUpdateCollectorOnline(collectorOnline);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 把集中器信息对象记录到ChannelHandlerContext的属性中。
     * 以方便今后可以快速得知ChannelHandlerContext对应的集中器的信息。
     *
     * @param collectorInfo 集中器信息对象。
     * @param ctx           连接ChannelHandlerContext。
     */
    private static void setCollectorInfoToCtx(CollectorInfo collectorInfo, ChannelHandlerContext ctx) {
        Attribute<CollectorInfo> attr = ((AttributeMap) ctx).attr(keyCollectorInfo);
        attr.set(collectorInfo);
    }

    /**
     * 获取ChannelHandlerContext对应的集中器信息对象。
     * 该值是之前通过setCollectorInfoToCtx()记录在属性中的。
     *
     * @param ctx 连接ChannelHandlerContext。
     * @return 对应的集中器信息对象。
     */
    public static CollectorInfo getCollectorInfoOfCtx(ChannelHandlerContext ctx) {
        Attribute<CollectorInfo> attr = ((AttributeMap) ctx).attr(keyCollectorInfo);
        return attr.get();
    }

    /**
     * 删除ChannelHandlerContext对应的集中器信息对象。
     * 该值是之前通过setCollectorInfoToCtx()记录在属性中的。
     *
     * @param ctx 连接ChannelHandlerContext。
     * @return 原记录的集中器信息对象。
     */
    private static CollectorInfo removeCollectorInfoOfCtx(ChannelHandlerContext ctx) {
        Attribute<CollectorInfo> attr = ((AttributeMap) ctx).attr(keyCollectorInfo);
        return attr.getAndSet(null);
    }

    /**
     * 获取ChannelHandlerContext对应的远端地址。
     *
     * @param ctx 连接ChannelHandlerContext。
     * @return 对应的远端地址。
     */
    public static String getRemoteIp(ChannelHandlerContext ctx) {
        String ip = ctx.channel().remoteAddress().toString();
        if (ip.startsWith("/"))
            ip = ip.substring(1);
        int ipp = ip.indexOf(":");
        if (ipp >= 0)
            ip = ip.substring(0, ipp);
        return ip;
    }
}
