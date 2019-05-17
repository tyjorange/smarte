package com.rogy.smarte.util;

public class Bit {
	private Bit() {
	}
	
	/**
	 * 对BYTE数值进行二进制位的循环左移。
	 * @param b BYTE数值。
	 * @param offset 循环移动位数。
	 * @return 循环移位后的数值。
	 */
	public static byte CircleShiftLeftByte(byte b, int offset) {
		offset %= 8;
		if(offset < 0)
			offset += 8;
		if(offset == 0)
			return b;
		return (byte)(((b & 0xff) << offset) + ((b & 0xff) >> (8 - offset)));
	}

	/**
	 * 对BYTE数值进行二进制位的循环右移。
	 * @param b BYTE数值。
	 * @param offset 循环移动位数。
	 * @return 循环移位后的数值。
	 */
	public static byte CircleShiftRightByte(byte b, int offset) {
		offset %= 8;
		if(offset < 0)
			offset += 8;
		if(offset == 0)
			return b;
		return (byte)(((b & 0xff) >> offset) + ((b & 0xff) << (8 - offset)));
	}

	public static void main(String args[]) {
		byte b = 0x73;
		System.out.printf("%s %s\n",
				Integer.toBinaryString(b & 0xff),
				Integer.toBinaryString(CircleShiftRightByte(b, 4) & 0xff));
	}
}
