package com.rogy.smarte.fsu;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 各集中器的CollectorInfo的管理类。
 *
 * 集中器上线后，其对应的CollectorInfo对象将被创建。
 * 该对象将一直存在，并且对此集中器不会被重新创建，
 * 以保证其属性和锁的唯一性一致性。
 */
public class VirtualFsuCollectorInfo {
	private VirtualFsuCollectorInfo() {
	}

	private static final int COLLECTOR_HASHTABLE_SIZE = 128;
	/**
	 * 各集中器的CollecorInfo对象。
	 * KEY为集中器编码的十六进制解析值，VALUE为CollectorInfo对象。
	 */
	public static final ConcurrentHashMap<Long, CollectorInfo> collectorInfos =
			new ConcurrentHashMap<Long, CollectorInfo>(COLLECTOR_HASHTABLE_SIZE);

	/**
	 * 获取集中器的CollectorInfo对象。
	 * @param code 集中器编码。
	 * @return 该集中器的CollectorInfo对象。null表示不存在。
	 */
	public static CollectorInfo getCollectorInfo(String code) {
		try {
			Long codeValue = Long.parseLong(code, 16);
			return getCollectorInfo(codeValue);
		} catch(Exception e) {
			return null;
		}
	}
	/**
	 * 获取集中器的CollectorInfo对象。
	 * @param codeValue 集中器编码值。
	 * @return 该集中器的CollectorInfo对象。null表示不存在。
	 */
	public static CollectorInfo getCollectorInfo(Long codeValue) {
		return collectorInfos.get(codeValue);
	}
	/**
	 * 获取集中器的CollectorInfo对象。
	 * 如果不存在则创建。
	 * @param code 集中器编码。
	 * @param encrypt 加密方式。
	 * @param collectorID 集中器记录ID。
	 * @return 该集中器的CollectorInfo对象。
	 */
	public static CollectorInfo getOrCreateCollectorInfo(String code, int encrypt, ChannelHandlerContext ctx, Integer collectorID) {
		try {
			Long codeValue = Long.parseLong(code, 16);
			return getOrCreateCollectorInfo(codeValue, encrypt, ctx, collectorID);
		} catch(Exception e) {
			return null;
		}
	}
	/**
	 * 获取集中器的CollectorInfo对象。
	 * 如果不存在则创建。
	 * @param codeValue 集中器编码值。
	 * @param encrypt 加密方式。
	 * @return 该集中器的CollectorInfo对象。
	 */
	public static CollectorInfo getOrCreateCollectorInfo(Long codeValue, int encrypt, ChannelHandlerContext ctx, Integer collectorID) {
		try {
			CollectorInfo collectorInfo = collectorInfos.get(codeValue);
			if(collectorInfo == null) {	// 该集中器的CollectorInfo不存在。
				collectorInfo = new CollectorInfo(codeValue, encrypt, collectorID);
				collectorInfo.setChannelHandlerContext(ctx);
				CollectorInfo old = collectorInfos.putIfAbsent(codeValue, collectorInfo);	// 设置新值时，需再次检测是否有值了。
				if(old == null)	// 无老对象，使用新对象。
					return collectorInfo;
				else {	// 有老对象则使用老对象。
					old.setEncrypt(encrypt);
					old.setChannelHandlerContext(ctx);
					return old;
				}
			}
			else {	// 使用老对象。
				collectorInfo.setEncrypt(encrypt);
				collectorInfo.setChannelHandlerContext(ctx);
				return collectorInfo;
			}
		} catch(Exception e) {
			return null;
		}
	}

	/**
	 * 获取总数。
	 * @return 总数。
	 */
	public static int getSize() {
		return collectorInfos.size();
	}
	/**
	 * 获取在线(ctx<>null)的总数。
	 * @return 在线总数。
	 */
	public static int getOnlineSize() {
		return (int)collectorInfos.entrySet().stream().filter(e -> e.getValue().getChannelHandlerContext() != null).count();
	}
}
