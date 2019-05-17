package com.rogy.smarte.fsu;


import com.rogy.smarte.entity.db1.Collector;
import com.rogy.smarte.entity.db1.CollectorTraffic;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class CollectorTrafficRunnable implements Runnable {
	private void traffic(CollectorInfo collectorInfo) {
		try {
			LocalDateTime now = LocalDateTime.now();
			CollectorTraffic collectorTraffic = new CollectorTraffic();
			Collector collector = new Collector();
			collector.setCollectorID(collectorInfo.getCollectID());
			collectorTraffic.setCollector(collector);
			collectorTraffic.setRwtime(Timestamp.valueOf(now));
			// 注意要调用getAndClearxxx()来获取本时间段统计值并归零。
			collectorTraffic.setByteread(collectorInfo.getAndClearByteRead());
			collectorTraffic.setBytewrite(collectorInfo.getAndClearByteWrite());
			collectorTraffic.setPacketread(collectorInfo.getAndClearPacketRead());
			collectorTraffic.setPacketreaderr(collectorInfo.getAndClearPacketReadErr());
			collectorTraffic.setPacketwrite(collectorInfo.getAndClearPacketWrite());
			collectorTraffic.setDuration(collectorInfo.getAndClearOnlineSecs());
			VirtualFsuUtil.virtualFsuService.addOrUpdateCollectorTraffic(collectorTraffic);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			Thread.currentThread().setName("PM_CollectorTraffic");
			VirtualFsuCollectorInfo.collectorInfos.entrySet().stream().forEach(e -> traffic(e.getValue()));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
