package com.rogy.smarte.service.impl;

import com.rogy.smarte.entity.db1.Collector;
import com.rogy.smarte.entity.db1.Switch;
import com.rogy.smarte.fsu.VirtualFsuController;
import com.rogy.smarte.repository.db1.SwitchDao;
import com.rogy.smarte.service.ISwitchService;

import com.rogy.smarte.fsu.SwitchCache;
import com.rogy.smarte.fsu.VirtualFsuUtil;

import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

@Service
public class SwitchServiceImpl implements ISwitchService {

    @Resource
    private SwitchDao switchDao;

    @Override
    public Switch addOrUpdate(Switch swich) {
        if (swich == null) {
            return null;
        }
        Switch sw = switchDao.saveAndFlush(swich);
        Collector collector = sw.getCollector();
		// 修改断路器Cache信息
		SwitchCache sc = SwitchCache.newSwitchCacheFromSwitch(sw); 
		VirtualFsuUtil.SWITCHCACHE.put(sw.getCode(), sc);
        // 集中器信息修改，通知设备端
        VirtualFsuController.setCollectorConfig(collector);
        return sw;
    }

    @Override
    public boolean delete(String ids) {
        if (ids == null || ids.isEmpty())
            return false;
        for (String id : ids.split(",")) {
        	List<Switch> sws = switchDao.findBySwitchID(Integer.valueOf(id));
            if (!sws.isEmpty()) {
            	Switch sw = sws.get(0);
                Collector collector = sw.getCollector();
                switchDao.delete(sw);
				// 删除断路器Cache信息
				VirtualFsuUtil.SWITCHCACHE.remove(sw.getCode());
                // 集中器信息修改，通知设备端
                VirtualFsuController.setCollectorConfig(collector);
            }
        }
        return true;
    }
}
