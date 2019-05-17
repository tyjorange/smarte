package com.rogy.smarte.fsu.message;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * 集中器的断路器定时设置消息体。
 */
public class MessageBodyCollectorTimer {
	private byte[] code = new byte[6];		// 集中器地址
	private List<MessageBodyBreakerTimer> times = new ArrayList<MessageBodyBreakerTimer>();

	/**
	 * 设置集中器地址。
	 * @param src 包含地址的字节数组。
	 * @param srcBegin 地址内容的起始字节索引。
	 * @return 集中器地址的字节长度。
	 */
	public int setCode(byte[] src, int srcBegin) {
		// 拷贝地址内容
		System.arraycopy(src, srcBegin, code, 0, code.length);
		return code.length;
	}
	/**
	 * 设置集中器器地址。
	 * @param codeValue 地址的数值。
	 * @return 集中器地址的字节长度。
	 */
	public int setBrkCode(long codeValue) {
		for(int i = code.length - 1; i >= 0; i--) {
			code[i] = (byte) (codeValue & 0xFF);
			codeValue = codeValue >> 8;
		}
		return code.length;
	}
	
	public void addBreakerTimer(MessageBodyBreakerTimer messageBodyBreakerTimer) {
		if(messageBodyBreakerTimer != null)
			times.add(messageBodyBreakerTimer);
	}

	/**
	 * 获取对象的字节内容的长度。
	 * @return 对象的字节内容的长度。
	 */
	public int byteLength() {
		return 6 + 1 + times.size() * MessageBodyBreakerTimer.byteLength();
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
		System.arraycopy(code, 0, dst, pos, code.length);
		pos += code.length;
		int size = times.size();
		dst[pos] = (byte)size;
		pos++;
		if(size > 0) {
			for(MessageBodyBreakerTimer messageBodyBreakerTimer : times) {
				pos += messageBodyBreakerTimer.getBytes(bo, dst, pos);
			}
		}
		
		return byteLength();
	}
}
