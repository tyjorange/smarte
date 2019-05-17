package com.rogy.smarte.util;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Aes128 {
	private Aes128() {
	}

	/**
	 * AES128-ECB块加密解密。
	 * 
	 * @param key
	 *            128位长度密钥。
	 * @param mode
	 *            0=解密;1=加密。
	 * @param input
	 *            128位长度源数据数组。
	 * @return 128位长度结果byte数组;null表示失败。
	 */
	public static byte[] ecb(final byte[] key, int mode, final byte[] input) {
		if (mode != 0 && mode != 1)
			return null;

		try {
			SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(mode == 0 ? Cipher.DECRYPT_MODE : Cipher.ENCRYPT_MODE, secretKeySpec);
			// cipher.update(input);
			byte[] output = cipher.doFinal(input);
			return output;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * AES128-CFB8加密解密。
	 * 使用key做IV。
	 * 
	 * @param key
	 *            128位长度密钥。
	 * @param mode
	 *            0=解密;1=加密。
	 * @param input
	 *            源数据数组。
	 * @return 结果byte数组;null表示失败。
	 */
	public static byte[] cfb8(final byte[] key, int mode, final byte[] input) {
		return cfb8(key, mode, input, 0, input.length);
	}
	
	/**
	 * AES128-CFB8加密解密。
	 * 使用key做IV。
	 * 
	 * @param key
	 *            128位长度密钥。
	 * @param mode
	 *            0=解密;1=加密。
	 * @param input
	 *            源数据数组。
	 * @param start
	 *            源数据起始位置索引。
	 * @param length
	 *            源数据字节长度。
	 * @return 结果byte数组;null表示失败。
	 */
	public static byte[] cfb8(final byte[] key, int mode, final byte[] input, int start, int length) {
		if (mode != 0 && mode != 1)
			return null;
		
		byte c;
		byte[] iv = new byte[16];
		System.arraycopy(key, 0, iv, 0, 16);
		byte[] ov = new byte[17];
		//int length = input.length;
		byte[] output = new byte[length];
		int inputidx = start, outputidx = 0;
		
//		while( length-- )
		while( length-- > 0 )
		{
//			memcpy( ov, iv, 16 );
			System.arraycopy(iv, 0, ov, 0, 16);
//			mbedtls_aes_crypt_ecb( ctx, MBEDTLS_AES_ENCRYPT, iv, iv );
			iv = ecb(key, 1, iv);
//			
//			if( mode == MBEDTLS_AES_DECRYPT )
//				ov[16] = *input;
			if(mode == 0)
				ov[16] = input[inputidx];
//			
//			c = *output++ = (unsigned char)( iv[0] ^ *input++ );
			c = output[outputidx] = (byte)((iv[0] & 0xff) ^ (input[inputidx] & 0xff));
			outputidx++;
			inputidx++;
//			
//			if( mode == MBEDTLS_AES_ENCRYPT )
//			ov[16] = c;
			
			if(mode == 1)
				ov[16] = c;
//			
//			memcpy( iv, ov + 1, 16 );
			System.arraycopy(ov, 1, iv, 0, 16);
		}
		return output;
	}
	
	public static void main(String args[]) {
		byte key[] = new byte[] {0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01,0x01};
		byte c[] = new byte[] {0x00, 0x01, 0x02, 0x03};
		c = cfb8(key, 1, c);
		c = cfb8(key, 0, c);
		System.out.println("1");
	}
}
