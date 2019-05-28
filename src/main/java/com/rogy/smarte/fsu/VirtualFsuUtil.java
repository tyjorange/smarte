package com.rogy.smarte.fsu;

import com.rogy.smarte.entity.db1.Signalstype;
import com.rogy.smarte.service.impl.VirtualFsuServiceImpl;

import java.nio.ByteOrder;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 工具类。
 */
public class VirtualFsuUtil {
	private VirtualFsuUtil() {
	}

	public static final boolean DEBUG = false;		// 调试

	/** virtualFsuService资源对象 */
	public static VirtualFsuServiceImpl virtualFsuService = null;

	/**
	 * 调度任务执行线程池。
	 */
//	public static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
//	public static void shutdownScheduledExcutorService() {
//		scheduledExecutorService.shutdown();
//	}
	/**
	 * 任务执行线程池。
	 */
//	public static ExecutorService executorService = Executors.newCachedThreadPool();
//	public static void shutdownExcutorService() {
//		executorService.shutdown();
//	}

	/**
	 * SignalsType记录。
	 * KEY为记录ID即SignalTypeId，VALUE为对应的SignalsType对象。
	 * 因为SignalsType为静态数据，保存在内存中以免查询数据库。
	 */
	//public static final ConcurrentHashMap<Short, Signalstype> SIGNALTYPES = new ConcurrentHashMap<>();
	/**
	 * SignalsType记录。
	 * 索引为记录ID即SignalTypeId。
	 * 因为SignalsType为静态数据，保存在内存中以免查询数据库。
	 */
	public static final Signalstype[] SIGNALTYPES = new Signalstype[16];
	/**
	 * Switch的Cache信息。
	 * KEY为SwitchCode，VALUE为对应的SwitchStaticInfo对象。
	 * Switch相关静态数据保存在内存中以免查询数据库。
	 */
	public static final ConcurrentHashMap<String, SwitchCache> SWITCHCACHE = new ConcurrentHashMap<>(128);

	/** 消息字节序 */
	public static final ByteOrder BYTEORDER = ByteOrder.LITTLE_ENDIAN;
	/** serialNo消息流水号生成器 */
	public static AtomicInteger serialNo = new AtomicInteger(0);
	/** 配置信息消息的密钥 **/
	public static final byte[] CONFIG_KEY = {0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01};

	/**
	 * 根据地址编码字节数组计算对应的整形数值。
	 * @param src 包含地址编码的字节数组。
	 * @param srcBegin 地址编码起始字节索引。
	 * @param len 地址编码的字节长度。
	 * @return 对应的整形数值。
	 */
	public static long calcCodeValue(byte[] src, int srcBegin, int len) {
		// 计算地址数值
		long codeValue = 0L;
		for(int i = 0; i < len; i++) {
			codeValue = codeValue << 8;
			codeValue += (src[srcBegin + i] & 0xff);
		}
		return codeValue;
	}

	/**
	 * 根据总累计值计算每小时累计值。
	 * @param dtLast 上次累计时间
	 * @param totalLast 上次总累计值
	 * @param dtCurrent 本次累计时间
	 * @param totalCurrent 本次总累计值
	 * @return 上次累计时间到本次累计时间之间的各小时的累计值。
	 */
	public static Map<LocalDateTime, Double> calcGroupByHour(LocalDateTime dtLast, double totalLast, LocalDateTime dtCurrent, double totalCurrent) {
		Map<LocalDateTime, Double> totalHours = new LinkedHashMap<LocalDateTime, Double>();
		if(!dtCurrent.isAfter(dtLast) ||
				totalLast >= totalCurrent)
			return totalHours;

		double totalBetween = totalCurrent - totalLast;	// 本时间段的统计差值
		long dtSeconds;		// 上次累计时间到本次累计时间的总秒数
		dtSeconds = dtLast.until(dtCurrent, ChronoUnit.SECONDS);
		if(dtSeconds <= 0)
			return totalHours;

		LocalDateTime dtHour;	// 当前小时的统计起始时间
		dtHour = dtLast;
		LocalDateTime dtNextHour;	// 下一小时的起始时间。其分、秒等字段为0
		dtNextHour = dtHour.plusHours(1);
		dtNextHour = LocalDateTime.of(dtNextHour.getYear(), dtNextHour.getMonthValue(), dtNextHour.getDayOfMonth(), dtNextHour.getHour(), 0, 0, 0);
		// 对区间中的每个小时进行循环，根据其秒数在区间总秒数的占比，计算其统计数值
		long dtSecondsOfHour;
		while(true) {
			if(dtNextHour.isAfter(dtCurrent))
				dtNextHour = dtCurrent;
			dtSecondsOfHour = dtHour.until(dtNextHour, ChronoUnit.SECONDS);
			if(dtSecondsOfHour > 0)
				totalHours.put(dtHour, totalBetween / dtSeconds * dtSecondsOfHour);
			else
				break;

			if(!dtNextHour.isBefore(dtCurrent))	// 所有小时都已处理完毕
				break;
			else {	// 下一小时
				dtHour = dtNextHour;
				dtNextHour = dtNextHour.plusHours(1);
			}
		}

		return totalHours;
	}

	public static void main(String args[]) {
		LocalDateTime dtLast = LocalDateTime.of(2018, 4, 10, 2, 12, 12);
		double totalLast = 32;
		LocalDateTime dtCurrent = LocalDateTime.of(2018, 4, 11, 4, 0, 2);
		double totalCurrent = 62;
		double total = 0;
		Map<LocalDateTime, Double> totalHours = VirtualFsuUtil.calcGroupByHour(dtLast, totalLast, dtCurrent, totalCurrent);
		for(Map.Entry<LocalDateTime, Double> entry : totalHours.entrySet()) {
			System.out.printf("%s %02d : %f\n", entry.getKey().toLocalDate().toString(), entry.getKey().getHour(), entry.getValue());
			total += entry.getValue();
		}
		System.out.printf("Total : %f\n", total);
	}
}
