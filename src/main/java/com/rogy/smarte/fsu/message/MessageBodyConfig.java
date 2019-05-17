package com.rogy.smarte.fsu.message;


import com.rogy.smarte.fsu.VirtualFsuUtil;
import com.rogy.smarte.util.UInt16;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * 集中器配置信息。
 */
public class MessageBodyConfig {
	private byte[] clctCode = new byte[6];		// 集中器地址
	private long clctCodeValue = 0L;	// 地址数值
	private int baud = 9600;	// 波特率
	private int freq = 0;	// 上报频率
	private byte range = 10;	// 变化幅度
	private int hbFreq = 45;	// 心跳频率
	private List<Long> breakers = new ArrayList<Long>();	// 集中器的断路器
	private byte[] key = new byte[16];
	
	/**
	 * 设置地址。
	 * @param src 包含地址的字节数组。
	 * @param srcBegin 地址内容的起始字节索引。
	 * @return 地址的字节长度。
	 */
	public int setClctCode(byte[] src, int srcBegin) {
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
	public int setClctCode(long cv) {
		for(int i = clctCode.length - 1; i >= 0; i--) {
			clctCode[i] = (byte) (cv & 0xFF);
			cv = cv >> 8;
		}
		// 计算地址数值
		clctCodeValue = VirtualFsuUtil.calcCodeValue(clctCode, 0, clctCode.length);
		
		return clctCode.length;
	}
	
	/**
	 * 添加一个断路器。
	 * @param breakerCodeValue 断路器地址数值。
	 */
	public void addBreaker(long breakerCodeValue) {
		breakers.add(breakerCodeValue);
	}
	
	/**
	 * 获取断路器个数。
	 * @return 断路器个数。
	 */
	public int getBreakerCount() {
		return breakers.size();
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
		System.arraycopy(clctCode, 0, dst, pos, clctCode.length);
		pos += clctCode.length;
		UInt16.put(baud, bo, dst, pos);
		pos += 2;
		UInt16.put(freq, bo, dst, pos);
		pos += 2;
		dst[pos] = range;
		pos++;
		UInt16.put(hbFreq, bo, dst, pos);
		pos += 2;
		int size = breakers.size();
		dst[pos] = (byte) size;
		pos++;
		if(size > 0) {
			long cv;
			for(Long breakerCodeValue : breakers) {
				cv = breakerCodeValue.longValue();
				for(int i = 6 - 1; i >= 0; i--) {
					dst[pos + i] = (byte) (cv & 0xFF);
					cv = cv >> 8;
				}
				pos += 6;
			}
		}
		System.arraycopy(key, 0, dst, pos, key.length);
		pos += key.length;
		return byteLength();
	}
	
	/**
	 * 获取对象的字节内容的长度。
	 * @return 对象的字节内容的长度。
	 */
	public int byteLength() {
		return 6 + 2 + 2 + 1 + 2 + 1 + breakers.size() * 6 + key.length;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{code=");
		sb.append(String.format("%012X", clctCodeValue));
		sb.append(" baud=");
		sb.append(baud);
		sb.append(" freq=");
		sb.append(freq);
		sb.append(" range=");
		sb.append(range);
		sb.append(" hb=");
		sb.append(hbFreq);
		sb.append(" breaker=[");
		boolean isFirst = true;
		for(Long cv : breakers) {
			if(!isFirst)
				sb.append(",");
			else
				isFirst = false;
			sb.append(String.format("%012X", cv));
		}
		sb.append("]");
		sb.append("}");
		return sb.toString();
	}
	
	public final byte[] getClctCode() {
		return clctCode;
	}
	public long getClctCodeValue() {
		return clctCodeValue;
	}
	public int getFreq() {
		return freq;
	}
	public void setFreq(int freq) {
		this.freq = freq;
	}
	public byte getRange() {
		return range;
	}
	public void setRange(byte range) {
		this.range = range;
	}
	public int getBaud() {
		return baud;
	}
	public void setBaud(int baud) {
		this.baud = baud;
	}
	/**
	 * @return the hbFreq
	 */
	public int getHbFreq() {
		return hbFreq;
	}
	/**
	 * @param hbFreq the hbFreq to set
	 */
	public void setHbFreq(int hbFreq) {
		this.hbFreq = hbFreq;
	}
	
	public void setKey(final byte[] key) {
		System.arraycopy(key, 0, this.key, 0, this.key.length);
	}
	
	public final byte[] getKey() {
		return key;
	}
}
