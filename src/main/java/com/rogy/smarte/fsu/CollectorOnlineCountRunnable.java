package com.rogy.smarte.fsu;


import com.rogy.smarte.entity.db1.CollectorOnlineCount;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CollectorOnlineCountRunnable implements Runnable {
	private LocalDate countDate = LocalDate.now();		// 统计日
	private int maxOnlineCount = 0;	// 当日的最大在线数。

	@Override
	public void run() {
		try {
			Thread.currentThread().setName("PM_CollectorOnlineCount");
			LocalDateTime now = LocalDateTime.now();	// 当前时刻
			LocalDate nowDate = now.toLocalDate(); 	// 当前日期
			int count = VirtualFsuCollectorInfo.getOnlineSize();
			if(nowDate.equals(countDate)) {	// 统计日没有变化
				if(count > maxOnlineCount)
					maxOnlineCount = count;
			} else {	// 统计日变化了
				countDate = nowDate;
				maxOnlineCount = count;
			}
			CollectorOnlineCount collectorOnlineCount = new CollectorOnlineCount();
			collectorOnlineCount.setId(0L);
			collectorOnlineCount.setOncount(count);
			collectorOnlineCount.setOntopcount(maxOnlineCount);
			collectorOnlineCount.setOntime(Timestamp.valueOf(now));
			VirtualFsuUtil.virtualFsuService.addOrUpdateCollectorOnlineCount(collectorOnlineCount);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
