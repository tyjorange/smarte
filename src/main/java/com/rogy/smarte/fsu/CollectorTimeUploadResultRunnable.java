package com.rogy.smarte.fsu;


import com.rogy.smarte.entity.db1.CollectorTimerUpload;
import com.rogy.smarte.service.impl.VirtualFsuServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;


/**
 * 后台线程，定时操作上传集中器失败(一段时间内无返回结果)时，重新尝试上传。
 */
@Component
public class CollectorTimeUploadResultRunnable {
    final
    VirtualFsuServiceImpl virtualFsuService;
    private static final int UPLOAD_RESPONSE_MIN = 1;    // 正常响应时间(分钟)

    @Autowired
    public CollectorTimeUploadResultRunnable(VirtualFsuServiceImpl virtualFsuService) {
        this.virtualFsuService = virtualFsuService;
    }

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0/1 * * * ? ")
    public void doSchedule() {
        try {
            Thread.currentThread().setName("PM_CollectorTimeUploadResult");
            LocalDateTime now = LocalDateTime.now();
            List<CollectorTimerUpload> collectorTimerUploads =
                    virtualFsuService.findNoResultBefore(Timestamp.valueOf(now.minusMinutes(UPLOAD_RESPONSE_MIN)));    // 超过1分钟无返回结果
            collectorTimerUploads.forEach(this::uploadAgain);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
