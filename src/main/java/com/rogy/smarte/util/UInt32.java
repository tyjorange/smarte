package com.rogy.smarte.util;

import java.nio.ByteOrder;

/**
 * 无符号32位整形数值 
 */
public final class UInt32 {
	private byte[] bytes = new byte[4];		// 各字节值(高位->低位)
	
	/**
	 * 构造一无符号32位整形数值对象。
	 * @param lValue 对应的long值。如果小于0则构造的对象值为0；如果大于0则该long值的4个低字节数值构造成对象。
	 */
	public UInt32(long lValue){
		if(lValue < 0)
			bytes[0] = bytes[1] = bytes[2] = bytes[3] = 0;
		else {
			bytes[0] = (byte) ((lValue >> 24) & 0xFF);
			bytes[1] = (byte) ((lValue >> 16) & 0xFF);
			bytes[2] = (byte) ((lValue >> 8) & 0xFF);
			bytes[3] = (byte) (lValue & 0xFF);
		}
	};
	
	/**
	 * 构造一无符号32位整形数值对象。
	 * @param b0 数值的最高位字节的值。
	 * @param b1 数值的次高位字节的值。
	 * @param b2 数值的次低位字节的值。
	 * @param b3 数值的最低位字节的值。
	 */
	public UInt32(int b0, int b1, int b2, int b3) {
		bytes[0] = (byte) (b0 & 0xFF);
		bytes[1] = (byte) (b1 & 0xFF);
		bytes[2] = (byte) (b2 & 0xFF);
		bytes[3] = (byte) (b3 & 0xFF);
	}
	
	/**
	 * 获取和对象等值的long数值。
	 * @return 和对象等值的long数值。
	 */
	public long longValue() {
		long value = 0;
		for(int i = 0; i < bytes.length; i++) {
			value = value << 8;
			value += (bytes[i] & 0xff);
		}
		return value;
	}
	
	/**
	 * 获取对象的字节串。
	 * @param bo 字节序。
	 * @return 对象的字节串。
	 */
	public byte[] getBytes(ByteOrder bo) {
		byte[] bs = new byte[4];
		if(bo == ByteOrder.LITTLE_ENDIAN) {
			bs[0] = bytes[3];
			bs[1] = bytes[2];
			bs[2] = bytes[1];
			bs[3] = bytes[0];
		}
		else {
			bs[0] = bytes[0];
			bs[1] = bytes[1];
			bs[2] = bytes[2];
			bs[3] = bytes[3];
		}
		return bs;
	}
	
	/**
	 * 获取对象的字节串到指定的字节数组中。
	 * @param bo 字节序。
	 * @param dst 输出字节数组。
	 * @param dstBegin 输出字节数组的起始位置。
	 */
	public void getBytes(ByteOrder bo, byte[] dst, int dstBegin) {
		if(bo == ByteOrder.LITTLE_ENDIAN) {
			dst[dstBegin + 0] = bytes[3];
			dst[dstBegin + 1] = bytes[2];
			dst[dstBegin + 2] = bytes[1];
			dst[dstBegin + 3] = bytes[0];
		}
		else {
			dst[dstBegin + 0] = bytes[0];
			dst[dstBegin + 1] = bytes[1];
			dst[dstBegin + 2] = bytes[2];
			dst[dstBegin + 3] = bytes[3];
		}
	}
	
	/**
	 * 从byte数组中读取一无符号32位整形数值。
	 * @param bo 数组的字节序。
	 * @param src 数组。
	 * @param srcBegin 读取的起始位置索引。
	 * @return 对应的无符号32位整形数值。
	 */
	public static long get(ByteOrder bo, byte[] src, int srcBegin) {
		long value = 0;
		if(bo == ByteOrder.LITTLE_ENDIAN) {
			for(int i = srcBegin + 3; i >= srcBegin; i--) {
				value = value << 8;
				value += (src[i] & 0xFF);
			}
		}
		else {
			for(int i = srcBegin; i < srcBegin + 4; i++) {
				value = value << 8;
				value += (src[i] & 0xFF);
			}
		}
		return value;
	}
	
	/**
	 * 输出一无符号32位整形数值到指定的字节数组中。
	 * @param value 对应的无符号32位整形数值
	 * @param bo 字节序。
	 * @param dst 输出字节数组。
	 * @param dstBegin 输出字节数组的起始位置。
	 */
	public static void put(long value, ByteOrder bo, byte[] dst, int dstBegin) {
		for(int i = 0; i < 4; i++) {
			if(bo == ByteOrder.LITTLE_ENDIAN) {
				dst[dstBegin + i] = (byte) ((value >> (8 * i)) & 0xFF);
			}
			else {
				dst[dstBegin + 3 - i] = (byte) ((value >> (8 * i)) & 0xFF);
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.longValue());
		return sb.toString();
	}
	
	public static void main(String args[]) {
		byte bytes[] = new byte[8];
		long value = 0xfedcba98L;
		long vget;
		UInt32.put(value, ByteOrder.BIG_ENDIAN, bytes, 2);
		vget = 0;
		vget = UInt32.get(ByteOrder.BIG_ENDIAN, bytes, 2);
		System.out.println(vget == value);
		UInt32.put(value, ByteOrder.LITTLE_ENDIAN, bytes, 2);
		vget = 0;
		vget = UInt32.get(ByteOrder.LITTLE_ENDIAN, bytes, 2);
		System.out.println(vget == value);
	}
}