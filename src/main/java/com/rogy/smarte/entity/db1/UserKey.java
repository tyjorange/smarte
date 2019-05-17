package com.rogy.smarte.entity.db1;


import com.rogy.smarte.util.Base64;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;


public class UserKey {
	// 版本
	private static final byte VERSION_MAJOR = 1;
	private static final byte VERSION_MINOR = 1;
	// 数据类型
//	private static final byte DATATYPE_BYTE = 0;
//	private static final byte DATATYPE_SHORT = 1;
//	private static final byte DATATYPE_INT = 2;
	private static final byte DATATYPE_LONG = 3;
	private static final byte DATATYPE_STRING = 4;
	// 序列化字节顺序
	private static final ByteOrder BYTEORDER = ByteOrder.BIG_ENDIAN;
	// 字符集
	private static final Charset CHARSET = Charset.forName("UTF-8");

	private boolean valid = false;		// 是否有效
	private String userID;				// 用户ID
	private String userLoginID = "";	// 用户登录ID
	private String password;			// 密码
	private long timeMillis = 0;		// 用户登录的TimeMillis

	public UserKey() {
	}
	/**
	 * 从Base64字符串构造对象
	 * @param userKeyString 包含对象内容的Base64字符串
	 */
	public UserKey(String userKeyString) {
		if(userKeyString != null && !userKeyString.isEmpty()) {
			try{
//				String skey = Base64.UrlBase64Decode(userKeyString);
//				if(skey == null || skey.isEmpty())
//					return;
//				byte[] bytes;
//				bytes = skey.getBytes();
				byte[] bytes = Base64.UrlBase64DecodeByteArray(userKeyString);
				if(bytes == null || bytes.length <= 0)
					return;

				ByteBuffer buf = ByteBuffer.wrap(bytes);
				buf.order(BYTEORDER);
				byte dt;
				byte len;
				// Version
				dt = buf.get();
				if(dt != VERSION_MAJOR)
					return;
				dt = buf.get();
				if(dt != VERSION_MINOR)
					return;
				// userID
				dt = buf.get();
				if(dt != DATATYPE_STRING)
					return;
				len = buf.get();
				if(len < 0)
					return;
				else if(len > 0) {
					bytes = new byte[len];
					buf.get(bytes);
					userID = new String(bytes, CHARSET);
				}
				// userLoginID
				dt = buf.get();
				if(dt != DATATYPE_STRING)
					return;
				len = buf.get();
				if(len < 0)
					return;
				else if(len > 0) {
					bytes = new byte[len];
					buf.get(bytes);
					userLoginID = new String(bytes, CHARSET);
				}
				// password
				dt = buf.get();
				if(dt != DATATYPE_STRING)
					return;
				len = buf.get();
				if(len < 0)
					return;
				else if(len > 0) {
					bytes = new byte[len];
					buf.get(bytes);
					password = new String(bytes, CHARSET);
				}
				// timeMillis
				dt = buf.get();
				if(dt != DATATYPE_LONG)
					return;
				timeMillis = buf.getLong();

				valid = true;
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 对象是否有效
	 * @return 是否有效
	 */
	public boolean isValid() {
		return this.valid;
	}
	/**
	 * 设置对象的有效状态
	 * @param valid 有效状态
	 */
	public void Valid(boolean valid) {
		this.valid = valid;
	}
	/**
	 * 得到用户ID
	 * @return 用户ID
	 */
	public String getUserID() {
		return userID;
	}
	/**
	 * 设置用户ID
	 * @param userID 用户ID
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}

	/**
	 * 得到用户登录ID
	 * @return 用户登录ID
	 */
	public String getUserLoginID() {
		return userLoginID;
	}
	/**
	 * 设置用户登录ID
	 * @param userLoginID 用户登录ID
	 */
	public void setUserLoginID(String userLoginID) {
		this.userLoginID = userLoginID;
	}
	/**
	 * 得到用户登录密码
	 * @return 用户登录密码
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * 设置用户登录密码
	 * @param password 用户登录密码
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * 得到用户登录时的系统时间(毫秒)
	 * @return 用户登录时的系统时间(毫秒)
	 */
	public long getTimeMillis() {
		return timeMillis;
	}
	/**
	 * 设置用户登录时的系统时间(毫秒)
	 * @param timeMillis 用户登录时的系统时间(毫秒)
	 */
	public void setTimeMillis(long timeMillis) {
		this.timeMillis = timeMillis;
	}

	/**
	 * 得到对应该对象内容的base64字符串
	 * 转换前需要设置对象为有效
	 * @return 对应该对象内容的base64字符串。如果对象无效则返回null。
	 */
	public String toUserKeyString() {
		if(!valid)
			return null;

		byte len;
		int size = getSize();
		if(size <= 0)
			return null;
		ByteBuffer buf = ByteBuffer.allocate(size);
		buf.order(BYTEORDER);
		// Version
		buf.put(VERSION_MAJOR);
		buf.put(VERSION_MINOR);
		// userID
		buf.put(DATATYPE_STRING);
		len = (byte)(userID == null ? 0 : userID.getBytes(CHARSET).length);
		buf.put(len);
		if(len > 0)
			buf.put(userID.getBytes(CHARSET));
		else if(len < 0)
			return null;
		// userLoginID
		buf.put(DATATYPE_STRING);
		len = (byte)(userLoginID == null ? 0 : userLoginID.getBytes(CHARSET).length);
		buf.put(len);
		if(len > 0)
			buf.put(userLoginID.getBytes(CHARSET));
		else if(len < 0)
			return null;
		// password
		buf.put(DATATYPE_STRING);
		len = (byte)(password == null ? 0 : password.getBytes(CHARSET).length);
		buf.put(len);
		if(len > 0)
			buf.put(password.getBytes(CHARSET));
		else if(len < 0)
			return null;
		// timeMillis
		buf.put(DATATYPE_LONG);
		buf.putLong(timeMillis);

		// 转换
		byte[] bytes = buf.array();
		return Base64.UrlBase64Encode(bytes);
	}

	/**
	 * 获取对象内容的字节长度
	 * @return 对象内容的字节长度
	 */
	private int getSize() {
		return	1 + 1 +												// Version
				1 + 1 + (userID == null ? 0 : userID.getBytes().length) +	// userLoginID
//				1 + 1 + (phone == null ? 0 : phone.getBytes().length) +	// userLoginID
				1 + 1 + (userLoginID == null ? 0 : userLoginID.getBytes().length) +	// userLoginID
				1 + 1 + (password == null ? 0 : password.getBytes().length) +	// password
				1 + 8;												// TimeMillis
	}

	/**
	 * 判断对象内容是否相等
	 * 两个对象必须都有效
	 * @param key 要比较的对象
	 * @return 是否相等
	 */
	public boolean equals(UserKey key) {
		if(key == null)
			return false;

		return	(valid && key.valid) &&
				((userID == null && key.userID == null) || (userID != null && key.userID != null && userID.equals(key.userID))) &&
				((userLoginID == null && key.userLoginID == null) || (userLoginID != null && key.userLoginID != null && userLoginID.equals(key.userLoginID))) &&
//				((phone == null && key.phone == null) || (phone != null && key.phone != null && phone.equals(key.phone))) &&
				((password == null && key.password == null) || (password != null && key.password != null && password.equals(key.password))) &&
				(timeMillis == key.timeMillis);
	}
	@Override
	public String toString() {
		return "UserKey [valid=" + valid + ", userID=" + userID
				+ ", userLoginID=" + userLoginID + ", password=" + password
				+ ", timeMillis=" + timeMillis + "]";
	}

	public static void main(String args[]) {
//		String name = "长";
//		byte[] bs = name.getBytes(Charset.forName("utf-8"));
//		String bn = Base64.UrlBase64Encode(name, "utf-8");
//		String nname = Base64.UrlBase64Decode(bn, "utf-8");
//		byte[] bsn = nname.getBytes(Charset.forName("utf-8"));
//		System.out.printf("%s %s %s\n", name, nname, name.equals(nname));

		String key = "aPCC0CP-pmP4DcCfwvR-D7P-w7IhpmrAPtcfp7a-wmrfVvjhhaWlXWBeYZ4C0CPHDcwOwtVhVvjOPmcmVCP-D70fDCCmwcIvD7hvptwoa4aaaqem7TH8";
		UserKey userKey = new UserKey(key);
		System.out.println(userKey.valid);
	}
}
