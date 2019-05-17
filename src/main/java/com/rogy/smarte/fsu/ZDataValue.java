package com.rogy.smarte.fsu;

public class ZDataValue {
	// 采集数据。
	public volatile long newId = 0;		// 记录ID。<=0表示无记录。
	//public volatile LocalDateTime newTime = null;		// 数据时间。
	public volatile long newTimeMs;		// 数据时间(毫秒值）。
	public volatile double newValue;		// 数据数值。

	// (电量)分小时累计。
//	public volatile long dlId = 0;		// 记录ID。<=0表示无记录。
//	public volatile long dlTimeMs;		// 数据日期(毫秒值)。
//	public volatile double dlValue;		// 总累计值。
//	public volatile int dlHour;			// 小时。
//	public volatile double dlStatistik;	// 本小时累计值。

	// 每日最大最小值统计。
	public volatile long dayId = 0;		// 记录ID。<=0表示无记录。
	//public volatile LocalDate dayTime = null;
	public volatile long dayTimeMs;		// 数据日期(毫秒值）。
	public volatile double dayMax, dayMin;

	// 每月最大最小值统计。
	public volatile long monthId = 0;		// 记录ID。<=0表示无记录。
	public volatile int monthYear, monthMonth;
	public volatile double monthMax, monthMin;
}
