package com.rogy.smarte.fsu.message;


import com.rogy.smarte.fsu.VirtualFsuUtil;
import com.rogy.smarte.util.UInt32;

import java.nio.ByteOrder;

/**
 * 查询断路器故障应答消息体对象 
 */
public class MessageBodyBreakerFault {
	private byte[] code = new byte[6];		// 地址
	private long codeValue;	// 地址数值
	private long fault = 0;		// 故障码
	
	private MessageBodyBreakerFault(){};
	/**
	 * 从字节数组解析MessageBodyBreakerFault对象。
	 * @param bo 字节序。
	 * @param src 包含对象内容的源字节数组。
	 * @return 解析得到的MessageBodyBreakerFault对象。null表示失败。
	 */
	public static MessageBodyBreakerFault create(ByteOrder bo, byte[] src) {
		return create(bo, src, 0, src.length);
	}
	/**
	 * 从字节数组解析MessageBodyBreakerFault对象。
	 * @param bo 字节序。
	 * @param src 包含对象内容的源字节数组。
	 * @param begin 对象内容起始字节索引。
	 * @param len 对象内容长度。
	 * @return 解析得到的MessageBodyBreakerFault对象。null表示失败。
	 */
	public static MessageBodyBreakerFault create(ByteOrder bo, byte[] src, int begin, int len) {
		MessageBodyBreakerFault flt = new MessageBodyBreakerFault();
		int pos = begin;
		pos += flt.setCode(src, pos);
		flt.fault = UInt32.get(bo, src, pos);
		pos += 4;
		return flt;
	}


	/**
	 * 设置地址。
	 * @param src 包含地址的字节数组。
	 * @param srcBegin 地址内容的起始字节索引。
	 * @return 地址的字节长度。
	 */
	public int setCode(byte[] src, int srcBegin) {
		// 拷贝地址内容
		System.arraycopy(src, srcBegin, code, 0, code.length);
		// 计算地址数值
		codeValue = VirtualFsuUtil.calcCodeValue(code, 0, code.length);
		
		return code.length;
	}
	
	public final byte[] getCode() {
		return code;
	}
	public long getCodeValue() {
		return codeValue;
	}
	
	public long getFault() {
		return fault;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{code=");
		sb.append(String.format("%012X", codeValue));
		sb.append(" fault=");
		sb.append(fault);
		sb.append("}");
		return sb.toString();
	}
}
