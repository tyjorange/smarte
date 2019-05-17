package com.rogy.smarte.fsu.message;

public class MessageBodyCollectorControl {
	private byte cmd = 0;		// 指令
	
	public byte getCmd() {
		return cmd;
	}
	
	/**
	 * 清除命令。
	 */
	public void clearCmd() {
		cmd = 0;
	}
	
	/**
	 * 设置命令：恢复出厂设置。
	 */
	public void setCmdReset() {
		cmd = (byte) ((cmd & 0xFF) | 0x01);
	}
	
	/**
	 * 设置命令：重启。
	 */
	public void setCmdReboot() {
		cmd = (byte) ((cmd & 0xFF) | 0x80);
	}

	/**
	 * 得到消息体对象的字节数组。
	 * @return 字节数组。
	 */
	public final byte[] getBytes() {
		byte[] bytes = new byte[1];
		bytes[0] = cmd;
		return bytes;
	}
}
