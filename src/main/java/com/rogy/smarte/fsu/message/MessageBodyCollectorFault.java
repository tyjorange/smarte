package com.rogy.smarte.fsu.message;


import com.rogy.smarte.fsu.VirtualFsuUtil;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * 查询集中器下全部断路器故障应答消息体对象 
 */
public class MessageBodyCollectorFault {
	private byte[] code = new byte[6];		// 集中器地址
	private long codeValue;	// 集中器地址数值
	private List<MessageBodyBreakerFault> faults = new ArrayList<MessageBodyBreakerFault>();	// 各断路器故障
	
	private MessageBodyCollectorFault(){};
	/**
	 * 从字节数组解析MessageBodyCollectorFault对象。
	 * @param bo 字节序。
	 * @param src 包含对象内容的源字节数组。
	 * @return 解析得到的MessageBodyCollectorFault对象。null表示失败。
	 */
	public static MessageBodyCollectorFault create(ByteOrder bo, byte[] src) {
		return create(bo, src, 0, src.length);
	}
	/**
	 * 从字节数组解析MessageBodyCollectorFault对象。
	 * @param bo 字节序。
	 * @param src 包含对象内容的源字节数组。
	 * @param begin 对象内容起始字节索引。
	 * @param len 对象内容长度。
	 * @return 解析得到的MessageBodyCollectorFault对象。null表示失败。
	 */
	public static MessageBodyCollectorFault create(ByteOrder bo, byte[] src, int begin, int len) {
		MessageBodyCollectorFault flt = new MessageBodyCollectorFault();
		int pos = begin;
		pos += flt.setCode(src, pos);
		int count = src[pos] & 0xFF;
		pos++;
		for(int i = 0; i < count; i++) {
			MessageBodyBreakerFault breakerFault = MessageBodyBreakerFault.create(bo, src, pos, 10);
			pos += 10;
			flt.faults.add(breakerFault);
		}
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
	
	public List<MessageBodyBreakerFault> getFaults() {
		return faults;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{code=0x");
		sb.append(String.format("%012X", codeValue));
		sb.append(" faults=");
		sb.append(faults.toString());
		sb.append("}");
		return sb.toString();
	}
}
