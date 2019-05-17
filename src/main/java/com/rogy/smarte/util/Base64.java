package com.rogy.smarte.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class Base64 {
	private static byte Base64CharTable[] = {				// BASE64字符表
		'a', 'h', 'o', 'v', 'C', 'I', 'O', 'U', '0', '6',
		'b', 'i', 'p', 'w', 'D', 'J', 'P', 'V', '1', '7',
		'c', 'j', 'q', 'x', 'E', 'K', 'Q', 'W', '2', '8',
		'd', 'k', 'r', 'y', 'F', 'L', 'R', 'X', '3', '9',
		'e', 'l', 's', 'z', 'G', 'M', 'S', 'Y', '4', '-',
		'f', 'm', 't', 'A', 'H', 'N', 'T', 'Z', '5', '_',
		'g', 'n', 'u', 'B'
		};

	/**
	 * 对一字符串进行UrlBase编码。
	 * 必须和UrlBase64Decode配对使用。
	 * @param s 要编码的字符串。
	 * @param charsetName 字符串的字符集。
	 * @return UTF-8的Base64编码字符串。null表示失败。
	 */
	public static String UrlBase64Encode(String s, String charsetName)
	{
		if(s == null || s.isEmpty())
			return s;

		byte buf[] = null;
		try {
			buf = s.getBytes(charsetName);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return UrlBase64Encode(buf);
	}

	/**
	 * 对一字节数组进行UrlBase编码。
	 * 必须和UrlBase64Decode配对使用。
	 * @param buf 要编码的字节数组
	 * @return UTF-8的Base64编码的字符串。null表示失败。
	 */
	public static String UrlBase64Encode(byte[] buf)
	{
		if(buf == null || buf.length <= 0)
			return null;

		int len = buf.length;
		int outlen;
		if(len % 3 == 0)
			outlen = len * 8 / 6;
		else
			outlen = len * 8 / 6 + 1;
		byte outbuf[] = new byte[outlen];

		int outindex = 0;		// 当前输出字符的索引
		int remainbit = 8;		// 当前处理的字符还剩余几位没有输出
		byte charindex = 0;		// 转换后的6个二进制位组成的值
		int outbit = 0;			// outindex中已经包含了几个二进制位
		for(int i = 0; i < len; i++)
		{
			while(remainbit > 0)	// 当前字符的内容要全部处理完毕
			{
				if(remainbit + outbit < 6)	// 当前字符的剩余位数加上已有的输出位数不够6位
				{
					// 把当前字符的剩余位合并进来
					charindex = (byte) ((((charindex & 0xff) << remainbit) & 0xff) | (Base64.PickBitsFromChar(buf[i], 8 - remainbit ,7) & 0xff));
					outbit += remainbit;	// 已有位数增加
					remainbit = 8;			// 开始处理下一字符的内容
					if(i == len - 1)		// 已经是最后一个字符了
					{
						outbuf[outindex] = Base64.Base64CharTable[((charindex & 0xff) << (6 - outbit)) & 0xff];
					}
					break;
				}
				else
				{
					// 把当前字符中能够凑足6位的内容合并进来
					charindex = (byte) ((((charindex & 0xff) << (6 - outbit)) & 0xff) | (Base64.PickBitsFromChar(buf[i], 8 - remainbit, 8 - remainbit + 6 - outbit - 1) & 0xff));
					outbuf[outindex] = Base64.Base64CharTable[charindex];
					outindex++;		// 下一个输出字符
					remainbit -= 6 - outbit;	// 剩余位数减少
					charindex = 0;
					outbit = 0;
					if(remainbit == 0) {	// 本字符已经处理完毕
						remainbit = 8;		// 开始处理下一字符的内容
						break;
					}
				}
			}
		}

		return new String(outbuf, Charset.forName("UTF-8"));
	}

	/**
	 * 对一字符串进行UrlBase解码。
	 * 必须和UrlBase64Encode配对使用。
	 * @param b64s 要解码的UTF-8的Base64编码字符串。
	 * @return 解码结果字节数组。null表示失败。
	 */
	public static byte[] UrlBase64DecodeByteArray(String b64s)
	{
		if(b64s == null || b64s.isEmpty())
			return null;

		byte buf[] = b64s.getBytes(Charset.forName("UTF-8"));
		int len = buf.length;
		int outlen;
//		if(len % 4 == 0)
//			outlen = len * 6 / 8;
//		else
//			outlen = len * 6 / 8 + 1;
		outlen = len * 6 / 8;
		byte outbuf[] = new byte[outlen];

		int outindex = 0;		// 当前输出字符的索引
		int remainbit = 6;		// 当前处理的字符还剩余几位没有输出
		byte outchar = 0;		// 源字符
		int outbit = 0;			// outindex中已经包含了几个二进制位
		byte b64CharIndex;		// base64字符对应的索引
		for(int i = 0; i < len; i++)
		{
			b64CharIndex = Base64.UrlBase64CharIndex(buf[i]);
			while(remainbit > 0)	// 当前字符的内容要全部处理完毕
			{
				if(remainbit + outbit < 8)	// 当前字符的剩余位数加上已有的输出位数不够8位
				{
					// 把当前字符的剩余位合并进来
					outchar = (byte) ((((outchar & 0xff) << remainbit) & 0xff) | (Base64.PickBitsFromChar(b64CharIndex, 8 - remainbit ,7) & 0xff));
					outbit += remainbit;	// 已有位数增加
					remainbit = 6;			// 开始处理下一字符的内容
//					if(i == len - 1)		// 已经是最后一个字符了
//					{
//						outbuf[outindex] = outchar;
//					}
					break;
				}
				else
				{
					// 把当前字符中能够凑足8位的内容合并进来
					outchar = (byte) ((((outchar & 0xff) << (8 - outbit)) & 0xff) | (Base64.PickBitsFromChar(b64CharIndex, 8 - remainbit, 8 - remainbit + 8 - outbit - 1) & 0xff));
					outbuf[outindex] = outchar;
					outindex++;		// 下一个输出字符
					if(outindex >= outlen)
						break;	// 转换已经全部完成，剩余的位是Encode转换时填充进来的。
					remainbit -= 8 - outbit;	// 剩余位数减少
					outchar = 0; outbit = 0;
					if(remainbit == 0) {	// 本字符已经处理完毕
						remainbit = 6;		// 开始处理下一字符的内容
						break;
					}
				}
			}
		}

		return outbuf;
	}

	/**
	 * 对一字符串进行UrlBase解码。
	 * 必须和UrlBase64Encode配对使用。
	 * @param b64s 要解码的UTF-8的Base64编码字符串
	 * @param charsetName 结果字符串的字符集。
	 * @return 解码结果字符串。null表示失败。
	 */
	public static String UrlBase64Decode(String b64s, String charsetName)
	{
		byte buf[] = UrlBase64DecodeByteArray(b64s);
		if(buf == null)
			return null;
		else
			return new String(buf, Charset.forName("UTF-8"));
	}

	/**
	 * 从一个字符中提取指定的二进制位，得到一对应的字符
	 * 调用者需要保证startbit和endbit在0-7之间，并且endbit不小于startbit
	 * @param c 源字符
	 * @param startbit 起始位
	 * @param endbit 终止位(含该位的内容)
	 * @return 对应的字符
	 */
	private static byte PickBitsFromChar(byte c, int startbit, int endbit)
	{
		if(startbit < 0 || startbit > 7)
			return 0;
		if(endbit < 0 || endbit > 7)
			return 0;
		int pickbits = endbit - startbit + 1;
		if(pickbits < 1)
			return 0;
		if(startbit == 0)
			return (byte)(((c & 0xff) >> (8 - pickbits)) & 0xff);
		else
			return (byte)(((((c & 0xff) << startbit) & 0xff) >> (8 - pickbits)) & 0xff);
	}

	/**
	 * 得到UrlBase64字符对应的索引
	 * @param b64Char UrlBase64字符
	 * @return 对应的索引。如果是非UrlBase64字符，则返回0。
	 */
	private static byte UrlBase64CharIndex(byte b64Char)
	{
		for(int i = 0; i < Base64CharTable.length; i++)
		{
			if(Base64CharTable[i] == b64Char)
				return (byte)i;
		}
		return (byte)0;
	}

	public static void printBytes(String s) {
		byte[] bs = s.getBytes(Charset.forName("utf-8"));
		printBytes(bs);
	}
	public static void printBytes(byte[] bs) {
		for(byte b : bs) {
			System.out.printf("%02X ", b & 0xff);
		}
		System.out.println();
	}

	public static void main(String args[]) {
		String name = "长";
		printBytes(name);
		String bn = Base64.UrlBase64Encode(name, "utf-8");
		printBytes(bn);
		System.out.println(bn);
		String nname = Base64.UrlBase64Decode(bn, "utf-8");
		printBytes(nname);

		System.out.printf("%s %s %s\n", name, nname, name.equals(nname));
	}
}
