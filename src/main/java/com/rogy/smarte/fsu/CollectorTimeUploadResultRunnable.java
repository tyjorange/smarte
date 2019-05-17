package com.rogy.smarte.fsu;


import com.rogy.smarte.entity.db1.CollectorTimerUpload;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;


/**
 * 后台线程，定时操作上传集中器失败(一段时间内无返回结果)时，重新尝试上传。
 */
public class CollectorTimeUploadResultRunnable implements Runnable {
	private static final int UPLOAD_RESPONSE_MIN = 1;	// 正常响应时间(分钟)

	private void uploadAgain(CollectorTimerUpload collectorTimerUpload) {
		try {
			int result = VirtualFsuController.setCollectorTimer(collectorTimerUpload);
			if (result >= 0) {
//				System.out.printf("[%s] Retry set collector(%s) timer ok. count=(%d).\n",
//						LocalDateTime.now().toString(), collectorTimerUpload.getCollector().getCode(), result);
			} else {
//				System.out.printf("[%s] Retry set collector(%s) timer failed. err=(%d).\n",
//						LocalDateTime.now().toString(), collectorTimerUpload.getCollector().getCode(), result);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			Thread.currentThread().setName("PM_CollectorTimeUploadResult");
			LocalDateTime now = LocalDateTime.now();
			List<CollectorTimerUpload> collectorTimerUploads =
					VirtualFsuUtil.virtualFsuService.findNoResultBefore(Timestamp.valueOf(now.minusMinutes(UPLOAD_RESPONSE_MIN)));	// 超过1分钟无返回结果
			collectorTimerUploads.stream().forEach(ctu -> uploadAgain(ctu));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
