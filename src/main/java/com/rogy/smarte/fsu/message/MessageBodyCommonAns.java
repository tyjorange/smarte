package com.rogy.smarte.fsu.message;


import com.rogy.smarte.fsu.VirtualFsuUtil;
import com.rogy.smarte.util.UInt16;

import java.nio.ByteOrder;

/**
 * 通用应答消息体。
 */
public class MessageBodyCommonAns {
	private int id = 0;					// 消息ID
	private int serialno = 0;				// 消息流水号
	private byte result = 0;	// 结果
	private byte[] code = new byte[6];		// 地址
	private long codeValue = 0L;
	
	private MessageBodyCommonAns() {};
	/**
	 * 从字节数组解析MessageModbusBodyCommonAns对象。
	 * @param bo 字节序。
	 * @param src 包含对象内容的源字节数组。
	 * @return 解析得到的MessageModbusBodyCommonAns对象。null表示失败。
	 */
	public static MessageBodyCommonAns create(ByteOrder bo, byte[] src) {
		return create(bo, src, 0, src.length);
	}
	/**
	 * 从字节数组解析MessageModbusBodyCommonAns对象。
	 * @param bo 字节序。
	 * @param src 包含对象内容的源字节数组。
	 * @param begin 对象内容起始字节索引。
	 * @param len 对象内容长度。
	 * @return 解析得到的MessageModbusBodyCommonAns对象。null表示失败。
	 */
	public static MessageBodyCommonAns create(ByteOrder bo, byte[] src, int begin, int len) {
		MessageBodyCommonAns ans = new MessageBodyCommonAns();
		int pos = begin;
		ans.serialno = UInt16.get(bo, src, pos);
		pos += 2;
		ans.id = UInt16.get(bo, src, pos);
		pos += 2;
		ans.result = src[pos];
		pos += 1;
		pos += ans.setCode(src, pos);
		return ans;
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

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSerialno() {
		return serialno;
	}
	public void setSerialno(int serialno) {
		this.serialno = serialno;
	}
	public byte getResult() {
		return result;
	}
	public void setResult(byte result) {
		this.result = result;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{id=0x");
		sb.append(String.format("%04X", id));
		sb.append(" no=");
		sb.append(serialno);
		sb.append(" result=");
		sb.append(result);
		sb.append(" code=0x");
		sb.append(String.format("%012X", codeValue));
		sb.append("}");
		return sb.toString();
	}
}
