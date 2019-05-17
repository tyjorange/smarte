package com.rogy.smarte.fsu;

import com.rogy.smarte.fsu.message.Message;
import io.netty.buffer.ByteBuf;

import java.nio.ByteOrder;

public class MessageCodec {
	private MessageCodec() {
	}
	
	/**
	 * 打印ByteBuf内容。
	 * @param buf ByteBuf。
	 * @param start 起始索引。
	 * @param length 长度。
	 */
	public static final void printBuf(ByteBuf buf, int start, int length) {
		for(int i = start; i < start + length; i++) {
			System.out.printf("%02X ", buf.getByte(i));
		}
		System.out.println();
	}

	/**
	 * 打印ByteBuf内容。
	 * @param buf ByteBuf。
	 * @param start 起始索引。
	 * @param length 长度。
	 */
	public static final void printBufAsString(ByteBuf buf, int start, int length) {
		byte[] ba = new byte[length];
		buf.getBytes(start, ba, 0, length);
		String s = new String(ba);
		System.out.println(s);
	}
	
	/**
	 * 打印ByteBuf内容。
	 * @param buf ByteBuf。
	 * @param start 起始索引。
	 * @param length 长度。
	 */
	public static void printBufOrString(ByteBuf buf, int start, int length) {
		if(length > 2 &&
				(buf.getByte(start + length - 2) & 0xff) == 0x0D &&
				(buf.getByte(start + length - 1) & 0xff) == 0x0A) {
			MessageCodec.printBufAsString(buf, start, length - 2);
		} else {
			MessageCodec.printBuf(buf, start, length);
		}
	}
	
	/**
	 * 从ByteBuf中读取一个16位无符号整数WORD数值。
	 * 读取时会进行转义还原。
	 * @param in ByteBuf。
	 * @param bo 字节序。
	 * @return 16位无符号整数WORD数值。
	 * @throws Exception
	 */
	public static final int readWord(ByteBuf in, ByteOrder bo) throws Exception {
		int v = 0;
		byte b;
		for(int i = 0; i < 2; i++) {
			b = in.readByte();
			if(b == Message.DELIMITER_TRANS) {	// 转义
				switch(in.readByte()) {
				case 0x01:
					break;
				case 0x02:
					b = Message.DELIMITER;
					break;
				default:
					throw new IllegalArgumentException("Wrong trans.");
				}
			}
			if(bo == ByteOrder.BIG_ENDIAN) {	// BIG_ENDIAN
				v = (v << 8) + (b & 0xff);
			} else {	// LITTLE_ENDIAN
				v += (b & 0xff) << (8 * i);
			}
		}
		return v;
	}

	/**
	 * 从ByteBuf中读取一个32位无符号整数DWORD数值。
	 * 读取时会进行转义还原。
	 * @param in ByteBuf。
	 * @param bo 字节序。
	 * @return 32位无符号整数DWORD数值。
	 * @throws Exception
	 */
	public static final long readDWord(ByteBuf in, ByteOrder bo) throws Exception {
		long v = 0;
		byte b;
		for(int i = 0; i < 4; i++) {
			b = in.readByte();
			if(b == Message.DELIMITER_TRANS) {	// 转义
				switch(in.readByte()) {
				case 0x01:
					break;
				case 0x02:
					b = Message.DELIMITER;
					break;
				default:
					throw new IllegalArgumentException("Wrong trans.");
				}
			}
			if(bo == ByteOrder.BIG_ENDIAN) {	// BIG_ENDIAN
				v = (v << 8) + (b & 0xff);
			} else {	// LITTLE_ENDIAN
				v += (b & 0xff) << (8 * i);
			}
		}
		return v;
	}
	
	/**
	 * 从ByteBuf读取一个字节。
	 * 读取时会进行转义还原。
	 * @param in ByteBuf。
	 * @return 得到的byte。
	 * @throws Exception
	 */
	public static final byte readByte(ByteBuf in) throws Exception {
		byte b;
		b = in.readByte();
		if(b == Message.DELIMITER_TRANS) {	// 转义
			switch(in.readByte()) {
			case 0x01:
				break;
			case 0x02:
				b = Message.DELIMITER;
				break;
			default:
				throw new IllegalArgumentException("Wrong trans.");
			}
		}
		return b;
	}
	
	/**
	 * 从ByteBuf读取指定长度的字节。
	 * 读取时会进行转义还原。
	 * @param in ByteBuf。
	 * @param length 读取的字节数。
	 * @return 得到的byte数组。
	 * @throws Exception
	 */
	public static final byte[] readBytes(ByteBuf in, int length) throws Exception {
		byte[] bs = new byte[length];
		readBytes(in, length, bs, 0);
		return bs;
	}

	/**
	 * 从ByteBuf读取指定长度的字节。
	 * 读取时会进行转义还原。
	 * @param in ByteBuf。
	 * @param length 读取的字节数。
	 * @param out 保存读取内容的数组。
	 * @param start 保存的起始位置。
	 * @return 得到的byte数组。
	 * @throws Exception
	 */
	public static final void readBytes(ByteBuf in, int length, byte[] out, int start) throws Exception {
		byte b;
		for(int i = 0; i < length; i++) {
			b = in.readByte();
			if(b == Message.DELIMITER_TRANS) {	// 转义
				switch(in.readByte()) {
				case 0x01:
					break;
				case 0x02:
					b = Message.DELIMITER;
					break;
				default:
					throw new IllegalArgumentException("Wrong trans.");
				}
			}
			out[start + i] = b;
		}
	}

}
