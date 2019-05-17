package com.rogy.smarte.fsu.message;


import com.rogy.smarte.fsu.VirtualFsuUtil;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class MessageBodyParam {
	private byte[] code = new byte[6];		// 地址
	private long codeValue = 0L;	// 地址数值
	private List<MessageBodyParamValue> values = new ArrayList<MessageBodyParamValue>();
	public List<MessageBodyParamValue> getValues() {
		return values;
	}

	/**
	 * 从字节数组解析MessageModbusBodyParam对象。
	 * @param bo 字节序。
	 * @param src 包含对象内容的源字节数组。
	 * @return 解析得到的MessageModbusBodyParam对象。null表示失败。
	 */
	public static MessageBodyParam create(ByteOrder bo, byte[] src) {
		return create(bo, src, 0, src.length);
	}
	/**
	 * 从字节数组解析MessageModbusBodyParam对象。
	 * @param bo 字节序。
	 * @param src 包含对象内容的源字节数组。
	 * @param srcBegin 对象内容起始字节索引。
	 * @param len 对象内容长度。
	 * @return 解析得到的MessageModbusBodyParam对象。null表示失败。
	 */
	public static MessageBodyParam create(ByteOrder bo, byte[] src, int srcBegin, int len) {
		MessageBodyParam param = new MessageBodyParam();
		int pos = srcBegin;
		int count = src[pos];
		pos++;
		pos += param.setCode(src, pos);
		for(int i = 0; i < count; i++) {
			MessageBodyParamValue v = MessageBodyParamValue.create(bo, src, pos);
			pos += v.byteLength();
			param.addValue(v);
		}
		return param;
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
	
	public void addValue(MessageBodyParamValue value) {
		values.add(value);
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
		dst[pos] = (byte) values.size();
		pos++;
		System.arraycopy(code, 0, dst, pos, code.length);
		pos += code.length;
		for(MessageBodyParamValue v : values)
			pos += v.getBytes(bo, dst, pos);
		return byteLength();
	}

	/**
	 * 获取对象的字节内容的长度。
	 * @return 对象的字节内容的长度。
	 */
	public int byteLength() {
		int len = 1 + 6;
		for(MessageBodyParamValue v : values)
			len += v.byteLength();
		return len;
	}
	
	public String toString(ByteOrder byteOrder) {
		StringBuilder sb = new StringBuilder();
		sb.append("{code=0x");
		sb.append(String.format("%012X", codeValue));
		sb.append(" count=");
		int size = values.size();
		sb.append(size);
		sb.append(" value=[");
		for(int i = 0; i < size; i++) {
			sb.append(values.get(i).toString(byteOrder));
			if(i < size - 1)
				sb.append(",");
		}
		sb.append("]}");
		return sb.toString();
	}
}
