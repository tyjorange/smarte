package com.rogy.smarte.util;

import java.nio.ByteOrder;

/**
 * 无符号16位整形数值 
 */
public final class UInt16 {
	private byte[] bytes = new byte[2];		// 各字节值(高位->低位)
	
	/**
	 * 构造一无符号16位整形数值对象。
	 * @param iValue 对应的int值。如果小于0则构造的对象值为0；如果大于0则该int值的2个低字节数值构造成对象。
	 */
	public UInt16(int iValue){
		if(iValue < 0)
			bytes[0] = bytes[1] = 0;
		else {
			bytes[0] = (byte) ((iValue >> 8) & 0xFF);
			bytes[1] = (byte) (iValue & 0xFF);
		}
	};
	
	/**
	 * 构造一无符号16位整形数值对象。
	 * @param b0 数值的高位字节的值。
	 * @param b1 数值的低位字节的值。
	 */
	public UInt16(int b0, int b1) {
		bytes[0] = (byte) (b0 & 0xFF);
		bytes[1] = (byte) (b1 & 0xFF);
	}
	
	/**
	 * 获取和对象等值的int数值。
	 * @return 和对象等值的int数值。
	 */
	public int intValue() {
		int value = 0;
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
		byte[] bs = new byte[2];
		if(bo == ByteOrder.LITTLE_ENDIAN) {
			bs[0] = bytes[1];
			bs[1] = bytes[0];
		}
		else {
			bs[0] = bytes[0];
			bs[1] = bytes[1];
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
			dst[dstBegin + 0] = bytes[1];
			dst[dstBegin + 1] = bytes[0];
		}
		else {
			dst[dstBegin + 0] = bytes[0];
			dst[dstBegin + 1] = bytes[1];
		}
	}
	
	/**
	 * 从byte数组中读取一无符号16位整形数值。
	 * @param bo 数组的字节序。
	 * @param src 数组。
	 * @param srcBegin 读取的起始位置索引。
	 * @return 对应的无符号16位整形数值。
	 */
	public static int get(ByteOrder bo, byte[] src, int srcBegin) {
		int value = 0;
		if(bo == ByteOrder.LITTLE_ENDIAN) {
			for(int i = srcBegin + 1; i >= srcBegin; i--) {
				value = value << 8;
				value += (src[i] & 0xFF);
			}
		}
		else {
			for(int i = srcBegin; i < srcBegin + 2; i++) {
				value = value << 8;
				value += (src[i] & 0xFF);
			}
		}
		return value;
	}
	
	/**
	 * 输出一无符号16位整形数值到指定的字节数组中。
	 * @param value 对应的无符号16位整形数值
	 * @param bo 字节序。
	 * @param dst 输出字节数组。
	 * @param dstBegin 输出字节数组的起始位置。
	 */
	public static void put(int value, ByteOrder bo, byte[] dst, int dstBegin) {
		for(int i = 0; i < 2; i++) {
			if(bo == ByteOrder.LITTLE_ENDIAN) {
				dst[dstBegin + i] = (byte) ((value >> (8 * i)) & 0xFF);
			}
			else {
				dst[dstBegin + 1 - i] = (byte) ((value >> (8 * i)) & 0xFF);
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.intValue());
		return sb.toString();
	}
	
	public static void main(String args[]) {
		byte bytes[] = new byte[4];
		int value = 65518;
		int vget;
		UInt16.put(value, ByteOrder.BIG_ENDIAN, bytes, 2);
		vget = 0;
		vget = UInt16.get(ByteOrder.BIG_ENDIAN, bytes, 2);
		System.out.println(vget == value);
		UInt16.put(value, ByteOrder.LITTLE_ENDIAN, bytes, 2);
		vget = 0;
		vget = UInt16.get(ByteOrder.LITTLE_ENDIAN, bytes, 2);
		System.out.println(vget == value);
	}
}
