package com.rogy.smarte.fsu.message;


import com.rogy.smarte.util.BCD8421;
import com.rogy.smarte.util.UInt32;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class MessageBodyDataChange {
	
	private long no = 0;		// 序号
	public long getNo() {
		return no;
	}
	
	private List<MessageBodyDataChangeValue> values = new ArrayList<MessageBodyDataChangeValue>();
	public List<MessageBodyDataChangeValue> getValues() {
		return values;
	}
	
	private MessageBodyDataChange() {
	}
	/**
	 * 从字节数组解析MessageModbusBodyData对象。
	 * @param bo 字节序。
	 * @param src 包含对象内容的源字节数组。
	 * @return 解析得到的MessageModbusBodyData对象。null表示失败。
	 */
	public static MessageBodyDataChange create(ByteOrder bo, byte[] src) {
		return create(bo, src, 0, src.length);
	}
	/**
	 * 从字节数组解析MessageModbusBodyDataChange对象。
	 * @param bo 字节序。
	 * @param src 包含对象内容的源字节数组。
	 * @param srcBegin 对象内容起始字节索引。
	 * @param len 对象内容长度。
	 * @return 解析得到的MessageModbusBodyDataChange对象。null表示失败。
	 */
	public static MessageBodyDataChange create(ByteOrder bo, byte[] src, int srcBegin, int len) {
		MessageBodyDataChange data = new MessageBodyDataChange();
		int pos = srcBegin;
		data.no = UInt32.get(bo, src, pos);
		pos += 4;
		int count = (len - 4) / 34;
		long vl;
		int vi;
		for(int i = 0; i < count; i++) {
			MessageBodyDataChangeValue value = new MessageBodyDataChangeValue();
			data.values.add(value);
			pos += value.setBrkCode(src, pos);
			vl = UInt32.get(bo, src, pos);
			pos += 4;
			value.values[0] = (double)vl / 10.00;
			vl = UInt32.get(bo, src, pos);
			pos += 4;
			value.values[1] = (double)vl / 1000.00;
			vl = UInt32.get(bo, src, pos);
			pos += 4;
			value.values[2] = (double)vl / 10000.00;
			vl = UInt32.get(bo, src, pos);
			pos += 4;
			value.values[3] = (double)vl / 10000.00;
			vl = UInt32.get(bo, src, pos);
			pos += 4;
			value.values[4] = (double)vl / 1000.00;
			// 温度
			byte b[] = new byte[2];
			System.arraycopy(src, pos, b, 0, 2);
			pos += 2;
			double minus = 1.0;
			if((b[0] & 0xf0) == 0x80) {
				minus = -1.0;
				b[0] &= 0x0f;
			}
			vi = Integer.parseInt(BCD8421.BCD8421ToDecimal(b));
			value.values[5] = (double)vi * minus / 10.00;
			// 时间
			pos += value.setDatetime(src, pos);
		}
		return data;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{no=");
		sb.append(no);
		sb.append(" data=");
		sb.append(values.toString());
		sb.append("}");
		return sb.toString();
	}
}
