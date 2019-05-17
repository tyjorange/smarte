package com.rogy.smarte.util;

/**
 * BCD8421码类。
 */
public class BCD8421 {
	private BCD8421() {};
	
	/**
	 * BCD8421字节数组转换成对应的十进制数值字符串。
	 * 例如：输入长度2字节BCD8421码串（内容为0x12、0x34），输出"1234"。
	 * @param src BCD8421字节数组。
	 * @return 对应的十进制数值字符串。
	 */
	public static String BCD8421ToDecimal(byte[] src) {
		return BCD8421ToDecimal(src, 0, src.length);
	}
	/**
	 * BCD8421字节数组转换成对应的十进制数值字符串。
	 * 例如：输入长度2字节BCD8421码串（内容为0x12、0x34），输出"1234"。
	 * @param src BCD8421字节数组。
	 * @param srcBegin 起始字节索引。
	 * @param len BCD8421码字节长度。
	 * @return 对应的十进制数值字符串。
	 */
	public static String BCD8421ToDecimal(byte[] src, int srcBegin, int len) {
		byte[] dBytes = new byte[2 * len];
		for(int i = 0; i < len; i++) {
			dBytes[2 * i] = (byte) ('0' + ((src[i] & 0xF0) >> 4));
			dBytes[2 * i + 1] = (byte) ('0' + (src[i] & 0x0F));
		}
		return new String(dBytes);
	}
	
	/**
	 * 十进制数值字符串转换成对应的BCD8421字节数组。
	 * 例如：输入字符串"1234"，输出长度2字节BCD8421码串（内容为0x12、0x34）。
	 * @param src 十进制数值字符串。
	 * @return 对应的BCD8421字节数组。
	 */
	public static byte[] DecimalToBCD8421(String src) {
		int len = src.getBytes().length;
		if((len & 0x01) > 0)
			len++;
		byte[] dst = new byte[len / 2];
		DecimalToBCD8421(src, dst, 0);
		return dst;
	}
	/**
	 * 十进制数值字符串转换成对应的BCD8421字节数组。
	 * 例如：输入字符串"1234"，输出长度2字节BCD8421码串（内容为0x12、0x34）。
	 * @param src 十进制数值字符串。
	 * @param dst 输出到的字节数组。
	 * @param dstBegin 输出起始字节索引。
	 * @return 对应的BCD8421字节长度。
	 */
	public static int DecimalToBCD8421(String src, byte[] dst, int dstBegin) {
		byte[] bytes = src.getBytes();
		byte[] srcbytes = bytes;
		if((bytes.length & 0x01) == 1) {
			srcbytes = new byte[bytes.length + 1];
			srcbytes[0] = 0;
			System.arraycopy(bytes, 0, srcbytes, 1, bytes.length);
		}
		for(int i = 0; i < srcbytes.length / 2; i++) {
			dst[dstBegin + i] = (byte) (((srcbytes[2 * i] & 0xFF) - '0') << 4);
			dst[dstBegin + i] = (byte) ((dst[dstBegin + i] & 0xFF) | (((srcbytes[2 * i + 1] & 0xFF) - '0')));
		}
		return srcbytes.length / 2;
	}
	
	public static void main(String args[]) {
		String dt = "171231235959";
		byte[] bcd = DecimalToBCD8421(dt);
		String dt1 = BCD8421ToDecimal(bcd);
		System.out.println(dt.equals(dt1));
	}
}
