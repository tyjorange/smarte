package com.rogy.smarte.fsu.message;


import com.rogy.smarte.fsu.VirtualFsuUtil;
import com.rogy.smarte.util.UInt16;
import com.rogy.smarte.util.UInt32;

import java.nio.ByteOrder;

public class MessageBodyWork {
	private byte[] clctCode = new byte[6];		// 集中器地址
	private long clctCodeValue = 0L;	// 地址数值
	private long ip = 0;
	private int port = 55555;

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
		UInt32.put(ip, bo, dst, pos);
		pos += 4;
		UInt16.put(port, bo, dst, pos);
		pos += 2;
		
		return byteLength();
	}
	
	/**
	 * 获取对象的字节内容的长度。
	 * @return 对象的字节内容的长度。
	 */
	public int byteLength() {
		return 6 + 4 + 2;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{code=");
		sb.append(String.format("%012X", clctCodeValue));
		sb.append(" ip=");
		sb.append(String.format("%d", (ip >> 24) & 0xff));
		sb.append(String.format(".%d", (ip >> 16) & 0xff));
		sb.append(String.format(".%d", (ip >> 8) & 0xff));
		sb.append(String.format(".%d", ip & 0xff));
		sb.append(" port=");
		sb.append(port);
		sb.append("}");
		return sb.toString();
	}
	
	public final byte[] getClctCode() {
		return clctCode;
	}
	public long getClctCodeValue() {
		return clctCodeValue;
	}
	public long getIp() {
		return ip;
	}
	public void setIp(String ip) {
		try {
			String[] items = ip.split("\\.");
			this.ip = 0;
			for(String item : items) {
				this.ip = (this.ip << 8) + Integer.parseInt(item);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void setIp(long ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
}
