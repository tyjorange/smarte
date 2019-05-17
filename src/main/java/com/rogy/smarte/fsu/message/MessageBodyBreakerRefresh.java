package com.rogy.smarte.fsu.message;

/**
 * 刷新获取断路器最新数据消息体。
 */
public class MessageBodyBreakerRefresh {
	private byte[] brkCode = new byte[6];		// 断路器地址
	
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
	
	/**
	 * 得到消息体对象的字节数组。
	 * @return 字节数组。
	 */
	public final byte[] getBytes() {
		byte[] bytes = new byte[brkCode.length];
		System.arraycopy(brkCode, 0, bytes, 0, brkCode.length);
		return bytes;
	}
}
