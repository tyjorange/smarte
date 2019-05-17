package com.rogy.smarte.fsu.message;


import com.rogy.smarte.fsu.VirtualFsuUtil;
import com.rogy.smarte.util.UInt32;

import java.nio.ByteOrder;
import java.util.HashSet;
import java.util.Set;

public class MessageBodyQueryParam {
	private byte[] code = new byte[6];		// 地址
	private long codeValue;	// 地址数值

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
	/**
	 * 设置地址。
	 * @param cv 地址的数值。
	 * @return 地址的字节长度。
	 */
	public int setCode(long cv) {
		for(int i = code.length - 1; i >= 0; i--) {
			code[i] = (byte) (cv & 0xFF);
			cv = cv >> 8;
		}
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
	
	/** ids 参数ID列表 */
	private Set<Long> ids = new HashSet<Long>();
	public Set<Long> getIds() {
		return ids;
	}
	public void addId(long id) {
		ids.add(id);
	}
	
	/**
	 * 获取对象的字节内容的长度。
	 * @return 对象的字节内容的长度。
	 */
	public int byteLength() {
		return 1 + code.length + 4 * ids.size();
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
		dst[pos] = (byte) ids.size();
		pos++;
		System.arraycopy(code, 0, dst, pos, code.length);
		pos += code.length;
		for(Long id : ids) {
			UInt32.put(id, bo, dst, pos);
			pos += 4;
		}
		return byteLength();
	}
}
