package com.rogy.smarte.util;

import java.util.Random;

public class Code {
	private static final byte[] CODE_KEY = {0x50, 0x51, 0x52, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x1a, 0x1b, 0x1c, 0x1d, 0x1e, 0x1f};
	private Code() {
	}
	
	/**
	 * 根据实际的产品编码，生成用于扫描添加设备的二维码的安装编码。
	 * 与getProductCode配对使用。
	 * @param productCode 产品编码。
	 * @return 对应的安装编码。null或空串表示失败。
	 */
	public static String getSetupCode(String productCode) {
		byte[] bsPC;
		bsPC = productCode.getBytes();
		if(bsPC == null || bsPC.length <= 0)
			return "";
		byte[] bsSC = Aes128.cfb8(CODE_KEY, 1, bsPC);
		if(bsSC == null || bsSC.length <= 0)
			return "";
		int length = bsSC.length;
		byte[] bsSCOut = new byte[length + 2];
		System.arraycopy(bsSC, 0, bsSCOut, 2, length);
		int sum = 0;
		for(byte b : bsSC) {
			sum += (b & 0xff);
		}
		bsSCOut[0] = (byte) (sum & 0xff);
		bsSCOut[1] = (byte) ((sum >> 8) & 0xff);
		return Base64.UrlBase64Encode(bsSCOut);
	}

	/**
	 * 根据用于扫描添加设备的二维码的安装编码，得到实际的产品编码。
	 * 与getSetupCode配对使用。
	 * @param setupCode 安装编码。
	 * @return 对应的产品编码。null或空串表示失败。
	 */
	public static String getProductCode(String setupCode) {
		byte[] bsSC = Base64.UrlBase64DecodeByteArray(setupCode);
		if(bsSC == null || bsSC.length <= 0)
			return "";
		int length = bsSC.length;
		if(length < 3)
			return "";
		int sum = 0;
		sum += bsSC[0] & 0xff;
		sum += ((bsSC[1] & 0xff) << 8);
		int s = 0;
		for(int i = 2; i < length; i++) {
			s += (bsSC[i] & 0xff);
		}
		if(sum != (s & 0xffff))
			return "";
		byte[] bsPC = Aes128.cfb8(CODE_KEY, 0, bsSC, 2, length - 2);
		return new String(bsPC);
	}
	
	/**
	 * 根据实际的十进制数字类型产品编码，生成用于扫描添加设备的二维码的安装编码。
	 * 与getDecProductCode配对使用。
	 * @param productCode 十进制数字类型产品编码。
	 * @return 对应的安装编码。null或空串表示失败。
	 */
	public static String getDecSetupCode(String productCode) {
		byte[] bsPC;
		bsPC = BCD8421.DecimalToBCD8421(productCode);
		if(bsPC == null || bsPC.length <= 0)
			return "";
		byte[] bsSC = Aes128.cfb8(CODE_KEY, 1, bsPC);
		if(bsSC == null || bsSC.length <= 0)
			return "";
		int length = bsSC.length;
		byte[] bsSCOut = new byte[length + 2];
		Random rdm = new Random();
		boolean left = rdm.nextBoolean();
		int offset = rdm.nextInt(7) + 1;
		int sum = 0;
		for(int i = 0; i < length; i++) {
			sum += (bsSCOut[i + 2] = Bit.CircleShiftLeftByte(bsSC[i], left ? offset : 8 - offset)) & 0xff;
		}
		bsSCOut[0] = (byte) (((offset + (left ? 8 : 0)) << 4) + ((sum >> 8) & 0x0f));
		bsSCOut[1] = (byte) (sum & 0xff);
		return Base64.UrlBase64Encode(bsSCOut);
	}

	/**
	 * 根据用于扫描添加设备的二维码的安装编码，得到实际的十进制数字类型产品编码。
	 * 与getDecSetupCode配对使用。
	 * @param setupCode 安装编码。
	 * @return 对应的十进制数字类型产品编码。null或空串表示失败。
	 */
	public static String getDecProductCode(String setupCode) {
		byte[] bsSC = Base64.UrlBase64DecodeByteArray(setupCode);
		if(bsSC == null || bsSC.length <= 0)
			return "";
		int length = bsSC.length;
		if(length < 3)
			return "";
		boolean left = (bsSC[0] & 0x80) > 0;
		int offset = (bsSC[0] >> 4) & 0x07;
		int sum = ((bsSC[0] & 0x0f) << 8) + (bsSC[1] & 0xff);
		int s = 0;
		byte b;
		for(int i = 2; i < length; i++) {
			b = bsSC[i];
			s += (b & 0xff);
			bsSC[i] = Bit.CircleShiftLeftByte(b, left ? 8 - offset : offset);
		}
		if(sum != (s & 0xfff))
			return "";
		byte[] bsPC = Aes128.cfb8(CODE_KEY, 0, bsSC, 2, length - 2);
		if(bsPC == null || bsPC.length <= 0)
			return "";
		return BCD8421.BCD8421ToDecimal(bsPC);
	}
	
	public static void main(String args[]) {
		String pc = "201811063333";
		String sc = Code.getDecSetupCode(pc);
		System.out.println(sc);
		String pc1 = Code.getDecProductCode(sc);
		System.out.println(pc1);
		System.out.println(pc.equals(pc1));
	}
}
