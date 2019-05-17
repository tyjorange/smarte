package com.rogy.smarte.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */
public class PowerManagerUtil {
	
	public static String dateToString(Date time) {
		if (time == null)
			return null;
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.format(time);
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将Timestamp转换为String,并规范格式
	 * 
	 * @param time
	 * @return
	 */
	public static String timestampToString(Timestamp time) {
		if (time == null)
			return null;
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return sdf.format(time);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取指定月份的天数
	 * 
	 * @param time
	 *            yyyy-MM格式
	 * @return
	 */
	public static int getDaysFromTime(String time) {
		if (time == null || time.trim().isEmpty())
			return 0;
		try {
			int year = Integer.valueOf(time.split("-")[0]);
			int month = Integer.valueOf(time.split("-")[1]);
			boolean flag = ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0);
			switch (month) {
			case 2:
				if (flag)
					return 29;
				else
					return 28;
			case 4:
				return 30;
			case 6:
				return 30;
			case 9:
				return 30;
			case 11:
				return 30;
			default:
				return 31;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

}
