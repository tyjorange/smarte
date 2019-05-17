package com.rogy.smarte.fsu.message;

import java.nio.ByteOrder;

/**
 * 断路器定时设置消息体。
 */
public class MessageBodyBreakerTimer {
	private byte[] brkCode = new byte[6];		// 断路器地址
	private byte data;			// 定时日和操作
	private byte hour;			// 定时-时
	private byte minute;		// 定时-分
	
	/**
	 * 设置断路器地址。
	 * @param src 包含地址的字节数组。
	 * @param srcBegin 地址内容的起始字节索引。
	 * @return 断路器地址的字节长度。
	 */
	public int setBrkCode(byte[] src, int srcBegin) {
		// 拷贝地址内容
		System.arraycopy(src, srcBegin, brkCode, 0, brkCode.length);
		return brkCode.length;
	}
	/**
	 * 设置断路器地址。
	 * @param codeValue 地址的数值。
	 * @return 断路器地址的字节长度。
	 */
	public int setBrkCode(long codeValue) {
		for(int i = brkCode.length - 1; i >= 0; i--) {
			brkCode[i] = (byte) (codeValue & 0xFF);
			codeValue = codeValue >> 8;
		}
		return brkCode.length;
	}
	
	public void setHour(byte hour) {
		if(hour >= 0 && hour <= 23)
			this.hour = hour;
		else
			throw new java.lang.IllegalArgumentException("hour out of range, must be 0 - 23.");
	}

	public void setMinute(byte minute) {
		if(minute >= 0 && minute <= 59)
			this.minute = minute;
		else
			throw new java.lang.IllegalArgumentException("minute out of range, must be 0 - 59.");
	}

	/**
	 * 设置合闸拉闸。
	 * @param isOn true=合闸；false=拉闸。
	 */
	public void setOn(boolean isOn) {
		if(isOn)
			data = (byte) (data | 0x01);
		else
			data = (byte) (data & 0xfe);
	}

	/**
	 * 增加有效日。
	 * @param wday 周的某日(1-7 = 周一-周日)。
	 */
	public void addActiveWeekDay(int wday) {
		if(wday >= 1 && wday <= 7)
			data = (byte) ((data & 0xff) | (0x01 << wday));
		else
			throw new java.lang.IllegalArgumentException("weekday out of range, must be 1 - 7.");
	}
	
	/**
	 * 设置有效日。
	 * @param wdays 一周中哪些日有效(第0位为1表示周1有效，否则周1无效；...；第6位为1表示周日有效，否则周日无效)
	 */
	public void setActiveWeekDays(int wdays) {
		data = (byte) (((wdays << 1) & 0xfe) | (data & 0x01));
	}

	/**
	 * 获取对象的字节内容的长度。
	 * @return 对象的字节内容的长度。
	 */
	public static int byteLength() {
		return 6 + 3;
	}
	
	/**
	 * 获取对象内容的字节数组。
	 * @param bo 字节序。
	 * @return 消息对象的字节数组。null表示失败。
	 */
	public byte[] getBytes(ByteOrder bo) {
		byte[] bytes = new byte[byteLength()];
		getBytes(bo, bytes, 0);
		return bytes;
	}
	/**
	 * 输出对象的字节内容。
	 * @param bo 字节序。
	 * @param dst 输出字节数组。
	 * @param dstBegin 输出字节数组的起始字节索引。
	 * @return 输出的字节长度。
	 */
	public int getBytes(ByteOrder bo, byte[] dst, int dstBegin) {
		int pos = dstBegin;
		System.arraycopy(brkCode, 0, dst, pos, brkCode.length);
		pos += brkCode.length;
		dst[pos] = data;
		pos++;
		dst[pos] = hour;
		pos++;
		dst[pos] = minute;
		pos++;
		
		return byteLength();
	}
}
