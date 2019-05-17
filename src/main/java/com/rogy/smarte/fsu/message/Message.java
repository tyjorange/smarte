package com.rogy.smarte.fsu.message;

import com.rogy.smarte.util.UInt16;

import java.nio.ByteOrder;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;


/**
 * 传输消息类。
 */
public class Message {
	/** 消息分隔符 */
	public static final byte DELIMITER = 0x7F;
	/** 消息分隔符的转义符 */
	public static final byte DELIMITER_TRANS = 0x7D;
	/** 错误消息ID **/
	public static final int ID_ERROR_MESSAGE = 0xFFFF;

	public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyMMddHHmmss");
	public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmss");

	// 消息头
	private int id = ID_ERROR_MESSAGE;					// 消息ID
	private int prop = 0;					// 消息体属性
	private int phoneno = 0;				// 终端号
	private int serialno = 0;				// 消息流水号
	private int splittotal = 0;			// (分包时有效)总包数
	private int splitno = 0;				// (分包时有效)从1开始的包序号
	// 消息体
	private byte[] body = null;
	// 校验码
	private byte checksum;

	/**
	 * 从字节数组解析MessageModbus对象。
	 * @param byteOrder 字节序
	 * @param src 包含对象内容的源字节数组。不包含头尾的分割符。
	 * @return 解析得到的MessageModbus对象。null表示失败。
	 */
	public static Message create(ByteOrder byteOrder, final byte[] src) {
		if(src == null || src.length < getMinSize())
			return null;
		return create(byteOrder, src, 0, src.length);
	}
	/**
	 * 从字节数组解析MessageModbus对象。
	 * @param byteOrder 字节序
	 * @param src 包含对象内容的源字节数组。不包含头尾的分割符。
	 * @param srcBegin 对象内容起始字节索引。
	 * @param len 对象内容长度。
	 * @return 解析得到的MessageModbus对象。null表示失败。
	 */
	public static Message create(ByteOrder byteOrder, final byte[] src, int srcBegin, int len) {
		if(src == null || src.length < srcBegin + len || len < Message.getMinSize())
			return null;

		Message message = null;
		try {
			// 转义还原
			byte[] mmsrc = transferBack(src, srcBegin, len);
			message = new Message();
			int pos = 0;
			message.id = UInt16.get(byteOrder, mmsrc, pos);	// id
			pos += 2;
			message.prop = UInt16.get(byteOrder, mmsrc, pos);	// 属性
			pos += 2;
			boolean isSplit = message.isSplit();	// 是否分包
			int bodyLen = message.getBodyLength();	// 消息体长度
			int totalLen = getMinSize() + (isSplit ? 4 : 0) + bodyLen;	// 总长度
			if(mmsrc.length >= totalLen) {
				message.phoneno = UInt16.get(byteOrder, mmsrc, pos);	// 终端号
				pos += 2;
				message.serialno = UInt16.get(byteOrder, mmsrc, pos);	// 消息流水号
				pos += 2;
				if(isSplit) {
					message.splittotal = UInt16.get(byteOrder, mmsrc, pos);	// 分包总数
					pos += 2;
					message.splitno = UInt16.get(byteOrder, mmsrc, pos);	// 分包序号
					pos += 2;
				}
				// 消息体
				if(bodyLen > 0) {
					message.setBodyContent(mmsrc, pos, bodyLen);
					pos += bodyLen;
				}
				message.checksum = mmsrc[pos];	// 校验码
				pos++;
			}
			else {	// 数据不足
				message = null;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		return message;
	}

	/**
	 * 获取消息对象的字节数组。
	 * @param byteOrder 字节序。
	 * @param withDelimiter 是否包含头尾分割符。
	 * @return 消息对象的字节数组。null表示失败。
	 */
	public byte[] getBytes(ByteOrder byteOrder, boolean withDelimiter) {
		boolean isSplit = isSplit();	// 是否分包
		int bodyLen = getBodyLength();	// 消息体长度
		int totalLen = getMinSize() + (isSplit ? 4 : 0) + bodyLen;	// 总长度
		byte[] srcBytes = new byte[totalLen];	// 未转义的源内容字节数组
		int pos = 0;
		UInt16.put(id, byteOrder, srcBytes, pos);
		pos += 2;
		UInt16.put(prop, byteOrder, srcBytes, pos);
		pos += 2;
		UInt16.put(phoneno, byteOrder, srcBytes, pos);
		pos += 2;
		UInt16.put(serialno, byteOrder, srcBytes, pos);
		pos += 2;
		if(isSplit) {
			UInt16.put(splittotal, byteOrder, srcBytes, pos);
			pos += 2;
			UInt16.put(splitno, byteOrder, srcBytes, pos);
			pos += 2;
		}
		if(bodyLen > 0) {
			System.arraycopy(body, 0, srcBytes, pos, bodyLen);
			pos += bodyLen;
		}
		srcBytes[pos] = checksum;
		pos++;

		// 转义
		byte[] trBytes = transfer(srcBytes);
		if(!withDelimiter)
			return trBytes;
		else {
			byte[] trDltBytes = new byte[trBytes.length + 2];
			trDltBytes[0] = trDltBytes[trDltBytes.length - 1] = DELIMITER;
			System.arraycopy(trBytes, 0, trDltBytes, 1, trBytes.length);
			return trDltBytes;
		}
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id & 0xFFFF;	// 16位
	}
	public int getProp() {
		return prop;
	}
	public void setProp(int prop) {
		this.prop = prop;
	}
	public int getPhoneno() {
		return phoneno;
	}
	public void setPhoneno(int phoneno) {
		this.phoneno = phoneno;
	}
	/**
	 * 是否分包。
	 * @return 是否分包。
	 */
	public boolean isSplit() {
		return (prop & 0x2000) > 0;
	}
	/**
	 * 设置是否分包。
	 * @param split 是否分包。
	 */
	public void setSplit(boolean split) {
		if(split)
			prop |= 0x2000;
		else
			prop &= (~0x2000);
	}
	/**
	 * 获取加密方式。
	 * @return 加密方式。
	 */
	public int getEncrypt() {
		return (prop >> 10) & 0x07;
	}
	/**
	 * 设置加密方式。
	 * @param encrypt 加密方式。
	 */
	public void setEncrypt(int encrypt) {
		//prop |= (encrypt & 0x7) << 10;
		prop = (prop & 0xE3FF) + ((encrypt & 0x7) << 10);
	}
	/**
	 * 获取消息体长度。
	 * @return 消息体长度。
	 */
	public int getBodyLength() {
		return prop & 0x3FF;
	}
	/**
	 * 设置消息体长度。
	 * @param length 消息体长度。
	 */
	public void setBodyLength(int length) {
		prop = (prop & 0xFC00) + (length & 0x3FF);
	}
	public int getSerialno() {
		return serialno;
	}
	public void setSerialno(int serialno) {
		this.serialno = serialno & 0xFFFF;	// 16位
	}
	public int getSplittotal() {
		return splittotal;
	}
	public void setSplittotal(int splittotal) {
		this.splittotal = splittotal;
	}
	public int getSplitno() {
		return splitno;
	}
	public void setSplitno(int splitno) {
		this.splitno = splitno;
	}
	public final byte[] getBody() {
		return body;
	}
	/**
	 * 设置消息体。
	 * 此方法不会拷贝源内容来生成新的消息体数组。
	 * 注意，本方法仅设置消息体内容，并不更改消息体长度属性。
	 * 要设置消息体长度需要调用设置消息体长度的方法。
	 * @param body 包含要设置的消息体内容的数组。
	 */
	public void wrapBody(final byte[] body) {
		this.body = body;
	}
	/**
	 * 设置消息体内容。
	 * 此方法将拷贝源内容来生成新的消息体数组。
	 * 注意，本方法仅设置消息体内容，并不更改消息体长度属性。
	 * 要设置消息体长度需要调用设置消息体长度的方法。
	 * @param body 包含要设置的消息体内容的数组。
	 */
	public void setBodyContent(final byte[] body) {
		setBodyContent(body, 0, body.length);
	}
	/**
	 * 设置消息体内容。
	 * 此方法将拷贝源内容来生成新的消息体数组。
	 * 注意，本方法仅设置消息体内容，并不更改消息体长度属性。
	 * 要设置消息体长度需要调用设置消息体长度的方法。
	 * @param body 包含要设置的消息体内容的数组。
	 * @param begin 消息体内容起始索引。
	 * @param len 消息体内容长度。
	 */
	public void setBodyContent(final byte[] body, int begin, int len) {
		this.body = Arrays.copyOfRange(body, begin, begin + len);
	}
	public byte getChecksum() {
		return checksum;
	}
	public void setChecksum(byte checksum) {
		this.checksum = checksum;
	}
	/**
	 * 计算校验和。
	 * @return 校验和。
	 */
	public byte calcChecksum() {
		int sum = 0;
		int w;

		// 消息头
		w = id;
		sum = (w >> 8) & 0xff;
		sum ^= (w & 0xff);
		w = prop;
		sum ^= ((w >> 8) & 0xff);
		sum ^= (w & 0xff);
		w = phoneno;
		sum ^= ((w >> 8) & 0xff);
		sum ^= (w & 0xff);
		w = serialno;
		sum ^= ((w >> 8) & 0xff);
		sum ^= (w & 0xff);
		if(isSplit()) {
			w = splittotal;
			sum ^= ((w >> 8) & 0xff);
			sum ^= (w & 0xff);
			w = splitno;
			sum ^= ((w >> 8) & 0xff);
			sum ^= (w & 0xff);
		}

		// 消息体
		if(body != null) {
			for(int i = 0; i < getBodyLength(); i++) {
				sum ^= body[i];
			}
		}

		return (byte) (sum & 0xFF);
	}

	@Override
	public boolean equals(Object o) {
		if(o == null || !(o instanceof Message))
			return false;
		Message mm = (Message)o;
		if(prop != mm.prop)
			return false;
		int bodylen = getBodyLength();
		if(bodylen > 0)
			if(!Arrays.equals(body, mm.body))
				return false;
		return id == mm.id &&
				phoneno == mm.phoneno &&
				serialno == mm.serialno &&
				splittotal == mm.splittotal &&
				splitno == mm.splitno &&
				checksum == mm.checksum;
	}

	/**
	 * 消息内容转义。
	 * @param src 包含要处理内容的字节数组。
	 * @return 转义后的字节数组。
	 */
	public static byte[] transfer(byte[] src) {
		return transfer(src, 0, src.length);
	}
	/**
	 * 消息内容转义。
	 * @param src 包含要处理内容的字节数组。
	 * @param srcBegin 转义起始字节索引。
	 * @param len 要转义内容的字节长度。
	 * @return 转义后的字节数组。
	 */
	public static byte[] transfer(byte[] src, int srcBegin, int len) {
		int transCount = 0;		// 转义次数
		for(int i = srcBegin; i < srcBegin + len; i++) {
			if(src[i] == DELIMITER || src[i] == DELIMITER_TRANS)
				transCount++;
		}
		if(transCount == 0)
			return Arrays.copyOfRange(src, srcBegin, srcBegin + len);

		byte bytes[];		// 转义后的字节数组
		bytes = new byte[len + transCount];
		int outPos = 0;
		for(int i = srcBegin; i < srcBegin + len; i++) {
			if(src[i] == DELIMITER) {
				bytes[outPos] = DELIMITER_TRANS;
				bytes[outPos + 1] = 0x02;
				outPos += 2;
			}
			else if(src[i] == DELIMITER_TRANS) {
				bytes[outPos] = DELIMITER_TRANS;
				bytes[outPos + 1] = 0x01;
				outPos += 2;
			}
			else {
				bytes[outPos] = src[i];
				outPos++;
			}
		}
		return bytes;
	}

	/**
	 * 消息内容转义还原。
	 * @param src 包含要处理内容的字节数组。
	 * @return 转义还原后的字节数组。
	 */
	public static byte[] transferBack(byte[] src) {
		return transferBack(src, 0, src.length);
	}
	/**
	 * 消息内容转义还原。
	 * @param src 包含要处理内容的字节数组。
	 * @param srcBegin 转义还原起始字节索引。
	 * @param len 要转义还原内容的字节长度。
	 * @return 转义还原后的字节数组。
	 */
	public static byte[] transferBack(byte[] src, int srcBegin, int len) {
		int transCount = 0;		// 转义次数
		byte bytes[] = new byte[len];
		int outPos = 0;
		for(int i = srcBegin; i < srcBegin + len; i++) {
			if(src[i] == DELIMITER_TRANS) {
				if(src[i + 1] == 0x02) {
					bytes[outPos] = DELIMITER;
					i++;
					transCount++;
				}
				else if(src[i + 1] == 0x01) {
					bytes[outPos] = DELIMITER_TRANS;
					i++;
					transCount++;
				}
				else
					bytes[outPos] = src[i];
			}
			else {
				bytes[outPos] = src[i];
			}
			outPos++;
		}
		if(transCount == 0)
			return bytes;
		else
			return Arrays.copyOf(bytes, len - transCount);
	}

	/**
	 * 获取消息的最小(无封包、消息体为空)字节长度。
	 * 不包含消息头尾的分隔符。
	 * @return 消息的最小长度。
	 */
	public static int getMinSize() {
		return 9;
	}

	/**
	 * 获取消息的最小(无封包、消息体为空)字节长度。
	 * 包含消息头尾的分隔符。
	 * @return 消息的最小长度。
	 */
	public static int getMinSizeAll() {
		return 11;
	}

	public static void main(String args[]) {
		byte body[] = new byte[6];
		for(int i = 0; i < body.length; i++)
			body[i] = (byte) i;
		ByteOrder bo = ByteOrder.LITTLE_ENDIAN;
		Message mm = new Message();
		mm.setId(0x01);
		mm.setProp(0x02);
		mm.setPhoneno(0x03);
		mm.setSerialno(0x10);
		mm.wrapBody(body);
		mm.setBodyLength(body.length);
		mm.setChecksum(mm.calcChecksum());
		byte[] src = mm.getBytes(bo, true);
		Message mm1 = Message.create(bo, src, 1, src.length - 2);
		System.out.println(mm.equals(mm1));
	}

}
