package com.rogy.smarte.fsu.message;


import com.rogy.smarte.fsu.VirtualFsuUtil;

import java.nio.ByteOrder;

/**
 * 查询集中器配置信息消息。
 */
public class MessageBodyQueryConfig {
	private byte[] clctCode = new byte[6];		// 集中器地址
	private long clctCodeValue = 0L;	// 地址数值
	
	private MessageBodyQueryConfig() {};
	/**
	 * 从字节数组解析MessageModbusBodyQueryConfig对象。
	 * @param bo 字节序。
	 * @param src 包含对象内容的源字节数组。
	 * @return 解析得到的MessageModbusBodyQueryConfig对象。null表示失败。
	 */
	public static MessageBodyQueryConfig create(ByteOrder bo, byte[] src) {
		return create(bo, src, 0, src.length);
	}
	/**
	 * 从字节数组解析MessageModbusBodyQueryConfig对象。
	 * @param bo 字节序。
	 * @param src 包含对象内容的源字节数组。
	 * @param begin 对象内容起始字节索引。
	 * @param len 对象内容长度。
	 * @return 解析得到的MessageModbusBodyQueryConfig对象。null表示失败。
	 */
	public static MessageBodyQueryConfig create(ByteOrder bo, byte[] src, int begin, int len) {
		MessageBodyQueryConfig cfg = new MessageBodyQueryConfig();
		int pos = begin;
		pos += cfg.setCode(src, pos);
		return cfg;
	}

	/**
	 * 设置地址。
	 * @param src 包含地址的字节数组。
	 * @param srcBegin 地址内容的起始字节索引。
	 * @return 地址的字节长度。
	 */
	public int setCode(byte[] src, int srcBegin) {
		// 拷贝地址内容
		System.arraycopy(src, srcBegin, clctCode, 0, clctCode.length);
		// 计算地址数值
		clctCodeValue = VirtualFsuUtil.calcCodeValue(clctCode, 0, clctCode.length);
		
		return clctCode.length;
	}
	/**
	 * 设置地址。
	 * @param cv 地址的数值。
	 * @return 地址的字节长度。
	 */
	public int setCode(long cv) {
		for(int i = clctCode.length - 1; i >= 0; i--) {
			clctCode[i] = (byte) (cv & 0xFF);
			cv = cv >> 8;
		}
		// 计算地址数值
		clctCodeValue = VirtualFsuUtil.calcCodeValue(clctCode, 0, clctCode.length);
		
		return clctCode.length;
	}
	
	public final byte[] getClctCode() {
		return clctCode;
	}
	public long getClctCodeValue() {
		return clctCodeValue;
	}
}
