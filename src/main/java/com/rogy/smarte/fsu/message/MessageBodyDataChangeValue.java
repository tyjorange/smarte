package com.rogy.smarte.fsu.message;

import com.rogy.smarte.fsu.VirtualFsuUtil;
import com.rogy.smarte.util.BCD8421;

import java.time.LocalDateTime;


public class MessageBodyDataChangeValue {
	private byte[] brkCode = new byte[6];		// 断路器地址
	private String brkCodeString;
	private long brkCodeValue = 0L;	// 断路器地址数值
	/**
	 * 指标类型ID。
	 */
	//public static final String[] typeIds = {"dy", "dl", "yggl", "wggl", "glys", "wd"};
	public static final Short[] typeIds = {4, 1, 11, 9, 5, 7};
	/**
	 * 指标数值。
	 */
	public double[] values = new double[typeIds.length];
	private byte[] datetime = new byte[6];	// 时间(BCD码)
	private LocalDateTime datetimeValue = null;

	/**
	 * 设置断路器地址。
	 * @param src 包含地址的字节数组。
	 * @param srcBegin 地址内容的起始字节索引。
	 * @return 断路器地址的字节长度。
	 */
	public int setBrkCode(byte[] src, int srcBegin) {
		// 拷贝地址内容
		System.arraycopy(src, srcBegin, brkCode, 0, brkCode.length);
		// 计算地址数值
		brkCodeValue = VirtualFsuUtil.calcCodeValue(brkCode, 0, brkCode.length);
		brkCodeString = String.format("%012X", brkCodeValue);

		return brkCode.length;
	}

	public final byte[] getBrkCode() {
		return brkCode;
	}
	public String getBrkCodeString() {
		return brkCodeString;
	}
	public long getBrkCodeValue() {
		return brkCodeValue;
	}

	/**
	 * 设置时间BCD码。
	 * @param src 包含时间的字节数组。
	 * @param srcBegin 时间内容的起始字节索引。
	 * @return 时间BCD码的字节长度。
	 */
	public int setDatetime(byte[] src, int srcBegin) {
		// 拷贝时间内容
		System.arraycopy(src, srcBegin, datetime, 0, datetime.length);
		// 计算时间数值
		String dts = BCD8421.BCD8421ToDecimal(datetime);
		datetimeValue = LocalDateTime.parse(dts, Message.DATETIME_FORMATTER);

		return datetime.length;
	}

	public LocalDateTime getDatetimeValue() {
		return datetimeValue;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{code=");
		sb.append(String.format("%012X", brkCodeValue));
		sb.append(" values=[");
		for(int i = 0; i < values.length; i++) {
			if(i != 0)
				sb.append(", ");
			sb.append(String.format("%s=", typeIds[i]));
			sb.append(values[i]);
		}
		sb.append("] datetime=");
		sb.append(datetimeValue.toString());
		sb.append("}");
		return sb.toString();
	}
}
