package com.rogy.smarte.service.impl;

import com.rogy.smarte.entity.db1.Collector;
import com.rogy.smarte.repository.db1.CollectorDao;
import com.rogy.smarte.service.ICollectorService;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

@Service
public class CollectorServiceImpl implements ICollectorService {

    @Resource
    private CollectorDao collectorDao;

    @Override
    public int updateCollector(Collector c) {
        return collectorDao.saveAndFlush(c) == null ? 1 : 0;
    }

    @Override
    public Collector addOrUpdate(Collector collector) {
        if (collector == null) {
            return null;
        }
        Collector fs = collectorDao.saveAndFlush(collector);
        // 集中器信息修改，通知设备端
//		VirtualFsuController.setCollectorConfig(fs);
        return fs;
    }

    @Override
    public boolean delete(String ids) {
        if (ids == null || ids.isEmpty())
            return false;
        for (String id : ids.split(",")) {
        	List<Collector> cs = collectorDao.findByCollectorID(Integer.valueOf(id));
        	if(!cs.isEmpty())
        		collectorDao.delete(cs.get(0));
        }
        return true;
    }
}
