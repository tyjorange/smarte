package com.rogy.smarte.fsu.message;


import com.rogy.smarte.util.BCD8421;
import com.rogy.smarte.util.UInt16;
import com.rogy.smarte.util.UInt32;

import java.nio.ByteOrder;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MessageBodyParamValue {
	private long id;		// 参数id
	public long getId() {
		return id;
	}
	
	private byte[] valueBytes = null;	// 数值
	
	private MessageBodyParamValue() {};
	
	/**
	 * 创建MessageModbusBodyParamValue对象。
	 * @param id 参数id。
	 * @param n 当id==0x0004时表示个数；其它时无效。
	 */
	public MessageBodyParamValue(long id, int n) {
		this.id = id;
		if(id == 0x00000001L)
			valueBytes = new byte[1];
		else if(id == 0x00000002L)
			valueBytes = new byte[2];
		else if(id == 0x00000003L)
			valueBytes = new byte[7];
		else if(id == 0x00000004L)
			valueBytes = new byte[6 * n];
		else if(id == 0x00000005L)
			valueBytes = new byte[2];
		else if(id == 0x00000006L)
			valueBytes = new byte[2];
		else if(id == 0x00000007L)
			valueBytes = new byte[2];
		else if(id == 0x00000008L)
			valueBytes = new byte[2];
		else if(id == 0x00000009L)
			valueBytes = new byte[2];
		else if(id == 0x0000000AL)
			valueBytes = new byte[2];
		else if(id == 0x0000000BL)
			valueBytes = new byte[2];
		else if(id == 0x0000000CL)
			valueBytes = new byte[2];
		else if(id == 0x0000000DL)
			valueBytes = new byte[2];
		else if(id == 0x0000000EL)
			valueBytes = new byte[2];
		else if(id == 0x0000000FL)
			valueBytes = new byte[2];
		else if(id == 0x00000010L)
			valueBytes = new byte[2];
		else if(id == 0x00000011L)
			valueBytes = new byte[4];
		else if(id == 0x00000012L)
			valueBytes = new byte[2];
		else if(id == 0x00000013L)
			valueBytes = new byte[2];
		else if(id == 0x00000014L)
			valueBytes = new byte[1];
		else if(id == 0x00000015L)
			valueBytes = new byte[1];
		else if(id == 0x00000016L)
			valueBytes = new byte[3];
		else if(id == 0x00000017L)
			valueBytes = new byte[3];
		else if(id == 0x00000018L)
			valueBytes = new byte[4];
		else if(id == 0x00000019L)
			valueBytes = new byte[4];
		else
			throw new java.lang.IllegalArgumentException("invalid id");
	}

	/**
	 * 从字节数组解析MessageModbusBodyParamValue对象。
	 * @param bo 字节序。
	 * @param src 包含对象内容的源字节数组。
	 * @return 解析得到的MessageModbusBodyParamValue对象。null表示失败。
	 */
	public static MessageBodyParamValue create(ByteOrder bo, byte[] src) {
		return create(bo, src, 0);
	}
	/**
	 * 从字节数组解析MessageModbusBodyParamValue对象。
	 * @param bo 字节序。
	 * @param src 包含对象内容的源字节数组。
	 * @param srcBegin 对象内容起始字节索引。
	 * @return 解析得到的MessageModbusBodyParamValue对象。null表示失败。
	 */
	public static MessageBodyParamValue create(ByteOrder bo, byte[] src, int srcBegin) {
		MessageBodyParamValue pv = new MessageBodyParamValue();
		int pos = srcBegin;
		pv.id = UInt32.get(bo, src, pos);
		pos += 4;
		int vlen = src[pos] & 0xFF;
		pos++;
		if(vlen > 0) {
			pv.valueBytes = new byte[vlen];
			System.arraycopy(src, pos, pv.valueBytes, 0, vlen);
			pos += vlen;
		}
		else
			pv.valueBytes = null;
		return pv;
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
		UInt32.put(id, bo, dst, pos);
		pos += 4;
		if(valueBytes == null)
			dst[pos] = 0;
		else
			dst[pos] = (byte) valueBytes.length;
		pos++;
		if(valueBytes != null) {
			System.arraycopy(valueBytes, 0, dst, pos, valueBytes.length);
			pos += valueBytes.length;
		}
		return byteLength();
	}

	/**
	 * 获取对象的字节内容的长度。
	 * @return 对象的字节内容的长度。
	 */
	public int byteLength() {
		if(valueBytes != null)
			return 4 + 1 + valueBytes.length;
		else
			return 4 + 1;
	}

	public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyMMddHHmmss");
	//public static final DateTimeFormatter RTC_FORMATTER = DateTimeFormatter.ofPattern("yyMMddeeHHmmss");
	public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmss");

	/**
	 * 设置数值。
	 * @param bo 字节序。
	 * @param value 数值。数值的源类型要和getValue返回的源类型一致。
	 */
	public void setValue(ByteOrder bo, Object value) {
		if(id == 0x00000001L)
			valueBytes[0] = ((Byte) value).byteValue();
		else if(id == 0x00000002L)
			UInt16.put(((Integer) value).intValue() & 0xFFFF, bo, valueBytes, 0);
		else if(id == 0x00000003L) {
			LocalDateTime dtv = (LocalDateTime) value;
			String dt = dtv.format(DATETIME_FORMATTER);
			String rtc = dt.substring(0, 6) + String.format("%02d", dtv.getDayOfWeek().getValue()) + dt.substring(6);
			BCD8421.DecimalToBCD8421(rtc, valueBytes, 0);
		}
		else if(id == 0x00000004L)
			System.arraycopy((byte[]) value, 0, valueBytes, 0, valueBytes.length);
		else if(id == 0x00000005L) {
			String v = String.format("%04d", (((int)(((Double) value).doubleValue() * 10)) & 0xFFFF));
			if(bo != ByteOrder.BIG_ENDIAN)
				v = v.substring(2, 4) + v.substring(0, 2);
			BCD8421.DecimalToBCD8421(v, valueBytes, 0);
		}
		else if(id == 0x00000006L) {
			String v = String.format("%04d", (((int)(((Double) value).doubleValue() * 10)) & 0xFFFF));
			if(bo != ByteOrder.BIG_ENDIAN)
				v = v.substring(2, 4) + v.substring(0, 2);
			BCD8421.DecimalToBCD8421(v, valueBytes, 0);
		}
		else if(id == 0x00000007L) {
			String v = String.format("%04d", (((int)(((Double) value).doubleValue() * 10)) & 0xFFFF));
			if(bo != ByteOrder.BIG_ENDIAN)
				v = v.substring(2, 4) + v.substring(0, 2);
			BCD8421.DecimalToBCD8421(v, valueBytes, 0);
		}
		else if(id == 0x00000008L) {
			String v = String.format("%04d", (((int)(((Double) value).doubleValue() * 10)) & 0xFFFF));
			if(bo != ByteOrder.BIG_ENDIAN)
				v = v.substring(2, 4) + v.substring(0, 2);
			BCD8421.DecimalToBCD8421(v, valueBytes, 0);
		}
		else if(id == 0x00000009L)
			UInt16.put(((Integer) value).intValue(), bo, valueBytes, 0);
		else if(id == 0x0000000AL)
			UInt16.put(((Integer) value).intValue(), bo, valueBytes, 0);
		else if(id == 0x0000000BL)
			UInt16.put(((Integer) value).intValue(), bo, valueBytes, 0);
		else if(id == 0x0000000CL)
			UInt16.put(((Integer) value).intValue(), bo, valueBytes, 0);
		else if(id == 0x0000000DL) {
			String v = String.format("%04d", (((int)(((Double) value).doubleValue() * 10)) & 0xFFFF));
			if(bo != ByteOrder.BIG_ENDIAN)
				v = v.substring(2, 4) + v.substring(0, 2);
			BCD8421.DecimalToBCD8421(v, valueBytes, 0);
		}
		else if(id == 0x0000000EL)
			UInt16.put(((Integer) value).intValue(), bo, valueBytes, 0);
		else if(id == 0x0000000FL)
			UInt16.put(((Integer) value).intValue(), bo, valueBytes, 0);
		else if(id == 0x00000010L)
			UInt16.put(((Integer) value).intValue(), bo, valueBytes, 0);
		else if(id == 0x00000011L) {
			String v = String.format("%08d", (((long)(((Double) value).doubleValue() * 1000)) & 0xFFFFFFFFL));
			if(bo != ByteOrder.BIG_ENDIAN)
				v = v.substring(6, 8) + v.substring(4, 6) + v.substring(2, 4) + v.substring(0, 2);
			BCD8421.DecimalToBCD8421(v, valueBytes, 0);
		}
		else if(id == 0x00000012L)
			UInt16.put(((Integer) value).intValue(), bo, valueBytes, 0);
		else if(id == 0x00000013L)
			UInt16.put(((Integer) value).intValue(), bo, valueBytes, 0);
		else if(id == 0x00000014L)
			valueBytes[0] = ((Byte) value).byteValue();
		else if(id == 0x00000015L)
			valueBytes[0] = ((Byte) value).byteValue();
		else if(id == 0x00000016L) {
			String t = ((LocalTime) value).format(TIME_FORMATTER);
			BCD8421.DecimalToBCD8421(t, valueBytes, 0);
		}
		else if(id == 0x00000017L) {
			String t = ((LocalTime) value).format(TIME_FORMATTER);
			BCD8421.DecimalToBCD8421(t, valueBytes, 0);
		}
		else if(id == 0x00000018L) {
			String v = String.format("%08d", (((long)(((Double) value).doubleValue() * 100)) & 0xFFFFFFFFL));
			BCD8421.DecimalToBCD8421(v, valueBytes, 0);
		}
		else if(id == 0x00000019L) {
			String v = String.format("%08d", (((long)(((Double) value).doubleValue() * 100)) & 0xFFFFFFFFL));
			BCD8421.DecimalToBCD8421(v, valueBytes, 0);
		}
		else
			throw new java.lang.IllegalArgumentException("invalid id");
	}

	/**
	 * 获取数值。
	 * @param bo 字节序。
	 * @return 数值对象。
	 */
	public Object getValue(ByteOrder bo) {
		if(valueBytes == null)
			return null;

		if(id == 0x00000001L)
			return Byte.valueOf(valueBytes[0]);
		else if(id == 0x00000002L)
			return UInt16.get(bo, valueBytes, 0);
		else if(id == 0x00000003L) {
			String d = BCD8421.BCD8421ToDecimal(valueBytes, 0, 3);
			String t = BCD8421.BCD8421ToDecimal(valueBytes, 4, 3);
			return LocalDateTime.parse(d + t, DateTimeFormatter.ofPattern("yyMMddHHmmss"));
		}
		else if(id == 0x00000004L)
			return valueBytes;
		else if(id == 0x00000005L) {
			byte[] vbs;
			if(bo == ByteOrder.BIG_ENDIAN)
				vbs = valueBytes;
			else {
				vbs = new byte[2];
				vbs[0] = valueBytes[1];
				vbs[1] = valueBytes[0];
			}
			int iv = Integer.parseInt(BCD8421.BCD8421ToDecimal(vbs));
			return Double.valueOf((double)iv / 10.0);
		}
		else if(id == 0x00000006L) {
			byte[] vbs;
			if(bo == ByteOrder.BIG_ENDIAN)
				vbs = valueBytes;
			else {
				vbs = new byte[2];
				vbs[0] = valueBytes[1];
				vbs[1] = valueBytes[0];
			}
			int iv = Integer.parseInt(BCD8421.BCD8421ToDecimal(vbs));
			return Double.valueOf((double)iv / 10.0);
		}
		else if(id == 0x00000007L) {
			byte[] vbs;
			if(bo == ByteOrder.BIG_ENDIAN)
				vbs = valueBytes;
			else {
				vbs = new byte[2];
				vbs[0] = valueBytes[1];
				vbs[1] = valueBytes[0];
			}
			int iv = Integer.parseInt(BCD8421.BCD8421ToDecimal(vbs));
			return Double.valueOf((double)iv / 10.0);
		}
		else if(id == 0x00000008L) {
			byte[] vbs;
			if(bo == ByteOrder.BIG_ENDIAN)
				vbs = valueBytes;
			else {
				vbs = new byte[2];
				vbs[0] = valueBytes[1];
				vbs[1] = valueBytes[0];
			}
			int iv = Integer.parseInt(BCD8421.BCD8421ToDecimal(vbs));
			return Double.valueOf((double)iv / 10.0);
		}
		else if(id == 0x00000009L)
			return Integer.valueOf(UInt16.get(bo, valueBytes, 0));
		else if(id == 0x0000000AL)
			return Integer.valueOf(UInt16.get(bo, valueBytes, 0));
		else if(id == 0x0000000BL)
			return Integer.valueOf(UInt16.get(bo, valueBytes, 0));
		else if(id == 0x0000000CL)
			return Integer.valueOf(UInt16.get(bo, valueBytes, 0));
		else if(id == 0x0000000DL) {
			byte[] vbs;
			if(bo == ByteOrder.BIG_ENDIAN)
				vbs = valueBytes;
			else {
				vbs = new byte[2];
				vbs[0] = valueBytes[1];
				vbs[1] = valueBytes[0];
			}
			int iv = Integer.parseInt(BCD8421.BCD8421ToDecimal(vbs));
			return Double.valueOf((double)iv / 10.0);
		}
		else if(id == 0x0000000EL)
			return Integer.valueOf(UInt16.get(bo, valueBytes, 0));
		else if(id == 0x0000000FL)
			return Integer.valueOf(UInt16.get(bo, valueBytes, 0));
		else if(id == 0x00000010L)
			return Integer.valueOf(UInt16.get(bo, valueBytes, 0));
		else if(id == 0x00000011L) {
			byte[] vbs;
			if(bo == ByteOrder.BIG_ENDIAN)
				vbs = valueBytes;
			else {
				vbs = new byte[4];
				vbs[0] = valueBytes[3];
				vbs[1] = valueBytes[2];
				vbs[2] = valueBytes[1];
				vbs[3] = valueBytes[0];
			}
			long lv = Long.parseLong(BCD8421.BCD8421ToDecimal(vbs));
			return Double.valueOf((double)lv / 1000.0);
		}
		else if(id == 0x00000012L)
			return Integer.valueOf(UInt16.get(bo, valueBytes, 0));
		else if(id == 0x00000013L)
			return Integer.valueOf(UInt16.get(bo, valueBytes, 0));
		else if(id == 0x00000014L)
			return Byte.valueOf(valueBytes[0]);
		else if(id == 0x00000015L)
			return Byte.valueOf(valueBytes[0]);
		else if(id == 0x00000016L) {
			String t = BCD8421.BCD8421ToDecimal(valueBytes);
			return LocalTime.parse(t, TIME_FORMATTER);
		}
		else if(id == 0x00000017L) {
			String t = BCD8421.BCD8421ToDecimal(valueBytes);
			return LocalTime.parse(t, TIME_FORMATTER);
		}
		else if(id == 0x00000018L) {
			long lv = Long.parseLong(BCD8421.BCD8421ToDecimal(valueBytes));
			return Double.valueOf((double)lv / 100.0);
		}
		else if(id == 0x00000019L) {
			long lv = Long.parseLong(BCD8421.BCD8421ToDecimal(valueBytes));
			return Double.valueOf((double)lv / 100.0);
		}
		else
			throw new java.lang.IllegalArgumentException("invalid id");
	}
	
	public String toString(ByteOrder byteOrder) {
		StringBuilder sb = new StringBuilder();
		sb.append("{id=0x");
		sb.append(String.format("%08X", id));
		sb.append(" value=");
		Object value = getValue(byteOrder);
		if(value == null)
			sb.append("NULL");
		else if(id == 0x00000001L)
			sb.append(String.format("0x%02X", ((Byte)value).byteValue() & 0xFF));
		else if(id == 0x00000002L)
			sb.append(((Integer)value).intValue());
		else if(id == 0x00000003L)
			sb.append(((LocalDateTime)value).toString());
		else if(id == 0x00000004L) {
			for(int i = 0; i < valueBytes.length / 6; i++) {
				if(i != 0)
					sb.append(",0x");
				else
					sb.append("0x");
				for(int j = 0; j < 6; j++)
					sb.append(String.format("%02X", valueBytes[6 * i + j] & 0xFF));
			}
		}
		else if(id == 0x00000005L)
			sb.append(((Double)value).doubleValue());
		else if(id == 0x00000006L)
			sb.append(((Double)value).doubleValue());
		else if(id == 0x00000007L)
			sb.append(((Double)value).doubleValue());
		else if(id == 0x00000008L)
			sb.append(((Double)value).doubleValue());
		else if(id == 0x00000009L)
			sb.append(((Integer)value).intValue());
		else if(id == 0x0000000AL)
			sb.append(((Integer)value).intValue());
		else if(id == 0x0000000BL)
			sb.append(((Integer)value).intValue());
		else if(id == 0x0000000CL)
			sb.append(((Integer)value).intValue());
		else if(id == 0x0000000DL)
			sb.append(((Double)value).doubleValue());
		else if(id == 0x0000000EL)
			sb.append(((Integer)value).intValue());
		else if(id == 0x0000000FL)
			sb.append(((Integer)value).intValue());
		else if(id == 0x00000010L)
			sb.append(((Integer)value).intValue());
		else if(id == 0x00000011L)
			sb.append(((Double)value).doubleValue());
		else if(id == 0x00000012L)
			sb.append(((Integer)value).intValue());
		else if(id == 0x00000013L)
			sb.append(((Integer)value).intValue());
		else if(id == 0x00000014L)
			sb.append(String.format("0x%02X", ((Byte)value).byteValue() & 0xFF));
		else if(id == 0x00000015L)
			sb.append(String.format("0x%02X", ((Byte)value).byteValue() & 0xFF));
		else if(id == 0x00000016L)
			sb.append(((LocalTime)value).toString());
		else if(id == 0x00000017L)
			sb.append(((LocalTime)value).toString());
		else if(id == 0x00000018L)
			sb.append(((Double)value).doubleValue());
		else if(id == 0x00000019L)
			sb.append(((Double)value).doubleValue());
		sb.append("}");
		
		return sb.toString();
	}
}
